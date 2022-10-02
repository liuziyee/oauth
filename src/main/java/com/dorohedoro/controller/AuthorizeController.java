package com.dorohedoro.controller;

import com.dorohedoro.config.AppProperties;
import com.dorohedoro.domain.dto.Token;
import com.dorohedoro.domain.dto.UserDTO;
import com.dorohedoro.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/authorize")
@RequiredArgsConstructor
public class AuthorizeController {

    private final IUserService userService;
    private final AppProperties appProperties;

    @PostMapping("/register")
    public UserDTO register(@RequestBody @Validated UserDTO userDTO) {
        return userDTO;
    }

    @PostMapping("/login")
    public Token login(@RequestBody UserDTO userDTO) {
        return userService.login(userDTO.getUsername(), userDTO.getPassword());
    }
    
    @GetMapping("/refresh")
    public Token refreshToken(@RequestHeader("authorization") String authorization,
                              @RequestParam String refreshToken) throws AccessDeniedException {
        String accessToken = authorization.replace(appProperties.getJwt().getPrefix(), "").trim();
        return userService.refreshToken(accessToken, refreshToken);
    }
}
