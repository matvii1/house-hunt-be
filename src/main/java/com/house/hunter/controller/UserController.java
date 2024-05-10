package com.house.hunter.controller;

import com.house.hunter.model.dto.user.CreateAdminDTO;
import com.house.hunter.model.dto.user.UserCredentials;
import com.house.hunter.model.dto.user.UserGetResponse;
import com.house.hunter.model.dto.user.UserRegistrationDto;
import com.house.hunter.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
@Validated
@Tag(name = "User Controller", description = "Endpoints for user management")
public class UserController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get user by email")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD','TENANT')")
    public ResponseEntity<UserGetResponse> getUser(@RequestParam @Valid @Email final String email) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(email));
    }

    @PostMapping("/register")
    @Operation(summary = "Register user")
    @ResponseStatus(HttpStatus.CREATED)
    // no auth filter needed
    public ResponseEntity<Void> registerUser(@RequestBody @Valid final UserRegistrationDto userRegistrationDto) {
        userService.registerUser(userRegistrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/password")
    @Operation(summary = "Update password")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD','TENANT')")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid final UserCredentials userCredentialsDto) {
        userService.updatePassword(userCredentialsDto.getPassword(), userCredentialsDto.getEmail());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{email}")
    @Operation(summary = "Delete user")
    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD','TENANT')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(@Valid @PathVariable @NotEmpty final String email) {
        userService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/documents/{email}")
    @Operation(summary = "Get user documents")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD','TENANT')")
    public ResponseEntity<List<String>> getDocuments(@Valid @PathVariable @NotEmpty final String email) {
        return new ResponseEntity<>(userService.getUserDocuments(email), HttpStatus.OK);
    }

    @GetMapping(value = "/documents/download/{documentName}", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Download document")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Resource> downloadDocument(@PathVariable(value = "documentName") @NotEmpty String documentName) {
        Resource file = userService.downloadFile(documentName);
        if (file == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                    .body(file);
        }
    }

    @PostMapping(path = "/documents/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload document")
    @ResponseStatus(HttpStatus.CREATED)
    public UUID uploadFile(@RequestParam @NotEmpty String documentType,
                           @Parameter(
                                   description = "Document file",
                                   required = true,
                                   content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
                           )
                           @RequestPart("file") MultipartFile file) {
        return userService.uploadDocument(documentType, file);
    }

    @DeleteMapping("/documents/{documentName}")
    @Operation(summary = "Delete document")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteDocument(@PathVariable(value = "documentName") @NotEmpty String documentName) {
        userService.deleteDocument(documentName);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Verify user identity")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> verifyUserIdentity(@PathVariable UUID userId) {
        userService.verifyUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register/admin")
    @Operation(summary = "Register admin user")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createAdminUser(@RequestBody @Valid final CreateAdminDTO createAdminDTO) {
        userService.createAdminUser(createAdminDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
