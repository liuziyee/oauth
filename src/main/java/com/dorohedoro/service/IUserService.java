package com.dorohedoro.service;

import com.dorohedoro.domain.dto.Token;

public interface IUserService {

    Token login(String username, String password);
}
