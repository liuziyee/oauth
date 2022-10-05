package com.dorohedoro.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorohedoro.domain.Permission;
import com.dorohedoro.domain.Role;

import java.util.List;
import java.util.Set;

public interface IRoleAdminService {

    Page<Role> getAll(Role role);

    Role createRole(Role role);

    Role updateRole(Long id, Role role);

    void deleteRole(Long id);

    Role updatePermissions(Long id, List<Long> permissionIds);

    Role get(Long id);

    Set<Permission> getAvailablePermissions(Long id);

    Boolean isRoleAssigned(Long id);

    Boolean isRolenameExist(String rolename);
}