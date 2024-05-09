package com.house.hunter.util;

import com.house.hunter.exception.ImageNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

public final class ImageUtil {
    private static ImageUtil INSTANCE;

    private ImageUtil() {
    }

    public static ImageUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ImageUtil();
        }
        return INSTANCE;
    }

    // To view an image
    public byte[] getImage(String imageDirectory, String imageName) throws IOException {
        Path imagePath = Path.of(imageDirectory, imageName);
        if (Files.exists(imagePath)) {
            byte[] imageBytes = Files.readAllBytes(imagePath);
            return imageBytes;
        } else {
            throw new ImageNotFoundException("Image not found");
        }
    }

    // Delete an image
    public void deleteImage(String imageDirectory, String imageName) throws IOException {
        Path imagePath = Path.of(imageDirectory, imageName);
        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
        } else {
            throw new ImageNotFoundException("Image not found");
        }
    }

    public String saveImageToStorage(String uploadDirectory, MultipartFile imageFile) throws IOException {
        final String uniqueFileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

        final Path uploadPath = Path.of(uploadDirectory);
        final Path filePath = uploadPath.resolve(uniqueFileName);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Check if an image with the same data already exists
        if (isImageDuplicate(uploadDirectory, imageFile)) {
            throw new IllegalArgumentException("Image with the same data already exists");
        }

        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFileName;
    }

    private boolean isImageDuplicate(String uploadDirectory, MultipartFile imageFile) throws IOException {
        byte[] newImageBytes = imageFile.getBytes();

        // Iterate over existing images in the directory
        try (Stream<Path> paths = Files.walk(Path.of(uploadDirectory))) {
            return paths
                    .filter(Files::isRegularFile)
                    .anyMatch(path -> {
                        try {
                            byte[] existingImageBytes = Files.readAllBytes(path);
                            return Arrays.equals(newImageBytes, existingImageBytes);
                        } catch (IOException e) {
                            return false;
                        }
                    });
        }
    }
}
