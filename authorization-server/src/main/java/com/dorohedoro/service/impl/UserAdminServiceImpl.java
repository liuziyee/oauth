package com.dorohedoro.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorohedoro.annotation.RoleAdminAndNotUserselfWithUsernameParam;
import com.dorohedoro.annotation.RoleAdminOrAuthorityUserCreate;
import com.dorohedoro.annotation.RoleAdminOrAuthorityUserRead;
import com.dorohedoro.annotation.RoleAdminOrAuthorityUserUpdate;
import com.dorohedoro.domain.Role;
import com.dorohedoro.domain.User;
import com.dorohedoro.domain.UserRole;
import com.dorohedoro.problem.InvalidParamProblem;
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

import static com.dorohedoro.config.Constants.*;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements IUserAdminService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @RoleAdminOrAuthorityUserRead
    public Page<User> getAll(User user) {
        Page<User> page = new Page(user.getPage() + PAGE_OFFSET, PAGE_SIZE);
        return userMapper.selectPage(page, user);
    }

    @Override
    @RoleAdminOrAuthorityUserRead
    public Optional<User> getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    @RoleAdminOrAuthorityUserCreate
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
    @RoleAdminOrAuthorityUserUpdate
    public User update(String username, User user) {
        return userMapper.selectByUsername(username)
                .map(toSave -> {
                    toSave.setUsername(user.getUsername());
                    toSave.setEmail(user.getEmail());
                    toSave.setMobile(user.getMobile());
                    userMapper.insert(toSave);
                    return toSave;
                })
                .orElseThrow(() -> new InvalidParamProblem("用户名" + username + "不存在"));
    }

    @Override
    @RoleAdminOrAuthorityUserUpdate
    // 分配角色给用户
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
    @RoleAdminAndNotUserselfWithUsernameParam
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
    @RoleAdminOrAuthorityUserRead
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
