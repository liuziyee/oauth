package com.dorohedoro.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorohedoro.domain.Role;
import com.dorohedoro.domain.User;
import com.dorohedoro.domain.UserRole;
import com.dorohedoro.exception.InvalidParamProblem;
import com.dorohedoro.mapper.RoleMapper;
import com.dorohedoro.mapper.UserMapper;
import com.dorohedoro.mapper.UserRoleMapper;
import com.dorohedoro.service.IUserAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.dorohedoro.config.Constants.ROLE_STAFF;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class UserAdminService implements IUserAdminService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    // TODO 自定义预校验注解
    public Page<User> getAll(User user) {
        Page<User> page = new Page(user.getPage(), user.getSize());
        return userMapper.selectPage(page, user);
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User create(User user) {
        Role staffRole = roleMapper.selectByRolename(ROLE_STAFF).get(0);
        user.setPassword(passwordEncoder.encode("12345")); // TODO 密码工具类
        userMapper.insert(user);

        UserRole userToRole = new UserRole();
        userToRole.setUserId(user.getId());
        userToRole.setRoleId(staffRole.getId());
        userRoleMapper.insert(userToRole);

        return user;
    }

    @Override
    public User assignRoles(String username, List<Long> roleIds) {
        Set<Role> roles = roleMapper.selectByIds(roleIds);
        return userMapper.selectByUsername(username)
                .map(user -> {
                    roles.stream()
                            .map(role -> {
                                UserRole userToRole = new UserRole();
                                userToRole.setUserId(user.getId());
                                userToRole.setUserId(role.getId());
                                userRoleMapper.insert(userToRole);
                                return role;
                            });
                    user.setRoles(roles);
                    return user;
                })
                .orElseThrow(() -> new InvalidParamProblem("用户名" + username + "不存在"));
    }

    @Override
    public User toggleEnabled(String username) {
        return userMapper.selectByUsername(username)
                .map(user -> {
                    user.setEnabled(!user.isEnabled());
                    userMapper.insert(user);
                    return user;
                })
                .orElseThrow(() -> new InvalidParamProblem("用户名" + username + " 不存在"));
    }

    @Override
    // 获取用户的可分配角色列表
    public Set<Role> getAvailableRoles(String username) {
        return userMapper.selectByUsername(username)
                .map(user -> {
                    Set<Role> assignedRoles = user.getRoles();
                    return roleMapper.selectByRolename(null).stream().filter(role -> !assignedRoles.contains(role)).collect(toSet());
                })
                .orElseThrow(() -> new InvalidParamProblem("用户名" + username + " 不存在"));
    }
}
