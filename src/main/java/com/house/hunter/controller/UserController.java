package com.house.hunter.controller;

import com.house.hunter.model.dto.user.UserCredentials;
import com.house.hunter.model.dto.user.UserGetResponse;
import com.house.hunter.model.dto.user.UserRegistrationDto;
import com.house.hunter.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
@Validated
@Tag(name = "User Controller", description = "Endpoints for user management")
public class UserController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get user by email")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD','TENANT')")
    public ResponseEntity<UserGetResponse> getUser(@RequestParam @Valid @Email final String email) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(email));
    }


    @PostMapping("/register")
    @Operation(summary = "Register user")
    @ResponseStatus(HttpStatus.CREATED)
    // no auth filter needed
    public ResponseEntity<Void> registerUser(@RequestBody @Valid final UserRegistrationDto userRegistrationDto) {
        userService.registerUser(userRegistrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PutMapping("/password")
    @Operation(summary = "Update password")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD','TENANT')")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid final UserCredentials userCredentialsDto) {
        userService.updatePassword(userCredentialsDto.getPassword(), userCredentialsDto.getEmail());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{email}")
    @Operation(summary = "Delete user")
    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD','TENANT')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //TODO: Add a check to see if the user is an admin or the user being deleted is the same as the one making the request
    public ResponseEntity<Void> deleteUser(@Valid @PathVariable final String email) {
        userService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }
}
