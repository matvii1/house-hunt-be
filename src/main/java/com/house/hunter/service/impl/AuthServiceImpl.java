package com.house.hunter.service.impl;

import com.house.hunter.exception.UserNotFoundException;
import com.house.hunter.model.dto.user.UserLoginDto;
import com.house.hunter.model.dto.user.UserLoginResponseDto;
import com.house.hunter.model.entity.User;
import com.house.hunter.repository.UserRepository;
import com.house.hunter.service.AuthService;
import com.house.hunter.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    public UserLoginResponseDto login(UserLoginDto loginDto) {
        final Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        final String email = authentication.getName();
        userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        final User user = new User();
        final String token = jwtUtil.createToken(user);
        return new UserLoginResponseDto(email, token);
    }
}
