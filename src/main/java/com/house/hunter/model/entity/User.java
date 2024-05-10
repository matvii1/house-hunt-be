package com.house.hunter.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.house.hunter.constant.UserAccountStatus;
import com.house.hunter.constant.UserRole;
import com.house.hunter.constant.UserEmailVerificationStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    @NotNull
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @NotEmpty(message = "Name is required")
    private String name;

    @NotEmpty(message = "Surname is required")
    private String surname;

    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email is required")
    @Column(unique = true)
    private String email;

    @Pattern(regexp = "^\\+(?:[0-9] ?){6,14}[0-9]$", message = "Invalid phone number format")
    @NotEmpty(message = "Phone number is required")
    @Column(unique = true)
    private String phoneNumber;

    // Store hashed password, not the blob directly. Ensure security by using a strong hash function.
    @NotEmpty(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace")
    private String password;

    @Enumerated(EnumType.STRING)
    @NotEmpty(message = "Email is required")
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @NotEmpty(message = "Email is required")
    private UserAccountStatus accountStatus;

    @Enumerated(EnumType.STRING)
    @NotEmpty(message = "Email is required")
    private UserEmailVerificationStatus verificationStatus;

    //TODO add a verify column for verifying the document
    // How to do it on frontend and backend
    // implement email verification and add a flag as ACTIVE or INACTIVE and user can't login until they verify their email
    // fix the property search
    // add a complaint form
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"owner"})
    private List<Property> properties = new ArrayList<>();
}
