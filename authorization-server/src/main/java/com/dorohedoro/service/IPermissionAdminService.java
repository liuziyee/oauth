package com.dorohedoro.service;

import com.dorohedoro.domain.Permission;

import java.util.Set;

public interface IPermissionAdminService {

    Set<Permission> getAll();
}
