package com.dorohedoro.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dorohedoro.deprecated.ldap.LDAPAuthenticationProvider;
import com.dorohedoro.deprecated.ldap.LDAPUserRepo;
import com.dorohedoro.filter.JwtFilter;
import com.dorohedoro.filter.PayloadAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.util.Arrays;
import java.util.Map;

@EnableWebSecurity(debug = true)
@Import(SecurityProblemSupport.class)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    private final UserDetailsService userDetailsService;
    private final UserDetailsPasswordService userDetailsPasswordService;
    private final SecurityProblemSupport securityProblemSupport;
    private final LDAPUserRepo ldapUserRepo;
    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        String defaultEncoderId = "bcrypt";
        Map<String, PasswordEncoder> encoderMap = Map.of(
                defaultEncoderId, new BCryptPasswordEncoder(),
                "SHA-1", new MessageDigestPasswordEncoder("SHA-1")
        );
        return new DelegatingPasswordEncoder(defaultEncoderId, encoderMap);
    }

    @Bean
    public PayloadAuthenticationFilter payloadAuthenticationFilter() throws Exception {
        PayloadAuthenticationFilter payloadAuthFilter = new PayloadAuthenticationFilter();
        payloadAuthFilter.setAuthenticationSuccessHandler((req, res, auth) -> {
            res.setStatus(HttpStatus.OK.value());
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write(JSON.toJSONString(auth));
        });
        payloadAuthFilter.setAuthenticationFailureHandler((req, res, exception) -> {
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            JSONObject data = new JSONObject();
            data.put("msg", "Unauthorized");
            data.put("detail", exception.getMessage());
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write(data.toJSONString());
        });
        payloadAuthFilter.setAuthenticationManager(authenticationManager());
        payloadAuthFilter.setFilterProcessesUrl("/authorize/login");
        return payloadAuthFilter;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthProvider = new DaoAuthenticationProvider();
        daoAuthProvider.setUserDetailsService(userDetailsService);
        daoAuthProvider.setUserDetailsPasswordService(userDetailsPasswordService);
        daoAuthProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthProvider;
    }

    @Bean
    LDAPAuthenticationProvider ldapAuthenticationProvider() {
        return new LDAPAuthenticationProvider(ldapUserRepo);
    }
    
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(
                "ROLE_ADMIN > ROLE_MANAGER\n" +
                "ROLE_MANAGER > ROLE_USER"); // 配置角色包含关系
        return roleHierarchy;
    }
    
    @Bean
    // 配置跨域
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "OPTIONS"));
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addExposedHeader("X-Authenticate"); // 暴露响应头
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return urlBasedCorsConfigurationSource;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .requestMatchers(configurer -> configurer.mvcMatchers("/api/**", "/admin/**", "/authorize/**"))
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(configurer -> configurer
                        .authenticationEntryPoint(securityProblemSupport)
                        .accessDeniedHandler(securityProblemSupport))
                .cors(configurer -> configurer.configurationSource(corsConfigurationSource()))
                // 配置URL级别的访问控制
                .authorizeRequests(registry -> registry
                        .antMatchers("/authorize/**").permitAll()
                        //.antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        .antMatchers("/admin/**").hasRole("ADMIN")
                        //.antMatchers("/api/greeting/{username}").access("hasRole('ADMIN') or @userServiceImpl.isYourself(authentication, #username)")
                        .antMatchers("/api/user/{email}").hasRole("MANAGER")
                        .antMatchers("/api/greeting/{username}").access("hasRole('ADMIN') or authentication.name.equals(#username)")
                        .antMatchers("/api/**").hasRole("USER")
                        .anyRequest().authenticated())
                // 替换过滤器
                //.addFilterAt(payloadAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // 加入过滤器
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults());
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth
                //.authenticationProvider(ldapAuthenticationProvider())
                .authenticationProvider(daoAuthenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/error/**"); // 绕开过滤器链
    }
}
