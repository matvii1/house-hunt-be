package com.house.hunter.service.impl;

import com.house.hunter.constant.DocumentType;
import com.house.hunter.constant.UserRole;
import com.house.hunter.exception.FileOperationException;
import com.house.hunter.exception.IllegalRequestException;
import com.house.hunter.exception.InvalidDocumentTypeException;
import com.house.hunter.exception.UserAlreadyExistsException;
import com.house.hunter.exception.UserNotFoundException;
import com.house.hunter.model.dto.user.UserGetResponse;
import com.house.hunter.model.dto.user.UserRegistrationDto;
import com.house.hunter.model.entity.Document;
import com.house.hunter.model.entity.User;
import com.house.hunter.repository.DocumentRepository;
import com.house.hunter.repository.UserRepository;
import com.house.hunter.service.UserService;
import com.house.hunter.util.DocumentUtil;
import com.house.hunter.util.PasswordEncoder;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final ModelMapper modelMapper;
    private final String documentDirectory;

    public UserServiceImpl(UserRepository userRepository, DocumentRepository documentRepository, ModelMapper modelMapper, @Value("${documents.directory}") String documentDirectory) {
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.modelMapper = modelMapper;
        this.documentDirectory = documentDirectory;
    }

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UserServiceImpl.class);

    private final DocumentUtil documentUtil = DocumentUtil.getInstance();

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

    public List<String> getUserDocuments(String email) {
        List<Document> documents = documentRepository.findDocumentsByUserEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        return documents.stream()
                .map(Document::getFilename)
                .toList();
    }

    public Resource downloadFile(String filename) {
        return documentUtil.getDocument(documentDirectory, filename);

    }

    @Transactional
    public UUID uploadDocument(String documentType, MultipartFile file) {
        if (!DocumentType.contains(documentType)) {
            throw new InvalidDocumentTypeException();
        }
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UserNotFoundException(SecurityContextHolder.getContext().getAuthentication().getName()));
        try {
            String documentName = documentUtil.saveDocumentToStorage(documentDirectory, file);
            Document document = new Document(null, documentName, DocumentType.valueOf(documentType), user);
            return documentRepository.save(document).getId();
        } catch (IOException e) {
            throw new FileOperationException(e.getMessage());
        }
    }

    private boolean hasRole(Authentication authentication, UserRole role) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role.name()));
    }

}
