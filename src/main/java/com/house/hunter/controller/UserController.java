package com.house.hunter.controller;

import com.house.hunter.model.dto.user.UserCredentialsDto;
import com.house.hunter.model.dto.user.UserRegistrationDto;
import com.house.hunter.model.dto.user.UserGetResponseDto;
import com.house.hunter.service.impl.UserService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController("api/v1/user")
@AllArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping("/{email}")
    public ResponseEntity<UserGetResponseDto> getUser(@PathVariable @Valid @Email final String email) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(email));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody @Valid final UserRegistrationDto userRegistrationDto) {
        userService.registerUser(userRegistrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticateUser(@RequestBody @Valid final UserCredentialsDto userCredentialsDto) {
        final boolean isAuthenticated = userService.authenticateUser(userCredentialsDto);
        return isAuthenticated ? ResponseEntity.ok("Authentication successful") : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
    }
    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid final UserCredentialsDto userCredentialsDto) {
        userService.updatePassword(userCredentialsDto.getPassword(), userCredentialsDto.getEmail());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable final String email) {
        userService.deleteUser(email);
        return ResponseEntity.ok().build();
    }
}
