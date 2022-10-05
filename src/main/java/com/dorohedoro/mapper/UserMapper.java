package com.dorohedoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorohedoro.domain.User;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

public interface UserMapper extends BaseMapper<User> {

    Optional<User> selectByUsername(String username);

    Optional<User> selectByEmail(String email);

    Page<User> selectPage(@Param("page") Page<User> page, @Param("user") User user);

    Long countByUsername(String username);

    Long countByEmail(String email);
    
    Long countByMobile(String mobile);
}
