package com.dorohedoro.service.impl;

import com.dorohedoro.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;
    
    @Override
    // 获取用户信息(权限等)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userMapper.selectByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("未找到用户名为" + username + "的用户"));
    }
}
