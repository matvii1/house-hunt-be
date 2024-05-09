package com.house.hunter.model.dto.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetPropertyDTO {
    private String title;
    private String address;
    private double price;
    private int squareMeters;
    private String description;
    private String isFurnished;
    private int numberOfRooms;
    private int floorNumber;
    private Date availableFrom;
    private String adType;
    private String apartmentType;
    private String ownerEmail;
}