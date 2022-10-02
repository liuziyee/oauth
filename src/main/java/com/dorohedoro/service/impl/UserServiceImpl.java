package com.dorohedoro.service.impl;

import com.dorohedoro.config.Constants;
import com.dorohedoro.domain.Role;
import com.dorohedoro.domain.User;
import com.dorohedoro.domain.UserRole;
import com.dorohedoro.domain.dto.Token;
import com.dorohedoro.mapper.RoleMapper;
import com.dorohedoro.mapper.UserMapper;
import com.dorohedoro.mapper.UserRoleMapper;
import com.dorohedoro.service.IUserService;
import com.dorohedoro.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Token login(String username, String password) {
        return userMapper.selectByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword())) // 匹配密码
                .map(user -> new Token(
                        jwtUtil.generateAccessToken(user),
                        jwtUtil.generateRefreshToken(user)
                )) // 生成访问令牌和刷新令牌
                .orElseThrow(() -> new BadCredentialsException("用户名或密码错误"));
    }

    @Override
    public Token refreshToken(String accessToken, String refreshToken) throws AccessDeniedException {
        if (jwtUtil.validateAccessTokenIgnoreExpired(accessToken) && jwtUtil.validateRefreshToken(refreshToken)) {
            // 访问令牌合法(忽略过期) 且 刷新令牌合法(不忽略过期)
            return new Token(jwtUtil.refreshAccessToken(refreshToken), refreshToken);
        }
        throw new AccessDeniedException("访问被拒绝");
    }

    @Override
    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
        
        Role defaultRole = roleMapper.selectByAuthority(Constants.ROLE_USER);
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(defaultRole.getId());
        userRoleMapper.insert(userRole);
    }

    @Override
    public Boolean isUsernameExist(String username) {
        return userMapper.countByUsername(username) > 0;
    }

    @Override
    public Boolean isEmailExist(String email) {
        return userMapper.countByEmail(email) > 0;
    }

    @Override
    public Boolean isMobileExist(String mobile) {
        return userMapper.countByMobile(mobile) > 0;
    }
}
