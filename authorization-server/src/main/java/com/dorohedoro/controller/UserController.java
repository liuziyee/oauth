package com.dorohedoro.controller;

import com.dorohedoro.domain.User;
import com.dorohedoro.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("/greeting/{username}")
    public String greeting(@PathVariable String username) {
        return "hello " + username;
    }

    @PostMapping("/greeting")
    public String greeting(@RequestParam String no, @RequestBody User user) {
        return "Hello " + no + "\n" + user.getId();
    }

    @GetMapping("/principal")
    public Authentication principal() {
        return SecurityContextHolder.getContext().getAuthentication(); // 获取认证对象
    }
    
    @PreAuthorize("hasRole('USER')") // 方法前授权
    @PostAuthorize("authentication.name.equals(returnObject.username)") // 方法后授权
    @GetMapping("/user/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }
}
