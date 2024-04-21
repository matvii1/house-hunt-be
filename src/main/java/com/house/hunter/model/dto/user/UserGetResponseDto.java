package com.house.hunter.model.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.house.hunter.constant.UserRole;
import com.house.hunter.constant.UserStatus;
import com.house.hunter.model.entity.Document;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserGetResponseDto {
    private String id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private UserStatus status;
    private Set<Document> documents;
}
