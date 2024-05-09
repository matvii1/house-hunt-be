package com.house.hunter.controller;


import com.house.hunter.model.dto.property.PropertyDTO;
import com.house.hunter.model.dto.property.PropertySearchCriteriaDTO;
import com.house.hunter.model.entity.Property;
import com.house.hunter.service.ImageService;
import com.house.hunter.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    /*    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD')")*/
    @Operation(summary = "Create property")
    public ResponseEntity<Void> createProperty(@RequestBody PropertyDTO propertyDto) {
        propertyService.createProperty(propertyDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public Page<Property> searchProperties(PropertySearchCriteriaDTO criteria, Pageable pageable) {
        return propertyService.searchProperties(criteria, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a specific property by id")
    public ResponseEntity<PropertyDTO> getPropertyById(@PathVariable UUID id) {
        PropertyDTO propertyDto = propertyService.getPropertyById(id);
        return ResponseEntity.ok(propertyDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update property")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PropertyDTO> updateProperty(@PathVariable UUID id, @RequestBody PropertyDTO propertyDto) {
        PropertyDTO updatedProperty = propertyService.updateProperty(id, propertyDto);
        return ResponseEntity.ok(updatedProperty);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete property")
    public void deleteProperty(@PathVariable UUID id) {
        propertyService.deleteProperty(id);
    }

    @PostMapping(path = "/{propertyId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Upload images of a property")
    public List<UUID> uploadImages(@PathVariable UUID propertyId,
                                   @ArraySchema(
                                           schema = @Schema(type = "string", format = "binary"),
                                           minItems = 1
                                   )
                                   @RequestPart(value = "images") MultipartFile[] images) throws IOException {
        return imageService.uploadImage(propertyId, images);
    }

    @GetMapping("/{propertyId}/images")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get images of a property")
    public ResponseEntity<List<byte[]>> getImagesByProperty(@PathVariable UUID propertyId) throws IOException {
        return ResponseEntity.ok(imageService.getImages(propertyId));
    }

    @DeleteMapping("/{propertyId}/images/{imageId}")
    @Operation(summary = "Delete image of a property")
    public ResponseEntity<Void> deleteImage(@PathVariable UUID imageId, @PathVariable UUID propertyId) throws IOException {
        imageService.deleteImage(imageId, propertyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{propertyId}/images")
    @Operation(summary = "Delete all images of a property")
    public ResponseEntity<Void> deleteImages(@PathVariable UUID propertyId) throws IOException {
        imageService.deleteImages(propertyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

