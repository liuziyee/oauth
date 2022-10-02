package com.dorohedoro.util;

import com.dorohedoro.config.AppProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

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

    // 刷新访问令牌(复用刷新令牌)
    public String refreshAccessToken(String refreshToken) {
        return parseJwtToken(refreshToken, refreshKey)
                .map(claims -> Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + appProperties.getJwt().getAccessTokenExpireTime()))
                        .signWith(accessKey)
                        .compact()
                )
                .orElseThrow(() -> new AccessDeniedException("访问被拒绝"));
    }

    // 校验访问令牌(不忽略令牌过期)
    public Boolean validateAccessToken(String token) {
        return validateJwtToken(token, accessKey, false);
    }

    // 校验访问令牌(忽略令牌过期)
    public Boolean validateAccessTokenIgnoreExpired(String token) {
        return validateJwtToken(token, accessKey, true);
    }

    // 校验刷新令牌(不忽略令牌过期)
    public Boolean validateRefreshToken(String token) {
        return validateJwtToken(token, refreshKey, false);
    }

    public String generateJwtToken(UserDetails userDetails, Long timeToExpire, Key key) {
        return Jwts.builder()
                .claim("authorities",
                        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList()))
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + timeToExpire))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    private Boolean validateJwtToken(String token, Key key, Boolean ignoreExpired) { // 忽略令牌过期
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            return ignoreExpired;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            return false;
        }
    }

    public Optional<Claims> parseJwtToken(String token, Key key) {
        try {
            return Optional.of(Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
