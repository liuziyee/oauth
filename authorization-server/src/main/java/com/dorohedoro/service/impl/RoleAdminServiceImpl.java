package com.dorohedoro.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorohedoro.annotation.RefreshRoleHierarchy;
import com.dorohedoro.annotation.RoleAdmin;
import com.dorohedoro.annotation.RoleAdminOrAuthorityUserRead;
import com.dorohedoro.domain.Permission;
import com.dorohedoro.domain.Role;
import com.dorohedoro.domain.RolePermission;
import com.dorohedoro.domain.UserRole;
import com.dorohedoro.problem.DataConflictProblem;
import com.dorohedoro.problem.DataDuplicateProblem;
import com.dorohedoro.problem.InvalidParamProblem;
import com.dorohedoro.mapper.PermissionMapper;
import com.dorohedoro.mapper.RoleMapper;
import com.dorohedoro.mapper.RolePermissionMapper;
import com.dorohedoro.mapper.UserRoleMapper;
import com.dorohedoro.service.IRoleAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.dorohedoro.config.Constants.PAGE_OFFSET;
import static com.dorohedoro.config.Constants.PAGE_SIZE;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class RoleAdminServiceImpl implements IRoleAdminService {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final UserRoleMapper userRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;

    @Override
    @RefreshRoleHierarchy // 刷新角色包含关系
    @RoleAdmin // 自定义预授权注解
    public Page<Role> getAll(Role role) {
        Page page = new Page(role.getPage() + PAGE_OFFSET, PAGE_SIZE);
        return roleMapper.selectPage(page, role);
    }

    @Override
    public Role createRole(Role role) {
        String roleName = role.getRoleName();
        if (isRolenameExist(roleName)) throw new DataDuplicateProblem("角色名" + roleName + "已存在");

        role.setRoleName(roleName.toUpperCase());
        roleMapper.insert(role);
        return role;
    }

    @Override
    @RefreshRoleHierarchy
    @RoleAdmin
    public Role updateRole(Long id, Role role) {
        String roleName = role.getRoleName();
        if (isRolenameExist(roleName)) {
            throw new DataDuplicateProblem("角色名" + roleName + "已存在");
        }

        role.setRoleName(roleName.toUpperCase());
        roleMapper.update(role, Wrappers.<Role>lambdaQuery().eq(Role::getId, id));
        return role;
    }

    @Override
    @RefreshRoleHierarchy
    @RoleAdmin
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
    @RefreshRoleHierarchy
    @RoleAdmin
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
    @RoleAdminOrAuthorityUserRead
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
