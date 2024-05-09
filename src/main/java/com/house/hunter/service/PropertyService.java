package com.house.hunter.service;

import com.house.hunter.model.dto.property.PropertyDTO;
import com.house.hunter.model.dto.property.PropertySearchCriteriaDTO;
import com.house.hunter.model.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PropertyService {
    void createProperty(PropertyDTO propertyDto);

    PropertyDTO getPropertyById(UUID id);

    List<PropertyDTO> getAllProperties();

    PropertyDTO updateProperty(UUID id, PropertyDTO propertyDto);

    void deleteProperty(UUID id);

    Page<Property> searchProperties(PropertySearchCriteriaDTO searchCriteria, Pageable pageable);
}

