package com.dorohedoro.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dorohedoro.filter.PayloadAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
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
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.util.Map;

@EnableWebSecurity(debug = true)
@Import(SecurityProblemSupport.class)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    private final UserDetailsService userDetailsService;
    
    private final UserDetailsPasswordService userDetailsPasswordService;

    private final SecurityProblemSupport securityProblemSupport;

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
    public UsernamePasswordAuthenticationFilter payloadAuthenticationFilter() throws Exception {
        PayloadAuthenticationFilter filter = new PayloadAuthenticationFilter();
        filter.setAuthenticationSuccessHandler((req, res, auth) -> {
            res.setStatus(HttpStatus.OK.value());
            res.getWriter().write(JSON.toJSONString(auth));
        });
        filter.setAuthenticationFailureHandler((req, res, exception) -> {
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            JSONObject data = new JSONObject();
            data.put("msg", "Unauthorized");
            data.put("detail", exception.getMessage());
            res.getWriter().write(data.toJSONString());
        });
        filter.setAuthenticationManager(authenticationManager());
        filter.setFilterProcessesUrl("/authorize/login");
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .requestMatchers(configurer -> configurer.mvcMatchers("/api/**", "/admin/**", "/authorize/**"))
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(configurer -> configurer
                        .authenticationEntryPoint(securityProblemSupport)
                        .accessDeniedHandler(securityProblemSupport))
                .authorizeRequests(registry -> registry
                        .antMatchers("/authorize/**").permitAll()
                        .antMatchers("/admin/**").hasRole("ADMIN")
                        .antMatchers("/api/**").hasRole("USER")
                        .anyRequest().authenticated())
                .addFilterAt(payloadAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults());
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
        web.ignoring().antMatchers("/error/**"); // 绕开过滤器链
    }

}
