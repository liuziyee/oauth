package com.dorohedoro.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserDTO implements Serializable {

    private Long id;

    private String username;

    private String password;

    private String extraInfo;

    private Date createTime;

    private Date updateTime;

    private Integer deleted;
}
