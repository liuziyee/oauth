package com.dorohedoro.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Order(100)
@Configuration
// 登录页(授权服务器提供)的安全配置
public class LoginSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 登录页不禁用会话
                .formLogin(configurer -> configurer.loginPage("/login").permitAll())
                .logout(configurer -> configurer.logoutUrl("/perform_logout").logoutSuccessUrl("/").permitAll())
                .rememberMe(configurer -> configurer.key("someSecret").tokenValiditySeconds(86400))
                .authorizeRequests(configurer -> configurer.anyRequest().authenticated());
    }
}
