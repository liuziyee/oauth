package com.dorohedoro.service;

import com.dorohedoro.domain.User;
import com.dorohedoro.domain.dto.Token;

import java.nio.file.AccessDeniedException;

public interface IUserService {

    Token login(String username, String password);

    Token refreshToken(String accessToken, String refreshToken) throws AccessDeniedException;

    void register(User user);

    Boolean isUsernameExist(String username);
    
    Boolean isEmailExist(String email);
    
    Boolean isMobileExist(String mobile);
}
