package com.house.hunter.service.impl;

import com.house.hunter.constant.UserAccountStatus;
import com.house.hunter.constant.UserVerificationStatus;
import com.house.hunter.exception.IllegalAccessRequestException;
import com.house.hunter.exception.PropertyAlreadyExistsException;
import com.house.hunter.exception.PropertyNotFoundException;
import com.house.hunter.exception.UserNotFoundException;
import com.house.hunter.model.dto.property.CreatePropertyDTO;
import com.house.hunter.model.dto.property.GetPropertyDTO;
import com.house.hunter.model.dto.property.PropertySearchCriteriaDTO;
import com.house.hunter.model.dto.property.UpdatePropertyDTO;
import com.house.hunter.model.dto.search.PropertyDTO;
import com.house.hunter.model.dto.search.UserDTO;
import com.house.hunter.model.entity.Property;
import com.house.hunter.model.entity.User;
import com.house.hunter.repository.PropertyRepository;
import com.house.hunter.repository.UserRepository;
import com.house.hunter.security.CustomUserDetails;
import com.house.hunter.service.PropertyService;
import com.house.hunter.util.PropertySpecifications;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PropertyServiceImpl implements PropertyService {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PropertyServiceImpl.class);

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
        if (!isAdmin() && !propertyCreateDto.getOwnerEmail().equals(getAuthenticatedUserEmail(false))) {
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
                    prop.setCreatedAt(LocalDateTime.now());
                    return prop;
                })
                .orElseThrow(IllegalStateException::new);

        return propertyRepository.save(property).getId();
    }


    @Override
    public Page<PropertyDTO> searchProperties(PropertySearchCriteriaDTO searchCriteria, Pageable pageable) {
        Optional<User> requestMaker = userRepository.findByEmail(getAuthenticatedUserEmail(true));
        Page<Property> properties = propertyRepository.findAll(PropertySpecifications.createSpecification(searchCriteria), pageable);
        if (requestMaker.isPresent()) {
            LOGGER.info("User found with email: {}", requestMaker.get().getEmail());
            User user = requestMaker.get();
            if (user.getVerificationStatus() == UserVerificationStatus.VERIFIED && user.getAccountStatus() == UserAccountStatus.ACTIVE) {
                LOGGER.info("User is verified and active, showing phone number");
                return properties.map(property -> convertToDTO(property, true));
            }
        }
        return properties.map(property -> convertToDTO(property, false));
    }


    @Override
    @Transactional
    public UpdatePropertyDTO updateProperty(UUID id, UpdatePropertyDTO updatePropertyDTO) {
        Property property;
        if (isAdmin()) {
            property = propertyRepository.findById(id).orElseThrow(PropertyNotFoundException::new);
            LOGGER.info("Admin user found, updating property with id: {}", id);
        } else {
            String email = getAuthenticatedUserEmail(false);
            LOGGER.info("Non-admin user {} is updating property with id: {}", email, id);
            property = propertyRepository.findByOwnerEmailAndId(getAuthenticatedUserEmail(false), id)
                    .orElseThrow(PropertyNotFoundException::new);
        }
        return modelMapper.map(property, UpdatePropertyDTO.class);
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
    public PropertyDTO getPropertyById(UUID id) {
        Optional<User> requestMaker = userRepository.findByEmail(getAuthenticatedUserEmail(true));
        Property property = propertyRepository.findById(id).orElseThrow(PropertyNotFoundException::new);
        if (requestMaker.isPresent()) {
            LOGGER.info("User found with email: {}", requestMaker.get().getEmail());
            User user = requestMaker.get();
            if (user.getVerificationStatus() == UserVerificationStatus.VERIFIED && user.getAccountStatus() == UserAccountStatus.ACTIVE) {
                LOGGER.info("User is verified and active, showing phone number");
                return convertToDTO(property, true);
            }
        }
        return convertToDTO(property, false);
    }


    @Override
    @Transactional
    public void deleteProperty(UUID id) {
        if (isAdmin()) {
            LOGGER.info("Admin user found, deleting property with id: {}", id);
            propertyRepository.deleteById(id);
        } else {
            LOGGER.info("Non-admin user {} is deleting property with id: {}", getAuthenticatedUserEmail(false), id);
            propertyRepository.deleteByOwnerEmailAndId(getAuthenticatedUserEmail(false), id).orElseThrow(IllegalAccessRequestException::new);
        }
    }

    private String getAuthenticatedUserEmail(boolean isEndpointPublic) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return userDetails.getUsername();
        } catch (Exception e) {
            if (isEndpointPublic) {
                return null;
            }
            throw new IllegalAccessRequestException();
        }
    }

    private boolean isAdmin() {
        List userDetails = (List) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        return userDetails.get(0).toString().equals("ROLE_ADMIN");
    }

    private PropertyDTO convertToDTO(Property property, boolean includeOwner) {
        PropertyDTO propertyDTO = modelMapper.map(property, PropertyDTO.class);
        User owner = property.getOwner();
        // Check if the owner is null
        if (owner != null) {
            // Check if the phone number should be included
            if (!includeOwner) {
                // Set the phone number to null
                return propertyDTO;
            }
            UserDTO ownerDTO = modelMapper.map(owner, UserDTO.class);
            propertyDTO.setOwner(ownerDTO);
        }
        return propertyDTO;
    }
}
