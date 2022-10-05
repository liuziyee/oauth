package com.dorohedoro.annotation;

import com.dorohedoro.config.Constants;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyAuthority('" + Constants.ROLE_ADMIN + "', '" + Constants.AUTHORITY_USER_UPDATE + "')") // 用户分配有管理员角色或有编辑用户信息权限
public @interface RoleAdminOrAuthorityUserUpdate {
}
