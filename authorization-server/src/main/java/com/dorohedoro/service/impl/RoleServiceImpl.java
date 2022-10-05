package com.dorohedoro.service.impl;

import com.dorohedoro.domain.Role;
import com.dorohedoro.mapper.RoleMapper;
import com.dorohedoro.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {

    private final RoleMapper roleMapper;

    @Override
    public String getRoleHierarchyExpr() {
        List<Role> roles = roleMapper.selectByRolename(null);
        if (CollectionUtils.isEmpty(roles)) return "";
        return roles.stream()
                .flatMap(role -> {
                    if (CollectionUtils.isEmpty(role.getPermissions())) {
                        return Stream.of(role.getRoleName() + " > " + "该角色未分配权限");
                    }
                    return role.getPermissions().stream()
                            .map(permission -> role.getRoleName() + " > " + permission.getAuthority());
                })
                .collect(joining(" ", "ROLE_ADMIN" + " > " + "ROLE_STAFF ", ""));
    }
}
