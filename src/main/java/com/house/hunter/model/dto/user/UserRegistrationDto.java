package com.house.hunter.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {
    @NotEmpty(message = "Name is required")
    private String name;

    @NotEmpty(message = "Surname is required")
    private String surname;

    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email is required")
    private String email;

    @Pattern(regexp = "^\\+(?:[0-9] ?){6,14}[0-9]$", message = "Invalid phone number format")
    @NotEmpty(message = "Phone number is required")
    private String phoneNumber;

    @NotEmpty(message = "User role is required")
    @Pattern(regexp = "ADMIN|LANDLORD|TENANT", message = "Invalid user role")
    private String role;

    @NotEmpty(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace")
    private String password;
}
