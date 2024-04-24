package com.house.hunter.service.impl;

import com.house.hunter.model.dto.token.RefreshTokenRequestDTO;
import com.house.hunter.model.entity.RefreshToken;
import com.house.hunter.repository.RefreshTokenRepository;
import com.house.hunter.repository.UserRepository;
import com.house.hunter.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    public RefreshToken createRefreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO) {
        return refreshTokenRepository.findByToken(refreshTokenRequestDTO.getToken())
                .map(this::verifyExpiration)
                .filter(token -> jwtUtil.validateToken(token.getToken()))
                .map(RefreshToken::getUser)
                .map(jwtUtil::generateRefreshToken)
                .map(refreshTokenRepository::save)
                .orElseThrow(() -> new RuntimeException("Refresh token can not be generated."));
    }

    private Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return token;
    }

}
