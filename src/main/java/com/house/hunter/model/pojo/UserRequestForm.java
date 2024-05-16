package com.house.hunter.model.pojo;

import com.house.hunter.constant.UserRequestType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestForm {
    @NotEmpty(message = "Name is required")
    private String name;
    @NotEmpty(message = "Email is required")
    private String email;
    @Pattern(regexp = UserRequestType.PATTERN, message = "Invalid request type")
    private String type;
    @NotEmpty(message = "Subject is required")
    private String subject;
    @NotEmpty(message = "Message is required")
    @Size(min = 10, message = "Message must be at least 10 characters long")
    private String message;
    private String propertyId;
}
