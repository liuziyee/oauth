package com.dorohedoro.service.impl;

import com.dorohedoro.domain.Role;
import com.dorohedoro.domain.User;
import com.dorohedoro.mapper.RoleMapper;
import com.dorohedoro.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    
    @Override
    // 获取用户信息(权限等)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("[MYSQL] 未找到用户名为" + username + "的用户"));
        Set<Role> authorities = roleMapper.selectByUsername(username);
        user.setAuthorities(authorities);
        return user;
    }
}
