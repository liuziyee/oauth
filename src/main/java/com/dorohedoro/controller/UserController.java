package com.dorohedoro.controller;

import com.dorohedoro.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/greeting")
    public String greeting() {
        return "Hello World";
    }

    @PostMapping("/greeting")
    public String greeting(@RequestParam String no, @RequestBody User user) {
        return "Hello " + no + "\n" + user.getId();
    }

    @PutMapping("/greeting/{no}")
    public String greeting(@PathVariable String no) {
        return "Hello " + no;
    }
    
    @GetMapping("/principal")
    public Authentication principal() {
        return SecurityContextHolder.getContext().getAuthentication(); // 获取认证对象
    }
}
