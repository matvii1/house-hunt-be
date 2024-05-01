package com.house.hunter.service;

import com.house.hunter.model.dto.property.ImageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ImageService {
    ImageDTO uploadImage(UUID propertyId, MultipartFile file) throws IOException;

    List<ImageDTO> getImagesByProperty(UUID propertyId);

    void deleteImage(UUID propertyId, UUID imageId);
}
