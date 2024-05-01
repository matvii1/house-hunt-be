package com.house.hunter.repository;

import com.house.hunter.model.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {
    List<Image> findByPropertyId(UUID propertyId);

    void deleteByIdAndPropertyId(UUID id, UUID propertyId);
}

