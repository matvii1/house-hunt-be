package com.house.hunter.service.impl;

import com.house.hunter.exception.InvalidRefreshTokenException;
import com.house.hunter.exception.InvalidUserAuthenticationException;
import com.house.hunter.model.dto.user.UserLoginDto;
import com.house.hunter.model.dto.user.UserLoginResponseDto;
import com.house.hunter.model.entity.RefreshToken;
import com.house.hunter.model.entity.User;
import com.house.hunter.repository.RefreshTokenRepository;
import com.house.hunter.repository.UserRepository;
import com.house.hunter.service.AuthService;
import com.house.hunter.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserLoginResponseDto login(UserLoginDto loginDto) throws InvalidUserAuthenticationException {
        // Authenticating the user
        final Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        if (authentication.isAuthenticated()) {
            final String email = authentication.getName();
            final User user = userRepository.findByEmail(email).get();
            final String token = jwtUtil.buildAccessToken(user.getEmail(), user.getRole());
            final RefreshToken refreshToken = createRefreshToken(user);
            return UserLoginResponseDto.builder().email(loginDto.getEmail()).token(token).refreshToken(refreshToken.getToken()).build();
        }
        throw new InvalidUserAuthenticationException();
    }

    public String renewRefreshToken(String token) {
        final RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(InvalidRefreshTokenException::new);
        verifyExpiration(refreshToken);
        final User user = refreshToken.getUser();
        return jwtUtil.buildRefreshToken(user.getEmail(), user.getRole());
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user).map(
                // if the user already has a refresh token, then update the token and expiry date
                token -> {
                    final RefreshToken newToken = jwtUtil.generateRefreshToken(user);
                    token.setToken(newToken.getToken());
                    token.setExpiryDate(newToken.getExpiryDate());
                    return refreshTokenRepository.save(token);
                }
        ).orElseGet(() -> jwtUtil.generateRefreshToken(user));
        return refreshTokenRepository.save(refreshToken);
    }


    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new InvalidRefreshTokenException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return token;
    }

}
