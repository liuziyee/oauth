package com.dorohedoro.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorohedoro.domain.Permission;
import com.dorohedoro.domain.Role;
import com.dorohedoro.domain.User;
import com.dorohedoro.domain.dto.PageBean;
import com.dorohedoro.domain.dto.RoleDTO;
import com.dorohedoro.domain.dto.UserDTO;
import com.dorohedoro.exception.InvalidParamProblem;
import com.dorohedoro.service.IPermissionAdminService;
import com.dorohedoro.service.IRoleAdminService;
import com.dorohedoro.service.IUserAdminService;
import com.dorohedoro.service.IUserService;
import com.dorohedoro.util.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IUserAdminService userAdminService;
    private final IUserService userService;
    private final IRoleAdminService roleAdminService;
    private final IPermissionAdminService permissionAdminService;

    @GetMapping("/users")
    public PageBean<UserDTO> getAllUsers(UserDTO userDTO) {
        Page<User> page = userAdminService.getAll(BeanUtil.copy(userDTO, User.class));
        
        PageBean<UserDTO> pageBean = new PageBean<>();
        pageBean.setPage(page.getCurrent()); // 页码
        pageBean.setOffset((page.getCurrent() - 1) * page.getSize()); // 偏移量
        pageBean.setContent(page.getRecords().stream().map(user -> BeanUtil.copy(user, UserDTO.class)).collect(toList()));
        pageBean.setTotal(page.getTotal());
        return pageBean;
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

    @PutMapping("/users/{username}/enalbed")
    public UserDTO toggleUserEnabled(@PathVariable String username) {
        return BeanUtil.copy(userAdminService.toggleEnabled(username), UserDTO.class);
    }

    @GetMapping("/users/{username}/roles/available")
    public Set<Role> getUserAvailableRoles(@PathVariable String username) {
        return userAdminService.getAvailableRoles(username);
    }

    @GetMapping("/users/{username}/roles")
    public UserDTO updateUserRoles(@PathVariable String username, List<Long> roleIds) {
        return BeanUtil.copy(userAdminService.assignRoles(username, roleIds), UserDTO.class);
    }

    @GetMapping("/roles")
    public PageBean<RoleDTO> getAllRoles(RoleDTO roleDTO) {
        Page<Role> page = roleAdminService.getAll(BeanUtil.copy(roleDTO, Role.class));
        
        PageBean<RoleDTO> pageBean = new PageBean<>();
        pageBean.setPage(page.getCurrent());
        pageBean.setOffset((page.getCurrent() - 1) * page.getSize());
        pageBean.setContent(page.getRecords().stream().map(role -> BeanUtil.copy(role, RoleDTO.class)).collect(toList()));
        pageBean.setTotal(page.getTotal());
        return pageBean;
    }

    @GetMapping("/roles/{roleId}")
    public RoleDTO getRole(@PathVariable Long roleId) {
        return BeanUtil.copy(roleAdminService.get(roleId), RoleDTO.class);
    }

    @PutMapping("/roles/{roleId}")
    public RoleDTO updateRole(@PathVariable Long roleId, @RequestBody RoleDTO roleDTO) {
        Role role = roleAdminService.updateRole(roleId, BeanUtil.copy(roleDTO, Role.class));
        return BeanUtil.copy(role, RoleDTO.class);
    }

    @DeleteMapping("/roles/{roleId}")
    public void deleteRole(@PathVariable Long roleId) {
        roleAdminService.deleteRole(roleId);
    }

    @GetMapping("/roles/{roleId}/permissions/available")
    public Set<Permission> getRoleAvailablePermissions(@PathVariable Long roleId) {
        return roleAdminService.getAvailablePermissions(roleId);
    }
    
    @PutMapping("/roles/{roleId}/permissions")
    public RoleDTO updateRolePermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        return BeanUtil.copy(roleAdminService.updatePermissions(roleId, permissionIds), RoleDTO.class);
    }
    
    @GetMapping("/permissions")
    public Set<Permission> getAllPermissions() {
        return permissionAdminService.getAll();
    }
    
    // TODO 参考源码 validateEmail() validateMobile() validateRoleName() validateRoleNameNotSelf()
}
