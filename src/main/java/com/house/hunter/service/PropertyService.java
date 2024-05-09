package com.house.hunter.service;

import com.house.hunter.model.dto.property.CreatePropertyDTO;
import com.house.hunter.model.dto.property.GetPropertyDTO;
import com.house.hunter.model.dto.property.PropertySearchCriteriaDTO;
import com.house.hunter.model.dto.property.UpdatePropertyDTO;
import com.house.hunter.model.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PropertyService {
    UUID createProperty(CreatePropertyDTO propertyCreateDto);

    CreatePropertyDTO updateProperty(UUID id, UpdatePropertyDTO updatePropertyDTO);

    void deleteProperty(UUID id);

    Page<Property> searchProperties(PropertySearchCriteriaDTO searchCriteria, Pageable pageable);

    List<GetPropertyDTO> getProperties(String email);
}

