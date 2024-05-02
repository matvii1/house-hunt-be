package com.house.hunter.controller;


import com.house.hunter.model.dto.property.ImageDTO;
import com.house.hunter.model.dto.property.PropertyDTO;
import com.house.hunter.service.ImageService;
import com.house.hunter.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;
    private final ImageService imageService;

    @Autowired
    public PropertyController(PropertyService propertyService, ImageService imageService) {
        this.propertyService = propertyService;
        this.imageService = imageService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD')")
    @Operation(summary = "Create property")
    public ResponseEntity<PropertyDTO> createProperty(@RequestBody PropertyDTO propertyDto) {
        PropertyDTO createdProperty = propertyService.createProperty(propertyDto);
        return new ResponseEntity<>(createdProperty, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific property by id")
    public ResponseEntity<PropertyDTO> getPropertyById(@PathVariable UUID id) {
        PropertyDTO propertyDto = propertyService.getPropertyById(id);
        return new ResponseEntity<>(propertyDto, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all properties")
    public ResponseEntity<List<PropertyDTO>> getAllProperties() {
        List<PropertyDTO> propertyDtos = propertyService.getAllProperties();
        return new ResponseEntity<>(propertyDtos, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update property")
    public ResponseEntity<PropertyDTO> updateProperty(@PathVariable UUID id, @RequestBody PropertyDTO propertyDto) {
        PropertyDTO updatedProperty = propertyService.updateProperty(id, propertyDto);
        return new ResponseEntity<>(updatedProperty, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete property")
    public ResponseEntity<Void> deleteProperty(@PathVariable UUID id) {
        propertyService.deleteProperty(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{propertyId}/images")
    @Operation(summary = "Upload image")
    public ResponseEntity<ImageDTO> uploadImage(@PathVariable UUID propertyId, @RequestParam("file") MultipartFile file) throws IOException {
        ImageDTO uploadedImage = imageService.uploadImage(propertyId, file);
        return new ResponseEntity<>(uploadedImage, HttpStatus.CREATED);
    }

    @GetMapping("/{propertyId}/images")
    @Operation(summary = "Get images by property")
    public ResponseEntity<List<ImageDTO>> getImagesByProperty(@PathVariable UUID propertyId) {
        List<ImageDTO> imageDtos = imageService.getImagesByProperty(propertyId);
        return new ResponseEntity<>(imageDtos, HttpStatus.OK);
    }

    @DeleteMapping("/{propertyId}/images/{imageId}")
    @Operation(summary = "Delete image")
    public ResponseEntity<Void> deleteImage(@PathVariable UUID propertyId, @PathVariable UUID imageId) {
        imageService.deleteImage(propertyId, imageId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

