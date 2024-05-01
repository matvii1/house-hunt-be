package com.house.hunter.service;

import com.house.hunter.model.dto.property.PropertyDTO;

import java.util.List;
import java.util.UUID;

public interface PropertyService {
    PropertyDTO createProperty(PropertyDTO propertyDto);

    PropertyDTO getPropertyById(UUID id);

    List<PropertyDTO> getAllProperties();

    PropertyDTO updateProperty(UUID id, PropertyDTO propertyDto);

    void deleteProperty(UUID id);
}

