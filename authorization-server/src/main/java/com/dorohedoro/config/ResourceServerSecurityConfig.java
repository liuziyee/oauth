package com.dorohedoro.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.dorohedoro.config.Constants.ROLE_ADMIN;
import static com.dorohedoro.config.Constants.SCOPE_PREFIX;
import static java.util.stream.Collectors.toList;

@Order(10)
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true) // 启用方法访问权限注解
@Import(SecurityProblemSupport.class)
@RequiredArgsConstructor
// 授权服务器做为资源服务器的安全配置
public class ResourceServerSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final UserDetailsPasswordService userDetailsPasswordService;
    private final SecurityProblemSupport securityProblemSupport;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    // JWT解码器(用于资源服务器验证访问令牌)
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build(); // 配置获取公钥的访问地址
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean(); // 这里是为了支持资源拥有者密码的授权方式
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        String defaultEncoderId = "bcrypt";
        Map<String, PasswordEncoder> encoderMap = Map.of(
                defaultEncoderId, new BCryptPasswordEncoder(),
                "SHA-1", new MessageDigestPasswordEncoder("SHA-1"),
                "noop", NoOpPasswordEncoder.getInstance()
        );
        return new DelegatingPasswordEncoder(defaultEncoderId, encoderMap);
    }

    /*@Bean
    public PayloadAuthenticationFilter payloadAuthenticationFilter() throws Exception {
        PayloadAuthenticationFilter payloadAuthenticationFilter = new PayloadAuthenticationFilter();
        payloadAuthenticationFilter.setAuthenticationSuccessHandler((req, res, auth) -> {
            res.setStatus(HttpStatus.OK.value());
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write(JSON.toJSONString(auth));
        });
        payloadAuthenticationFilter.setAuthenticationFailureHandler((req, res, exception) -> {
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            JSONObject data = new JSONObject();
            data.put("msg", "Unauthorized");
            data.put("detail", exception.getMessage());
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write(data.toJSONString());
        });
        payloadAuthenticationFilter.setAuthenticationManager(authenticationManager());
        payloadAuthenticationFilter.setFilterProcessesUrl("/authorize/login");
        return payloadAuthenticationFilter;
    }*/

    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(Constants.ROLE_HIERARCHY_INIT_EXPR);
        return roleHierarchy;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .requestMatchers(configurer -> configurer.mvcMatchers("/api/**", "/admin/**", "/authorize/**"))
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 禁用会话
                .exceptionHandling(configurer -> configurer
                        .authenticationEntryPoint(securityProblemSupport)
                        .accessDeniedHandler(securityProblemSupport))
                // 配置URL访问权限
                .authorizeRequests(registry -> registry
                        .mvcMatchers("/authorize/**").permitAll() // 公开访问
                        .mvcMatchers("/admin/**").hasAnyAuthority(ROLE_ADMIN, SCOPE_PREFIX + "user.admin", SCOPE_PREFIX + "client.admin")
                        //.mvcMatchers("/api/greeting/{username}").access("hasRole('ADMIN') or @userServiceImpl.isUserself(authentication, #username)")
                        .mvcMatchers("/api/user/{email}").hasRole("MANAGER")
                        .mvcMatchers("/api/greeting/{username}").access("hasRole('ADMIN') or authentication.name.equals(#username)")
                        .mvcMatchers("/api/**").hasRole("USER")
                        .anyRequest().authenticated())
                // 替换过滤器
                //.addFilterAt(payloadAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                //.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2ResourceServer(configurer -> configurer.jwt().jwtAuthenticationConverter(customJwtAuthenticationConverter())) // 配置自定义转换器(通过JWT构建认证对象)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .logout(AbstractHttpConfigurer::disable);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .userDetailsPasswordManager(userDetailsPasswordService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring() // 公开访问
                .mvcMatchers("/resources/**", "/static/**", "/public/**", "/error/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    private Converter<Jwt, AbstractAuthenticationToken> customJwtAuthenticationConverter() {
        return jwt -> {
            List<String> authorities = jwt.getClaimAsStringList("authorities");
            List<String> scopes = jwt.getClaimAsStringList("scope");
            // 组装授权用户权限和客户端权限
            List<SimpleGrantedAuthority> combinedAuthorities = Stream.concat(
                    authorities.stream(),
                    scopes.stream().map(scope -> SCOPE_PREFIX + scope))
                    .map(SimpleGrantedAuthority::new)
                    .collect(toList());
            String username = jwt.getClaimAsString("user_name");
            return new UsernamePasswordAuthenticationToken(username, null, combinedAuthorities);
        };
    }
}
