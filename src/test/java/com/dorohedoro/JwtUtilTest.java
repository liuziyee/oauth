package com.dorohedoro;

import com.dorohedoro.config.AppProperties;
import com.dorohedoro.domain.Role;
import com.dorohedoro.domain.User;
import com.dorohedoro.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class JwtUtilTest {

    private JwtUtil jwtUtil;
    
    @BeforeEach
    public void setup() {
        jwtUtil = new JwtUtil(new AppProperties());
    }
    
    @Test
    public void generateJwtToken() {
        Set<Role> authorities = Set.of(
                Role.builder().authority("ROLE_USER").build(),
                Role.builder().authority("ROLE_ADMIN").build());

        User user = User.builder().username("rabbit").authorities(authorities).build();

        String token = jwtUtil.generateAccessToken(user);
        Claims payload = Jwts.parserBuilder()
                .setSigningKey(jwtUtil.accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("rabbit", payload.getSubject());
        
    }
    
}
