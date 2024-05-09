package com.house.hunter.service.impl;

import com.house.hunter.exception.FileOperationException;
import com.house.hunter.exception.ImageNotFoundException;
import com.house.hunter.model.entity.Image;
import com.house.hunter.model.entity.Property;
import com.house.hunter.repository.ImageRepository;
import com.house.hunter.repository.PropertyRepository;
import com.house.hunter.service.ImageService;
import com.house.hunter.util.ImageUtil;
import jakarta.el.PropertyNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final PropertyRepository propertyRepository;

    public ImageServiceImpl(ImageRepository imageRepository, PropertyRepository propertyRepository) {
        this.imageRepository = imageRepository;
        this.propertyRepository = propertyRepository;
    }

    @Value("${image.directory}")
    private String imageDirectory;


    private final ImageUtil imageUtil = ImageUtil.getInstance();

    @Transactional
    public List<UUID> uploadImage(UUID propertyId, MultipartFile[] images) throws IOException {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with id: " + propertyId));
        List<Image> imagesWillBeSaved = new ArrayList<>();
        for (MultipartFile imageFile : images) {
            String filename = imageUtil.saveImageToStorage(imageDirectory, imageFile);
            imagesWillBeSaved.add(new Image(null, filename, property));
        }
        List<Image> savedImages = imageRepository.saveAll(imagesWillBeSaved);
        return savedImages.stream().map(Image::getId).collect(Collectors.toList());
    }

    public List<byte[]> getImages(UUID propertyId) throws PropertyNotFoundException {
        List<Image> images = imageRepository.findImagesByPropertyId(propertyId).orElseThrow(() -> new PropertyNotFoundException("Property not found with id: " + propertyId));
        return images.stream()
                .map(image -> {
                    try {
                        return imageUtil.getImage(imageDirectory, image.getFileName());
                    } catch (IOException e) {
                        throw new FileOperationException(e.getMessage());
                    }
                })
                .filter(Objects::nonNull)
                .flatMap(byteArray -> byteArray != null ? Stream.of(byteArray) : Stream.empty())
                .toList();
    }

    @Transactional
    public void deleteImage(UUID imageId, UUID propertyId) {
        Image image = imageRepository.findImageByIdAndPropertyId(imageId, propertyId).orElseThrow(ImageNotFoundException::new);
        try {
            imageUtil.deleteImage(imageDirectory, image.getFileName());
        } catch (IOException e) {
            throw new FileOperationException(e.getMessage());
        }
        imageRepository.deleteByIdAndPropertyId(imageId, propertyId);
    }

    @Transactional
    public void deleteImages(UUID propertyId) {
        List<Image> images = imageRepository.findImagesByPropertyId(propertyId).orElseThrow(PropertyNotFoundException::new);
        imageRepository.deleteAll(images);
        try {
            for (Image image : images) {
                imageUtil.deleteImage(imageDirectory, image.getFileName());
            }
        } catch (IOException e) {
            throw new FileOperationException(e.getMessage());
        }
    }

}
