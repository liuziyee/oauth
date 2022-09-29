package com.dorohedoro.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dorohedoro.domain.User;
import com.dorohedoro.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsPasswordServiceImpl implements UserDetailsPasswordService {

    private final UserMapper userMapper;
    
    @Override
    // 密码升级
    public UserDetails updatePassword(UserDetails userDetails, String newPassword) {
        User user = (User) userDetails;
        user.setPassword(newPassword);

        userMapper.update(user, Wrappers.<User>lambdaQuery().eq(User::getId, user.getId()));
        return user;
    }
}
