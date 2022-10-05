package com.dorohedoro.filter;

import com.dorohedoro.config.AppProperties;
import com.dorohedoro.util.CollectionUtil;
import com.dorohedoro.util.JwtUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final AppProperties appProperties;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String header = request.getHeader(appProperties.getJwt().getHeader());
        if (header != null && header.startsWith(appProperties.getJwt().getPrefix())) {
            String token = header.replace(appProperties.getJwt().getPrefix(), "").trim();
            validateToken(token).filter(claims -> claims.get("authorities") != null)
                    .ifPresentOrElse(this::buildAuthentication, SecurityContextHolder::clearContext);
        }
        filterChain.doFilter(request, response);
    }

    // 构建认证对象放入上下文
    private void buildAuthentication(Claims claims) {
        List<?> raws = CollectionUtil.convertObjectToList(claims.get("authorities"));
        List<SimpleGrantedAuthority> authorities = raws.stream()
                .map(String::valueOf)
                .map(SimpleGrantedAuthority::new)
                .collect(toList());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private Optional<Claims> validateToken(String token) {
        try {
            return Optional.of(Jwts.parserBuilder().setSigningKey(JwtUtil.accessKey).build().parseClaimsJws(token).getBody());
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }
}
