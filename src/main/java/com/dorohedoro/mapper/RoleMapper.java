package com.dorohedoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorohedoro.domain.Role;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleMapper extends BaseMapper<Role> {

    List<Role> selectByRolename(String rolename);

    Optional<Role> selectById(Long id);

    Set<Role> selectByIds(List<Long> ids);

    Page<Role> selectPage(Page<Role> page, Role role);

    Long countByRolename(String rolename);
}
