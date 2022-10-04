package com.dorohedoro.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.dorohedoro.config.Constants.AUTHORITY_USER_UPDATE;
import static com.dorohedoro.config.Constants.ROLE_ADMIN;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("authentication.name == #user.username or " +
        "hasAnyAuthority('" + ROLE_ADMIN + "', '" + AUTHORITY_USER_UPDATE + "')")
public @interface RoleAdminOrUserselfWithUserParam {
}
