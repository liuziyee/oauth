package com.dorohedoro.controller;

import com.dorohedoro.domain.User;
import com.dorohedoro.domain.dto.UserDTO;
import com.dorohedoro.exception.InvalidParamProblem;
import com.dorohedoro.service.IPermissionAdminService;
import com.dorohedoro.service.IRoleAdminService;
import com.dorohedoro.service.IUserAdminService;
import com.dorohedoro.service.IUserService;
import com.dorohedoro.util.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IUserAdminService userAdminService;
    private final IUserService userService;
    private final IRoleAdminService roleAdminService;
    private final IPermissionAdminService permissionAdminService;

    // TODO 看看前端的代码
    @GetMapping("/users")
    public void getAllUsers(@RequestBody UserDTO userDTO) {
        userAdminService.getAll(BeanUtil.copy(userDTO, User.class))
                .getRecords()
                .stream()
                .map(user -> BeanUtil.copy(user, UserDTO.class));
    }

    @GetMapping("/users/{username}")
    public UserDTO getUserByUsername(@PathVariable String username) {
        return userAdminService.getByUsername(username)
                .map(user -> BeanUtil.copy(user, UserDTO.class))
                .orElseThrow(() -> new InvalidParamProblem("用户名" + username + "不存在"));
    }

    @PostMapping("/users")
    public UserDTO createUser(@RequestBody UserDTO userDTO) {
        userService.validateUserUniqueFields(userDTO.getUsername(), userDTO.getEmail(), userDTO.getMobile());
        User user = userAdminService.create(BeanUtil.copy(userDTO, User.class));
        return BeanUtil.copy(user, UserDTO.class);
    }

    @PutMapping("/users/{username}")
    public UserDTO updateUser(@PathVariable String username, UserDTO userDTO) {
        User user = userAdminService.update(username, BeanUtil.copy(userDTO, User.class));
        return BeanUtil.copy(user, UserDTO.class);
    }
}
