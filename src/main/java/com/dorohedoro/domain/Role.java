package com.dorohedoro.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
@TableName("roles")
public class Role implements Serializable {

    @TableId
    private Long id;
    
    private String roleName;

    private String displayName;

    private Boolean builtIn;
    @JSONField(serialize = false)
    private Set<Permission> permissions; // 权限集合
}
