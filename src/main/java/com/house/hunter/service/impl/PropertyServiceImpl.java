package com.house.hunter.service.impl;

import com.house.hunter.exception.IllegalAccessRequestException;
import com.house.hunter.exception.PropertyAlreadyExistsException;
import com.house.hunter.exception.PropertyNotFoundException;
import com.house.hunter.exception.UserNotFoundException;
import com.house.hunter.model.dto.property.CreatePropertyDTO;
import com.house.hunter.model.dto.property.GetPropertyDTO;
import com.house.hunter.model.dto.property.PropertySearchCriteriaDTO;
import com.house.hunter.model.dto.property.UpdatePropertyDTO;
import com.house.hunter.model.entity.Property;
import com.house.hunter.model.entity.User;
import com.house.hunter.repository.PropertyRepository;
import com.house.hunter.repository.UserRepository;
import com.house.hunter.security.CustomUserDetails;
import com.house.hunter.service.PropertyService;
import com.house.hunter.util.PropertySpecifications;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public UUID createProperty(CreatePropertyDTO propertyCreateDto) {
        if (!isAdmin() && !propertyCreateDto.getOwnerEmail().equals(getAuthenticatedUserEmail())) {
            throw new IllegalAccessRequestException();
        }

        // Check if a property with the same title already exists
        if (propertyRepository.existsByTitle(propertyCreateDto.getTitle())) {
            throw new PropertyAlreadyExistsException();
        }

        Property property = Optional.of(propertyCreateDto)
                .map(dto -> modelMapper.map(dto, Property.class))
                .map(prop -> {
                    User owner = userRepository.findByEmail(propertyCreateDto.getOwnerEmail())
                            .orElseThrow(() -> new UserNotFoundException(propertyCreateDto.getOwnerEmail()));
                    prop.setOwner(owner);
                    return prop;
                })
                .orElseThrow(IllegalStateException::new);

        return propertyRepository.save(property).getId();
    }


    @Override
    public Page<Property> searchProperties(PropertySearchCriteriaDTO searchCriteria, Pageable pageable) {
        return propertyRepository.findAll(PropertySpecifications.createSpecification(searchCriteria), pageable);
    }

    @Override
    @Transactional
    public CreatePropertyDTO updateProperty(UUID id, UpdatePropertyDTO updatePropertyDTO) {
        Property property;
        if (isAdmin()) {
            property = propertyRepository.findById(id).orElseThrow(PropertyNotFoundException::new);
        } else {
            property = propertyRepository.findByOwnerEmailAndId(getAuthenticatedUserEmail(), id)
                    .orElseThrow(PropertyNotFoundException::new);
        }
        modelMapper.map(updatePropertyDTO, property);
        return modelMapper.map(property, CreatePropertyDTO.class);
    }

    @Override
    public List<GetPropertyDTO> getProperties(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        return user.getProperties().stream()
                .map(property -> modelMapper.map(property, GetPropertyDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void deleteProperty(UUID id) {
        if (isAdmin()) {
            propertyRepository.deleteById(id);
        } else {
            propertyRepository.deleteByOwnerEmailAndId(getAuthenticatedUserEmail(), id).orElseThrow(IllegalAccessRequestException::new);
        }
    }

    private String getAuthenticatedUserEmail() {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return userDetails.getUsername();
        } catch (Exception e) {
            throw new IllegalAccessRequestException();
        }
    }

    private boolean isAdmin() {
        List userDetails = (List) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        return userDetails.get(0).toString().equals("ROLE_ADMIN");
    }
}
