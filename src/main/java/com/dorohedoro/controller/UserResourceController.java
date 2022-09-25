package com.dorohedoro.controller;

import com.dorohedoro.dto.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserResourceController {

    @GetMapping("/greeting")
    public String greeting() {
        return "Hello World";
    }

    @PostMapping("/greeting")
    @ResponseStatus(HttpStatus.CREATED)
    public String greeting(@RequestParam String no, @RequestBody UserDTO userDTO) {
        return "Hello " + no + "\n" + userDTO.getId();
    }

    @PutMapping("/greeting/{no}")
    public String greeting(@PathVariable String no) {
        return "Hello " + no;
    }
}
