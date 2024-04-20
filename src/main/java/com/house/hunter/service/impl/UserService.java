package com.house.hunter.service.impl;

import com.house.hunter.model.dto.user.UserCredentialsDto;
import com.house.hunter.model.dto.user.UserGetResponseDto;
import com.house.hunter.model.dto.user.UserRegistrationDto;


public interface UserService {

    void registerUser(UserRegistrationDto userRegistrationDto);

    UserGetResponseDto getUser(String email);

    boolean authenticateUser(UserCredentialsDto userLoginDto);

    void updatePassword(String password, String email);

    void deleteUser(String email);

}

