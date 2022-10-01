package com.dorohedoro.controller;

import com.dorohedoro.domain.dto.Token;
import com.dorohedoro.domain.dto.UserDTO;
import com.dorohedoro.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authorize")
@RequiredArgsConstructor
public class AuthorizeController {

    private final IUserService userService;

    @PostMapping("/register")
    public UserDTO register(@RequestBody @Validated UserDTO userDTO) {
        return userDTO;
    }

    @PostMapping("/token")
    public Token login(@RequestBody UserDTO userDTO) {
        return userService.login(userDTO.getUsername(), userDTO.getPassword());
    }
}
