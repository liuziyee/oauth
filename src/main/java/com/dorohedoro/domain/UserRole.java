package com.dorohedoro.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("users_roles")
public class UserRole implements Serializable {

    private Long userId;

    private Long roleId;
}
