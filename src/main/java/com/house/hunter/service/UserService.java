package com.house.hunter.service;

import com.house.hunter.model.dto.user.UserGetResponse;
import com.house.hunter.model.dto.user.UserRegistrationDto;


public interface UserService {

    void registerUser(UserRegistrationDto userRegistrationDto);

    UserGetResponse getUser(String email);

    void updatePassword(String password, String email);

    void deleteUser(String email);

}

