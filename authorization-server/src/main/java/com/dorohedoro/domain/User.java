package com.dorohedoro.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dorohedoro.domain.dto.PageBean;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

@Data
@TableName("users")
public class User extends PageBean implements UserDetails, Serializable {

    @TableId
    private Long id;

    private String username;
    @JSONField(serialize = false)
    @TableField("password_hash")
    private String password;

    private String email;

    private String mobile;

    private boolean enabled;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;
    @JSONField(serialize = false)
    private Set<Role> roles = new HashSet<>(); // 这里为了规避空指针做了初始化
    
    // TODO 两个字段 usingMfa mfaKey

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    // 获取权限
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .flatMap(role -> Stream.concat(
                        Stream.of(new SimpleGrantedAuthority(role.getRoleName())), 
                        role.getPermissions().stream())
                ) // 把角色和权限平铺开,放入一个流里
                .collect(toSet()); // 去掉重复的权限
    }
}
