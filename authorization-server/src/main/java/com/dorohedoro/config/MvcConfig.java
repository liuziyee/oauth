package com.dorohedoro.config;

import lombok.RequiredArgsConstructor;
import org.passay.MessageResolver;
import org.passay.spring.SpringMessageResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(AppProperties.class)
public class MvcConfig implements WebMvcConfigurer {

    private final MessageSource messageSource;
    
    @Bean
    public MessageResolver messageResolver() {
        return new SpringMessageResolver(messageSource);
    }
}
