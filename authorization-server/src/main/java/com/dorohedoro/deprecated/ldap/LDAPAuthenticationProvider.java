package com.dorohedoro.deprecated.ldap;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class LDAPAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final LDAPUserRepo ldapUserRepo;
    
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {}

    @Override
    // 自定义认证流程
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return super.authenticate(authentication);
    }

    @Override
    // 获取用户信息
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        return ldapUserRepo.findByUsernameAndPassword(username, authentication.getCredentials().toString())
                .orElseThrow(() -> new BadCredentialsException("[LDAP] 用户名或密码错误"));
    }
}
