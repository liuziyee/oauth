package com.dorohedoro.service.impl;

import com.dorohedoro.domain.Permission;
import com.dorohedoro.mapper.PermissionMapper;
import com.dorohedoro.service.IPermissionAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermissionAdminServiceImpl implements IPermissionAdminService {

    private final PermissionMapper permissionMapper;
    
    @Override
    public Set<Permission> getAll() {
        return permissionMapper.selectByAuthority(null);
    }
}
