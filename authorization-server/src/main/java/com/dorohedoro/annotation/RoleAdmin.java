package com.dorohedoro.annotation;

import com.dorohedoro.config.Constants;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('" + Constants.AUTHORITY_ADMIN + "')")
public @interface RoleAdmin {
}
