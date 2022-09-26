package com.dorohedoro.controller;

import com.dorohedoro.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/greeting")
    public String greeting() {
        return "Hello World";
    }

    @PostMapping("/greeting")
    @ResponseStatus(HttpStatus.CREATED)
    public String greeting(@RequestParam String no, @RequestBody User user) {
        return "Hello " + no + "\n" + user.getId();
    }

    @PutMapping("/greeting/{no}")
    public String greeting(@PathVariable String no) {
        return "Hello " + no;
    }
}
