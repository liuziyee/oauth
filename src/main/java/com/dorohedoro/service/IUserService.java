package com.dorohedoro.service;

import com.dorohedoro.domain.User;
import com.dorohedoro.domain.dto.Token;
import org.springframework.security.core.Authentication;

import java.nio.file.AccessDeniedException;

public interface IUserService {

    Token login(String username, String password);

    Token refreshToken(String accessToken, String refreshToken) throws AccessDeniedException;

    void register(User user);

    void updateUser(User user);

    User getUserByEmail(String email);

    Boolean isUsernameExist(String username);
    
    Boolean isEmailExist(String email);
    
    Boolean isMobileExist(String mobile);

    Boolean isUserself(Authentication authentication, String username);
}
