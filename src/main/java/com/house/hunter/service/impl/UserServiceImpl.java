package com.house.hunter.service.impl;

import com.house.hunter.constant.DocumentType;
import com.house.hunter.constant.UserAccountStatus;
import com.house.hunter.constant.UserEmailVerificationStatus;
import com.house.hunter.constant.UserRole;
import com.house.hunter.exception.DocumentNotFoundException;
import com.house.hunter.exception.FileOperationException;
import com.house.hunter.exception.IllegalRequestException;
import com.house.hunter.exception.InvalidDocumentTypeException;
import com.house.hunter.exception.InvalidTokenException;
import com.house.hunter.exception.InvalidVerificationTokenException;
import com.house.hunter.exception.UserAlreadyExistsException;
import com.house.hunter.exception.UserNotFoundException;
import com.house.hunter.model.dto.user.CreateAdminDTO;
import com.house.hunter.model.dto.user.UserGetResponse;
import com.house.hunter.model.dto.user.UserRegistrationDto;
import com.house.hunter.model.entity.ConfirmationToken;
import com.house.hunter.model.entity.Document;
import com.house.hunter.model.entity.User;
import com.house.hunter.repository.ConfirmationTokenRepository;
import com.house.hunter.repository.DocumentRepository;
import com.house.hunter.repository.UserRepository;
import com.house.hunter.service.EmailService;
import com.house.hunter.service.UserService;
import com.house.hunter.util.DocumentUtil;
import com.house.hunter.util.MailUtil;
import com.house.hunter.util.PasswordEncoder;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailService emailService;
    private final ModelMapper modelMapper;
    private final String documentDirectory;

    public UserServiceImpl(UserRepository userRepository, DocumentRepository documentRepository, ModelMapper modelMapper, @Value("${documents.directory}") String documentDirectory, ConfirmationTokenRepository confirmationTokenRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.modelMapper = modelMapper;
        this.documentDirectory = documentDirectory;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.emailService = emailService;
    }

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UserServiceImpl.class);

    private final DocumentUtil documentUtil = DocumentUtil.getInstance();

    @Transactional
    @Override
    public void registerUser(@Valid final UserRegistrationDto userRegistrationDto) {
        if (userRepository.existsByEmail(userRegistrationDto.getEmail())) {
            throw new UserAlreadyExistsException(userRegistrationDto.getEmail());
        }
        final User user = modelMapper.map(userRegistrationDto, User.class);
        user.setVerificationStatus(UserEmailVerificationStatus.PENDING_VERIFICATION);
        user.setAccountStatus(UserAccountStatus.NOT_ACTIVATED);
        // Encrypting the password with automatic salting
        final String encryptedPassword = PasswordEncoder.getPasswordEncoder().encode(user.getPassword());
        user.setPassword(encryptedPassword);
        userRepository.save(user);
        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenRepository.save(confirmationToken);
        MimeMessagePreparator registrationEmail = MailUtil.buildRegistrationEmail(user.getEmail(), confirmationToken.getConfirmationToken());
        emailService.sendEmail(registrationEmail);
        LOGGER.info("User created: {}", user.getEmail());
    }

    @Override
    public UserGetResponse getUser(@Valid final String email) {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String requestMakerEmail = authentication.getName();
        // Check if the currently authenticated user is an admin or the same user being retrieved
        if (hasRole(authentication, UserRole.ADMIN) || requestMakerEmail.equals(email)) {
            return userRepository.findByEmail(email)
                    .map(user -> {
                        LOGGER.info("User found: {}", user.getEmail());
                        // If the user is not verified, hide the phone number
                        if (userRepository.findByEmail(requestMakerEmail).get().getVerificationStatus() != UserEmailVerificationStatus.VERIFIED) {
                            user.setPhoneNumber(null);
                        }
                        return modelMapper.map(user, UserGetResponse.class);
                    })
                    .orElseThrow(() -> new UserNotFoundException(email));
        } else {
            throw new IllegalRequestException("You are not authorized to retrieve this user");
        }
    }

    @Override
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

    @Override
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

    @Override
    public List<String> getUserDocuments(String email) {
        List<Document> documents = documentRepository.findDocumentsByUserEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        return documents.stream()
                .map(Document::getFilename)
                .toList();
    }

    @Override
    public Resource downloadFile(String filename) {
        User user = getAuthenticatedUser();
        // if the user does not have requested document, throw exception
        documentRepository.findDocumentsByUserEmail(user.getEmail()).orElseThrow(DocumentNotFoundException::new);
        return documentUtil.getDocument(documentDirectory, filename);

    }

    @Override
    @Transactional
    public UUID uploadDocument(String documentType, MultipartFile file) {
        if (!DocumentType.contains(documentType)) {
            throw new InvalidDocumentTypeException();
        }
        User user = getAuthenticatedUser();
        try {
            String documentName = documentUtil.saveDocumentToStorage(documentDirectory, file);

            // Check if a document with the same user and documentType already exists
            Optional<Document> existingDocument = documentRepository.findByUserAndDocumentType(user, DocumentType.valueOf(documentType));

            if (existingDocument.isPresent()) {
                // If the document exists, update its filename and save it
                Document document = existingDocument.get();
                document.setFilename(documentName);
                return documentRepository.save(document).getId();
            } else {
                // If the document doesn't exist, create a new one and save it
                Document document = new Document(null, documentName, DocumentType.valueOf(documentType), user);
                return documentRepository.save(document).getId();
            }
        } catch (IOException e) {
            throw new FileOperationException(e.getMessage());
        }
    }


    @Override
    @Transactional
    public void deleteDocument(String documentName) {
        try {
            Document document = documentRepository.findByFilename(documentName)
                    .orElseThrow(DocumentNotFoundException::new);
            documentRepository.delete(document);
            documentUtil.deleteDocument(documentDirectory, documentName);
        } catch (IOException e) {
            throw new FileOperationException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void verifyUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        user.setVerificationStatus(UserEmailVerificationStatus.VERIFIED);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void activateUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        user.setAccountStatus(UserAccountStatus.ACTIVE);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void createAdminUser(CreateAdminDTO createAdminDTO) {
        if (userRepository.existsByEmail(createAdminDTO.getEmail())) {
            throw new UserAlreadyExistsException(createAdminDTO.getEmail());
        }
        final User user = modelMapper.map(createAdminDTO, User.class);
        user.setRole(UserRole.ADMIN);
        user.setVerificationStatus(UserEmailVerificationStatus.VERIFIED);
        user.setAccountStatus(UserAccountStatus.ACTIVE);
        // Encrypting the password with automatic salting
        final String encryptedPassword = PasswordEncoder.getPasswordEncoder().encode(user.getPassword());
        user.setPassword(encryptedPassword);
        userRepository.save(user);
        LOGGER.info("Admin user created: {}", user.getEmail());
    }

    @Override
    public void confirmEmail(String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken).orElseThrow(InvalidVerificationTokenException::new);
        User user = userRepository.findByEmail(token.getUser().getEmail()).orElseThrow(() -> new UserNotFoundException("User not found with email: " + token.getUser().getEmail()));
        user.setAccountStatus(UserAccountStatus.ACTIVE);
        userRepository.save(user);
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // Generate a unique reset password token
        String resetToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetToken);
        userRepository.save(user);

        // Send the password reset email with the link
        MimeMessagePreparator resetPasswordEmail = MailUtil.buildResetPasswordEmail(user.getEmail(), resetToken);
        emailService.sendEmail(resetPasswordEmail);
    }

    @Override
    public void resetPassword(String resetToken, String newPassword) {
        User user = userRepository.findByResetPasswordToken(resetToken)
                .orElseThrow(() -> new InvalidTokenException("Invalid reset password token : " + resetToken));

        // Update the user's password
        user.setPassword(PasswordEncoder.getPasswordEncoder().encode(newPassword));
        user.setResetPasswordToken(null);
        userRepository.save(user);
    }

    private User getAuthenticatedUser() {
        return userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UserNotFoundException("User can not be gotten from the authorization token " + SecurityContextHolder.getContext().getAuthentication().getName()));
    }

    private boolean hasRole(Authentication authentication, UserRole role) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role.name()));
    }

}
