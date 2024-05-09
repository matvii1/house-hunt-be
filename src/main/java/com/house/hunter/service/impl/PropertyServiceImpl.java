

package com.house.hunter.service.impl;

import com.house.hunter.constant.ApartmentType;
import com.house.hunter.exception.UserNotFoundException;
import com.house.hunter.model.dto.property.PropertyDTO;
import com.house.hunter.model.dto.property.PropertySearchCriteriaDTO;
import com.house.hunter.model.entity.Property;
import com.house.hunter.model.entity.User;
import com.house.hunter.repository.PropertyRepository;
import com.house.hunter.repository.UserRepository;
import com.house.hunter.service.PropertyService;
import com.house.hunter.util.PropertySpecifications;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PropertyServiceImpl(PropertyRepository propertyRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public void createProperty(PropertyDTO propertyDto) {
        Property property = modelMapper.map(propertyDto, Property.class);
        User owner = userRepository.findByEmail(propertyDto.getOwnerEmail())
                .orElseThrow(() -> new UserNotFoundException(propertyDto.getOwnerEmail()));

        property.setOwner(owner);
        propertyRepository.save(property);
    }

    @Override
    public PropertyDTO getPropertyById(UUID id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
        return modelMapper.map(property, PropertyDTO.class);
    }

    @Override
    public List<PropertyDTO> getAllProperties() {
        List<Property> properties = propertyRepository.findAll();
        return properties.stream()
                .map(property -> modelMapper.map(property, PropertyDTO.class))
                .toList();
    }

    @Override
    public Page<Property> searchProperties(PropertySearchCriteriaDTO searchCriteria, Pageable pageable) {
        return propertyRepository.findAll(PropertySpecifications.createSpecification(searchCriteria), pageable);
    }

    @Override
    @Transactional
    public PropertyDTO updateProperty(UUID id, PropertyDTO propertyDto) {
        Property existingProperty = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
        modelMapper.map(propertyDto, existingProperty);
        Property updatedProperty = propertyRepository.save(existingProperty);
        return modelMapper.map(updatedProperty, PropertyDTO.class);
    }

    @Override
    @Transactional
    public void deleteProperty(UUID id) {
        propertyRepository.deleteById(id);
    }
}

