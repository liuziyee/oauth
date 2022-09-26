package com.dorohedoro.domain.dto;

import com.dorohedoro.annotation.Password;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;

@Data
public class UserDTO implements Serializable {

    private Long id;
    @NotEmpty
    @Size(min = 5, max = 25, message = "用户名长度必须在5到25个字符之间")
    private String username;
    @NotEmpty
    @Password
    private String password;
    @NotEmpty
    @Password
    private String matchingPassword;
    @NotEmpty
    @Email
    private String email;
}
