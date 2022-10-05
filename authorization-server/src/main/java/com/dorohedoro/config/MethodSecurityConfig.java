package com.dorohedoro.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

// 启用方法级别的访问控制
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class MethodSecurityConfig {
}
