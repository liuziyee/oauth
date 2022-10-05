package com.dorohedoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorohedoro.domain.Permission;

import java.security.Permissions;
import java.util.List;
import java.util.Set;

public interface PermissionMapper extends BaseMapper<Permissions> {

    Set<Permission> selectByIds(List<Long> ids);

    Set<Permission> selectByAuthority(String authority);
}
