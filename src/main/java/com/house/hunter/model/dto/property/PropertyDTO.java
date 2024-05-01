package com.house.hunter.model.dto.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDTO {
    private String id;
    private String title;
    private String address;
    private double price;
    private int squareMeters;
    private String type;
    private boolean isFurnished;
    private int floorNumber;
    private Date moveInDate;
    private String ownerId;
    private Set<ImageDTO> images;
}

