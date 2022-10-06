package com.dorohedoro.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;

@Order(1)
@Configuration
public class JwkSetEndpointConfig extends AuthorizationServerSecurityConfiguration {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .requestMatchers(configurer -> configurer.mvcMatchers("/.well-known/jwks.json"))
                .authorizeRequests(registry -> registry.antMatchers("/.well-known/jwks.json").permitAll()); // 公开访问获取公钥端点
    }
}
