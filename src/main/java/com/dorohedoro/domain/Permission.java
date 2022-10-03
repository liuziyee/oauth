package com.dorohedoro.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

@Data
@TableName("permissions")
public class Permission implements GrantedAuthority, Serializable {

    @TableId
    private Long id;
    @TableField("permission_name")
    private String authority;

    private String displayName;
}
