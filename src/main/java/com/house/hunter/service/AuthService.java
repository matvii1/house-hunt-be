package com.house.hunter.service;

import com.house.hunter.model.dto.user.UserLoginDto;
import com.house.hunter.model.dto.user.UserLoginResponseDto;

public interface AuthService {
    UserLoginResponseDto login(UserLoginDto loginDto);
}
