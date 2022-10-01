package com.dorohedoro.service.impl;

import com.dorohedoro.domain.dto.Token;
import com.dorohedoro.mapper.UserMapper;
import com.dorohedoro.service.IUserService;
import com.dorohedoro.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Token login(String username, String password) {
        return userMapper.selectByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword())) // 匹配密码
                .map(user -> new Token(
                        jwtUtil.generateAccessToken(user),
                        jwtUtil.generateRefreshToken(user)
                )) // 生成访问令牌和刷新令牌
                .orElseThrow(() -> new BadCredentialsException("[MYSQL] 用户名或密码错误"));
    }
}
