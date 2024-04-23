package com.house.hunter.service;

import com.house.hunter.model.dto.user.UserGetResponseDto;
import com.house.hunter.model.dto.user.UserLoginDto;
import com.house.hunter.model.dto.user.UserLoginResponseDto;
import com.house.hunter.model.dto.user.UserRegistrationDto;


public interface UserService {

    void registerUser(UserRegistrationDto userRegistrationDto);

    UserGetResponseDto getUser(String email);

    void updatePassword(String password, String email);

    void deleteUser(String email);

    UserLoginResponseDto login(UserLoginDto loginDto);

    String refreshAccessToken(String refreshToken);


}

