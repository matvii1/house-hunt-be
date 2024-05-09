package com.house.hunter.service;

import com.house.hunter.model.dto.user.UserGetResponse;
import com.house.hunter.model.dto.user.UserRegistrationDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


public interface UserService {

    void registerUser(UserRegistrationDto userRegistrationDto);

    UserGetResponse getUser(String email);

    void updatePassword(String password, String email);

    void deleteUser(String email);

    List<String> getUserDocuments(String email);

    Resource downloadFile(String filename);

    UUID uploadDocument(String documentType, MultipartFile file);

    void deleteDocument(String documentName);

}

