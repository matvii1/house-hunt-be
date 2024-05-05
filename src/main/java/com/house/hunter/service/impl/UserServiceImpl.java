package com.house.hunter.service.impl;

import com.house.hunter.constant.UserRole;
import com.house.hunter.exception.IllegalRequestException;
import com.house.hunter.exception.UserAlreadyExistsException;
import com.house.hunter.exception.UserNotFoundException;
import com.house.hunter.model.dto.user.UserGetResponse;
import com.house.hunter.model.dto.user.UserRegistrationDto;
import com.house.hunter.model.entity.User;
import com.house.hunter.repository.UserRepository;
import com.house.hunter.service.UserService;
import com.house.hunter.util.PasswordEncoder;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
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
        userRepository.save(user);
        LOGGER.info("User created: {}", user.getEmail());
    }

    public UserGetResponse getUser(@Valid final String email) {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        // Check if the currently authenticated user is an admin or the same user being retrieved
        if (hasRole(authentication, UserRole.ADMIN) || currentUserEmail.equals(email)) {
            return userRepository.findByEmail(email)
                    .map(user -> {
                        LOGGER.info("User found: {}", user.getEmail());
                        return modelMapper.map(user, UserGetResponse.class);
                    })
                    .orElseThrow(() -> new UserNotFoundException(email));
        } else {
            throw new IllegalRequestException("You are not authorized to retrieve this user");
        }
    }

    @Transactional
    public void updatePassword(@Valid final String password, @Valid final String email) {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        // Check if the currently authenticated user is an admin or the same user being updated
        if (hasRole(authentication, UserRole.ADMIN) || currentUserEmail.equals(email)) {
            final User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException(email));
            LOGGER.info("User found: {}", email);
            final String encryptedPassword = PasswordEncoder.getPasswordEncoder().encode(password);
            user.setPassword(encryptedPassword);
            LOGGER.info("Password has been updated for user : {}", user.getEmail());
            userRepository.save(user);
        } else {
            throw new IllegalRequestException("You are not authorized to update this user's password");
        }
    }
    @Transactional
    public void deleteUser(@Valid String email) {
        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        // Check if the currently authenticated user is an admin or the same user being deleted
        if (hasRole(authentication, UserRole.ADMIN) || currentUserEmail.equals(email)) {
            userRepository.delete(user);
            LOGGER.info("User deleted: {}", user.getEmail());
        } else {
            throw new IllegalRequestException("You are not authorized to delete this user");
        }
    }

    private boolean hasRole(Authentication authentication, UserRole role) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role.name()));
    }

}
