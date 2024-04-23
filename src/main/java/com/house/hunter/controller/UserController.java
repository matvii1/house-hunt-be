package com.house.hunter.controller;

import com.house.hunter.model.dto.user.UserCredentialsDto;
import com.house.hunter.model.dto.user.UserGetResponseDto;
import com.house.hunter.model.dto.user.UserLoginDto;
import com.house.hunter.model.dto.user.UserLoginResponseDto;
import com.house.hunter.model.dto.user.UserRegistrationDto;
import com.house.hunter.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController("api/v1/user")
@AllArgsConstructor
@Validated
@Tag(name = "User Controller", description = "Endpoints for user management")
public class UserController {
    private final UserService userService;

    @GetMapping("/{email}")
    @Operation(summary = "Get user by email")
    @ResponseStatus(HttpStatus.OK)
    /*    @PreAuthorize("hasRole('ADMIN')")*/
    public ResponseEntity<UserGetResponseDto> getUser(@PathVariable @Valid @Email final String email) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(email));
    }

    @PostMapping(value = "/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Login user")
    public UserLoginResponseDto login(@RequestBody UserLoginDto loginDto) {
        return userService.login(loginDto);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshAccessToken(@RequestHeader("Refresh-Token") String refreshToken) {
        String newAccessToken = userService.refreshAccessToken(refreshToken);
        if (newAccessToken != null) {
            return ResponseEntity.ok(newAccessToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Register user")
    @ResponseStatus(HttpStatus.CREATED)
    /*    @PreAuthorize("hasAnyRole('ADMIN','GUEST')")*/
    public ResponseEntity<Void> registerUser(@RequestBody @Valid final UserRegistrationDto userRegistrationDto) {
        userService.registerUser(userRegistrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PutMapping("/password")
    @Operation(summary = "Update password")
    @ResponseStatus(HttpStatus.OK)
    /*    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD','TENANT')")*/
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid final UserCredentialsDto userCredentialsDto) {
        userService.updatePassword(userCredentialsDto.getPassword(), userCredentialsDto.getEmail());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{email}")
    @Operation(summary = "Delete user")
    /*    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD','TENANT')")*/
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteUser(@Valid @PathVariable final String email) {
        userService.deleteUser(email);
        return ResponseEntity.ok().build();
    }
}
