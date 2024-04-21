package com.house.hunter.service.impl;

import com.house.hunter.exception.UserAlreadyExistsException;
import com.house.hunter.exception.UserNotFoundException;
import com.house.hunter.model.dto.user.UserCredentialsDto;
import com.house.hunter.model.dto.user.UserGetResponseDto;
import com.house.hunter.model.dto.user.UserRegistrationDto;
import com.house.hunter.model.entity.User;
import com.house.hunter.repository.UserRepository;
import com.house.hunter.util.PasswordEncoder;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Optional;

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
        LOGGER.info("User created: {}", user.getEmail());
        userRepository.save(user);
    }


    public UserGetResponseDto getUser(@Valid final String email) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    LOGGER.info("User found: {}", user.getEmail());
                    return modelMapper.map(user, UserGetResponseDto.class);
                })
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public boolean authenticateUser(@Valid UserCredentialsDto userCredentialsDto) {
        final Optional<User> user = userRepository.findByEmail(userCredentialsDto.getEmail());
        return user.isPresent() && PasswordEncoder.matches(userCredentialsDto.getPassword(), user.get().getPassword());
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
