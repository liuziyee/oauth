package com.dorohedoro.service.impl;

import com.dorohedoro.domain.Permission;
import com.dorohedoro.mapper.PermissionMapper;
import com.dorohedoro.service.IPermissionAdminService;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Data
@RequiredArgsConstructor
public class PermissionAdminServiceImpl implements IPermissionAdminService {

    private final PermissionMapper permissionMapper;
    
    @Override
    public Set<Permission> getAll() {
        return permissionMapper.selectByAuthority(null);
    }
}
