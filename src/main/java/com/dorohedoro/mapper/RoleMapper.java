package com.dorohedoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorohedoro.domain.Role;

import java.util.Set;

public interface RoleMapper extends BaseMapper<Role> {

    Set<Role> selectByUsername(String username);
}
