package com.dorohedoro.domain.dto;

import com.dorohedoro.domain.Permission;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class RoleDTO extends PageBean implements Serializable {

    private Long id;

    private String roleName;

    private String displayName;

    private Boolean builtIn;

    private Set<Permission> permissions;
}
