package com.dorohedoro.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dorohedoro.filter.FormAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public UsernamePasswordAuthenticationFilter formAuthenticationFilter() throws Exception {
        FormAuthenticationFilter filter = new FormAuthenticationFilter();
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
        http.authorizeRequests(registry -> registry.antMatchers("/authorize/**").permitAll() // 放行
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/api/**").hasRole("USER")
                .anyRequest().authenticated())
                .addFilterAt(formAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults())
                .csrf(configurer -> configurer.disable());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("root")
                .password(passwordEncoder().encode("dorohedoro1994"))
                .roles("USER", "ADMIN");
    }
}
