package com.dorohedoro.advice;

import com.dorohedoro.service.IRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RoleHierarchyRefreshAspect {

    private final IRoleService roleService;
    private final RoleHierarchyImpl roleHierarchy;
    
    @Pointcut("@annotation(com.dorohedoro.annotation.RefreshRoleHierarchy)")
    public void pointcut() {}
    
    @AfterReturning("pointcut()")
    public void refreshRoleHierarchy() {
        roleHierarchy.setHierarchy(roleService.getRoleHierarchyExpr());
        log.info("角色包含关系已刷新");
    }
}
