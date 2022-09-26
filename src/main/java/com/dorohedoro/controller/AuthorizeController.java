package com.dorohedoro.controller;

import com.dorohedoro.domain.dto.UserDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authorize")
public class AuthorizeController {

    @PostMapping("/register")
    public UserDTO register(@RequestBody @Validated UserDTO userDTO) {
        return userDTO;
    }
}
