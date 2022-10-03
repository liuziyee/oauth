package com.dorohedoro.controller;

import com.dorohedoro.config.AppProperties;
import com.dorohedoro.domain.User;
import com.dorohedoro.domain.dto.Token;
import com.dorohedoro.domain.dto.UserDTO;
import com.dorohedoro.exception.DuplicateProblem;
import com.dorohedoro.service.IUserService;
import com.dorohedoro.util.BeanUtil;
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
    public void register(@RequestBody @Validated UserDTO userDTO) {
        if (userService.isUsernameExist(userDTO.getUsername())) {
            throw new DuplicateProblem("用户名重复");
        }

        if (userService.isMobileExist(userDTO.getMobile())) {
            throw new DuplicateProblem("手机号重复");
        }

        if (userService.isEmailExist(userDTO.getEmail())) {
            throw new DuplicateProblem("电子邮件地址重复");
        }
        
        User user = BeanUtil.copy(userDTO, User.class);
        userService.register(user);
    }

    @PostMapping("/token")
    public Token token(@RequestBody UserDTO userDTO) {
        return userService.login(userDTO.getUsername(), userDTO.getPassword());
    }
    
    @GetMapping("/refresh")
    public Token refreshToken(@RequestHeader("authorization") String authorization,
                              @RequestParam String refreshToken) throws AccessDeniedException {
        String accessToken = authorization.replace(appProperties.getJwt().getPrefix(), "").trim();
        return userService.refreshToken(accessToken, refreshToken);
    }
}
