package com.house.hunter.model.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.house.hunter.constant.UserAccountStatus;
import com.house.hunter.constant.UserRole;
import com.house.hunter.constant.UserVerificationStatus;
import com.house.hunter.model.entity.Document;
import com.house.hunter.model.entity.Property;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserGetResponse {
    private String id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private UserAccountStatus accountStatus;
    private UserVerificationStatus verificationStatus;
    @JsonIgnoreProperties({"user"})
    private Set<Document> documents;
    @JsonIgnoreProperties({"owner"})
    private List<Property> properties;
}
