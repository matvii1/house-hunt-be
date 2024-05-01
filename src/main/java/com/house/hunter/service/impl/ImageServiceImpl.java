package com.house.hunter.service.impl;

import com.house.hunter.model.dto.property.ImageDTO;
import com.house.hunter.model.entity.Image;
import com.house.hunter.model.entity.Property;
import com.house.hunter.repository.ImageRepository;
import com.house.hunter.repository.PropertyRepository;
import com.house.hunter.service.ImageService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final PropertyRepository propertyRepository;
    private final ModelMapper modelMapper;

    public ImageDTO uploadImage(UUID propertyId, MultipartFile file) throws IOException {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + propertyId));
        Image image = new Image();
        image.setData(file.getBytes());
        image.setProperty(property);
        Image savedImage = imageRepository.save(image);
        return modelMapper.map(savedImage, ImageDTO.class);
    }

    public List<ImageDTO> getImagesByProperty(UUID propertyId) {
        List<Image> images = imageRepository.findByPropertyId(propertyId);
        return images.stream()
                .map(image -> modelMapper.map(image, ImageDTO.class))
                .collect(Collectors.toList());
    }

    public void deleteImage(UUID propertyId, UUID imageId) {
        imageRepository.deleteByIdAndPropertyId(imageId, propertyId);
    }
}
