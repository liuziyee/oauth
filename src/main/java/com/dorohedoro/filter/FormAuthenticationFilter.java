package com.dorohedoro.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
public class FormAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authToken = null;
        try {
            String payload = request.getReader().lines().collect(Collectors.joining());
            JSONObject jsonObject = JSON.parseObject(payload);
            String username = jsonObject.getString("username");
            String password = jsonObject.getString("password");
            log.info("payload: {}, username: {}, password: {}", payload, username, password);
            
            authToken = new UsernamePasswordAuthenticationToken(
                    username, password);
            setDetails(request, authToken);
        } catch (IOException e) {
            log.info(e.getMessage(), e);
        } finally {
            return this.getAuthenticationManager().authenticate(authToken);
        }
    }
}
