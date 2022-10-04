package com.dorohedoro.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorohedoro.domain.Role;
import com.dorohedoro.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IUserAdminService {

    Page<User> getAll(User user);

    Optional<User> getByUsername(String username);

    User create(User user);

    User assignRoles(String username, List<Long> roleIds);

    User toggleEnabled(String username);

    Set<Role> getAvailableRoles(String username);
    
    // TODO 参考源码 toggleAccountNonExpired() toggleAccountNonLocked() toggleCredentialsNonExpired() generatePassword()
}
