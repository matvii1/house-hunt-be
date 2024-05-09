package com.house.hunter.util;

import com.house.hunter.model.dto.property.PropertySearchCriteriaDTO;
import com.house.hunter.model.entity.Property;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class PropertySpecifications {

    public static Specification<Property> createSpecification(PropertySearchCriteriaDTO criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getTitle() != null && !criteria.getTitle().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + criteria.getTitle().toLowerCase() + "%"));
            }
            if (criteria.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), criteria.getMinPrice()));
            }
            if (criteria.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), criteria.getMaxPrice()));
            }
            if (criteria.getSquareMeters() > 0) {
                predicates.add(criteriaBuilder.equal(root.get("squareMeters"), criteria.getSquareMeters()));
            }
            if (criteria.getIsFurnished() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isFurnished"), criteria.getIsFurnished()));
            }
            if (criteria.getMinFloorNumber() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("floorNumber"), criteria.getMinFloorNumber()));
            }
            if (criteria.getMaxFloorNumber() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("floorNumber"), criteria.getMaxFloorNumber()));
            }
            if (criteria.getAvailableFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("availableFrom"), criteria.getAvailableFrom()));
            }
            if (criteria.getMinRooms() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("numberOfRooms"), criteria.getMinRooms()));
            }
            if (criteria.getMaxRooms() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("numberOfRooms"), criteria.getMaxRooms()));
            }
            if (criteria.getAdType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("adType"), criteria.getAdType()));
            }
            if (criteria.getApartmentType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("apartmentType"), criteria.getApartmentType()));
            }
            if (criteria.getAddress() != null && !criteria.getAddress().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), "%" + criteria.getAddress().toLowerCase() + "%"));
            }
            if (criteria.getDescription() != null && !criteria.getDescription().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + criteria.getDescription().toLowerCase() + "%"));
            }
            if (criteria.getOwnerEmail() != null && !criteria.getOwnerEmail().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.join("owner").get("email"), criteria.getOwnerEmail()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}


