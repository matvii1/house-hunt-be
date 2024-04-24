package com.house.hunter.controller;

import com.house.hunter.model.dto.token.RefreshTokenRequestDTO;
import com.house.hunter.service.impl.RefreshTokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("api/v1/auth")
@AllArgsConstructor
@Validated
@Tag(name = "Auth Controller", description = "Endpoints for authentication management")
public class AuthController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/refreshToken")
    public ResponseEntity<String> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        final String token = refreshTokenService.createRefreshToken(refreshTokenRequestDTO).getToken();
        if (token != null) {
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
