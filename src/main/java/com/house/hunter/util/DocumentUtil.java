package com.house.hunter.util;

import com.house.hunter.exception.FileOperationException;
import com.house.hunter.exception.ImageNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

public class DocumentUtil {
    private static DocumentUtil INSTANCE;

    private DocumentUtil() {
    }

    public static DocumentUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DocumentUtil();
        }
        return INSTANCE;
    }

    public Resource getDocument(String documentDirectory, String documentName) {
        File dir = new File(documentDirectory +"/" + documentName);
        try {
            if (dir.exists()) {
                Resource resource = new UrlResource(dir.toURI());
                return resource;
            }
        } catch (IOException e) {
            throw new FileOperationException(documentName);
        }
        return null;
    }

    public void deleteImage(String imageDirectory, String imageName) throws IOException {
        Path imagePath = Path.of(imageDirectory, imageName);
        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
        } else {
            throw new ImageNotFoundException("Image not found");
        }
    }

    public String saveDocumentToStorage(String uploadDirectory, MultipartFile document) throws IOException {
        final String uniqueFileName = UUID.randomUUID() + "_" + document.getOriginalFilename();

        final Path uploadPath = Path.of(uploadDirectory);
        final Path filePath = uploadPath.resolve(uniqueFileName);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Check if a document with the same data already exists
        if (isDocumentDuplicate(uploadDirectory, document)) {
            throw new IllegalArgumentException("Document with the same data already exists");
        }

        Files.copy(document.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFileName;
    }

    private boolean isDocumentDuplicate(String uploadDirectory, MultipartFile document) throws IOException {
        byte[] newDocumentBytes = document.getBytes();

        // Iterate over existing images in the directory
        try (Stream<Path> paths = Files.walk(Path.of(uploadDirectory))) {
            return paths
                    .filter(Files::isRegularFile)
                    .anyMatch(path -> {
                        try {
                            byte[] existingDocumentBytes = Files.readAllBytes(path);
                            return Arrays.equals(newDocumentBytes, existingDocumentBytes);
                        } catch (IOException e) {
                            return false;
                        }
                    });
        }
    }
}
