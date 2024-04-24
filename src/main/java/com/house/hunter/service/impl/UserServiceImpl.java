package com.house.hunter.service.impl;

import com.house.hunter.exception.UserAlreadyExistsException;
import com.house.hunter.exception.UserNotFoundException;
import com.house.hunter.model.dto.user.UserGetResponseDto;
import com.house.hunter.model.dto.user.UserLoginDto;
import com.house.hunter.model.dto.user.UserLoginResponseDto;
import com.house.hunter.model.dto.user.UserRegistrationDto;
import com.house.hunter.model.entity.RefreshToken;
import com.house.hunter.model.entity.User;
import com.house.hunter.repository.RefreshTokenRepository;
import com.house.hunter.repository.UserRepository;
import com.house.hunter.service.UserService;
import com.house.hunter.util.JWTUtil;
import com.house.hunter.util.PasswordEncoder;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ModelMapper modelMapper;
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UserServiceImpl.class);

    @Transactional
    public void registerUser(@Valid final UserRegistrationDto userRegistrationDto) {
        if (userRepository.existsByEmail(userRegistrationDto.getEmail())) {
            throw new UserAlreadyExistsException(userRegistrationDto.getEmail());
        }
        final User user = modelMapper.map(userRegistrationDto, User.class);
        // Encrypting the password with automatic salting
        final String encryptedPassword = PasswordEncoder.getPasswordEncoder().encode(user.getPassword());
        user.setPassword(encryptedPassword);
        LOGGER.info("User created: {}", user.getEmail());
        userRepository.save(user);
    }

    public UserLoginResponseDto login(UserLoginDto loginDto) {
        final Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        final String email = authentication.getName();
        final User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        final String token = jwtUtil.generateAccessToken(user);
        refreshTokenRepository.save(RefreshToken.builder().user(user).token(token).build());
        return new UserLoginResponseDto(email, token);
    }

    public String refreshAccessToken(String refreshToken) {
        if (jwtUtil.validateToken(refreshToken)) {
            String username = jwtUtil.getEmailFromToken(refreshToken);
            Optional<User> user = userRepository.findByEmail(username);
            if (user.isPresent()) {
                return jwtUtil.generateAccessToken(user.get());
            }
        }
        return null;
    }

    public UserGetResponseDto getUser(@Valid final String email) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    LOGGER.info("User found: {}", user.getEmail());
                    return modelMapper.map(user, UserGetResponseDto.class);
                })
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    @Transactional
    public void updatePassword(@Valid final String password, @Valid final String email) {
        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        LOGGER.info("User found: {}", email);
        final String encryptedPassword = PasswordEncoder.getPasswordEncoder().encode(password);
        user.setPassword(encryptedPassword);
        LOGGER.info("Password has been updated for user : {}", user.getEmail());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(@Valid String email) {
        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        LOGGER.info("User deleted: {}", user.getEmail());
        userRepository.delete(user);
    }

}
