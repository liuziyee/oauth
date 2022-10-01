package com.dorohedoro.util;

import com.dorohedoro.config.AppProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final AppProperties appProperties;
    public static final Key accessKey = Keys.secretKeyFor(SignatureAlgorithm.HS512); // 用于访问令牌的签名密钥
    public static final Key refreshKey = Keys.secretKeyFor(SignatureAlgorithm.HS512); // 用于刷新令牌的签名密钥
    
    // 生成访问令牌
    public String generateAccessToken(UserDetails userDetails) {
        return generateJwtToken(userDetails, appProperties.getJwt().getAccessTokenExpireTime(), accessKey);
    }

    // 生成刷新令牌
    public String generateRefreshToken(UserDetails userDetails) {
        return generateJwtToken(userDetails, appProperties.getJwt().getRefreshTokenExpireTime(), refreshKey);
    }

    public String generateJwtToken(UserDetails userDetails, Long timeToExpire, Key key) {
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .claim("authorities",
                        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList()))
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + timeToExpire))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}
