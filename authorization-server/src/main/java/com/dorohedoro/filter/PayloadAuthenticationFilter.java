package com.dorohedoro.filter;

import com.alibaba.fastjson.JSON;
import com.dorohedoro.domain.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class PayloadAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authToken = null;
        try {
            UserDTO payload = JSON.parseObject(request.getInputStream(), UserDTO.class);
            
            authToken = new UsernamePasswordAuthenticationToken(payload.getUsername(), payload.getPassword());
            setDetails(request, authToken);
        } catch (IOException e) {
            log.info(e.getMessage(), e);
        } finally {
            return this.getAuthenticationManager().authenticate(authToken);
        }
    }
}
