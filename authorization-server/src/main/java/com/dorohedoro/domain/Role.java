package com.dorohedoro.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dorohedoro.domain.dto.PageBean;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@TableName("roles")
public class Role extends PageBean implements Serializable {

    @TableId
    private Long id;

    private String roleName;

    private String displayName;

    private Boolean builtIn;
    @JSONField(serialize = false)
    private Set<Permission> permissions = new HashSet<>(); // 这里为了规避空指针做了初始化
}
