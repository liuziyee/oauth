package com.dorohedoro.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    @Getter
    @Setter
    private Jwt jwt = new Jwt();
    
    @Getter
    @Setter
    public static class Jwt {
        private String header = "Authorization";
        private String prefix = "Bearer";
        private Long accessTokenExpireTime = 60_000L; // 访问令牌过期时间
        private Long refreshTokenExpireTime = 30 * 24 * 3600 * 1000L; // 刷新令牌过期时间
    }
}
