package com.dorohedoro.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorohedoro.domain.Permission;
import com.dorohedoro.domain.Role;
import com.dorohedoro.domain.RolePermission;
import com.dorohedoro.domain.UserRole;
import com.dorohedoro.exception.DataConflictProblem;
import com.dorohedoro.exception.DuplicateProblem;
import com.dorohedoro.exception.InvalidParamProblem;
import com.dorohedoro.mapper.PermissionMapper;
import com.dorohedoro.mapper.RoleMapper;
import com.dorohedoro.mapper.RolePermissionMapper;
import com.dorohedoro.mapper.UserRoleMapper;
import com.dorohedoro.service.IRoleAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class RoleAdminServiceImpl implements IRoleAdminService {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final UserRoleMapper userRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;

    // TODO 刷新角色包含关系注解 预校验注解

    @Override
    public Page<Role> getAll(Role role) {
        Page page = new Page(role.getPage(), role.getSize());
        return roleMapper.selectPage(page, role);
    }

    @Override
    public Role createRole(Role role) {
        String roleName = role.getRoleName();
        if (isRolenameExist(roleName)) throw new DuplicateProblem("角色名" + roleName + "已存在");

        role.setRoleName(roleName.toUpperCase());
        roleMapper.insert(role);
        return role;
    }

    @Override
    public Role updateRole(Long id, Role role) {
        String roleName = role.getRoleName();
        if (isRolenameExist(roleName)) {
            throw new DuplicateProblem("角色名" + roleName + "已存在");
        }

        role.setRoleName(roleName.toUpperCase());
        roleMapper.update(role, Wrappers.<Role>lambdaQuery().eq(Role::getId, id));
        return role;
    }

    @Override
    public void deleteRole(Long id) {
        if (isRoleAssigned(id)) throw new DataConflictProblem("该角色已分配给用户");

        roleMapper.selectById(id)
                .map(role -> {
                    if (role.getBuiltIn()) throw new DataConflictProblem("该角色为内建角色");
                    roleMapper.deleteById(id);
                    return role;
                })
                .orElseThrow(() -> new InvalidParamProblem("角色ID无效"));
    }

    @Override
    // 更新角色分配的权限
    public Role updatePermissions(Long id, List<Long> permissionIds) {
        return roleMapper.selectById(id)
                .map(role -> {
                    rolePermissionMapper.delete(Wrappers.<RolePermission>lambdaQuery().eq(RolePermission::getRoleId, id)); // 清空旧的角色权限关系记录
                    Set<Permission> permissions = permissionMapper.selectByIds(permissionIds);
                    permissions.stream().map(permission -> {
                        RolePermission rolePermission = new RolePermission();
                        rolePermission.setRoleId(id);
                        rolePermission.setPermissionId(permission.getId());
                        rolePermissionMapper.insert(rolePermission);
                        return permission;
                    });
                    role.setPermissions(permissions);
                    return role;
                })
                .orElseThrow(() -> new InvalidParamProblem("角色ID无效"));
    }

    @Override
    public Role get(Long id) {
        return roleMapper.selectById(id).orElseThrow(() -> new InvalidParamProblem("角色ID无效"));
    }

    @Override
    // 获取可用权限(即当前角色未分配的权限)
    public Set<Permission> getAvailablePermissions(Long id) {
        return roleMapper.selectById(id)
                .map(role -> {
                    Set<Permission> assignedPermissions = role.getPermissions();
                    return permissionMapper.selectByAuthority(null).stream()
                            .filter(permission -> !assignedPermissions.contains(permission))
                            .collect(toSet());
                })
                .orElseThrow(() -> new InvalidParamProblem("角色ID无效"));
    }

    @Override
    // 角色是否已分配给用户
    public Boolean isRoleAssigned(Long id) {
        return userRoleMapper.selectCount(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getRoleId, id)) > 0;
    }

    @Override
    public Boolean isRolenameExist(String rolename) {
        return roleMapper.countByRolename(rolename) > 0;
    }
}
