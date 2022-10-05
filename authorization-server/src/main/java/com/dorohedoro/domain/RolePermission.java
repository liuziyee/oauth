package com.dorohedoro.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("roles_permissions")
public class RolePermission implements Serializable {

    private Long roleId;

    private Long permissionId;
}
