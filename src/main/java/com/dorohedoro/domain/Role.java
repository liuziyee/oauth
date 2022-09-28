package com.dorohedoro.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

@Data
@TableName("roles")
public class Role implements GrantedAuthority, Serializable {

    private Long id;

    @TableField("role_name")
    private String authority;
}
