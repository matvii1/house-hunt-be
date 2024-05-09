package com.house.hunter.model.dto.property;

import com.house.hunter.constant.AdType;
import com.house.hunter.constant.ApartmentType;
import com.house.hunter.constant.IsFurnished;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PropertySearchCriteriaDTO {
    private String title;
    private Double minPrice;
    private Double maxPrice;
    private int squareMeters;
    private IsFurnished isFurnished;
    private Integer minFloorNumber;
    private Integer maxFloorNumber;
    private Date availableFrom;
    private Integer minRooms;
    private Integer maxRooms;
    private AdType adType;
    private ApartmentType apartmentType;
    private String address;
    private String description;
    private String ownerEmail;
}
