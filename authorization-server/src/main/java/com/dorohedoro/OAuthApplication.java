package com.dorohedoro;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dorohedoro.mapper")
public class OAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(OAuthApplication.class, args);
    }
}
