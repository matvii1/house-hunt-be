package com.house.hunter.model.dto.property;

import com.house.hunter.constant.AdType;
import com.house.hunter.constant.ApartmentType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class PropertySearchCriteriaDTO {

    @Size(max = 100, message = "Title cannot be longer than 100 characters")
    @NotEmpty
    private String title;

    @Size(max = 200, message = "Address cannot be longer than 200 characters")
    @NotEmpty
    private String address;

    @DecimalMin(value = "0", message = "Price must be a positive number")
    @NotNull
    private Double price;

    @Positive(message = "Square meters must be positive")
    @NotNull
    private Integer squareMeters;

    @Size(max = 1000, message = "Description cannot be longer than 1000 characters")
    @NotEmpty
    private String description;

    @Size(max = 50, message = "Type cannot be longer than 50 characters")
    @NotEmpty
    private String type;

    @NotNull
    private Boolean isFurnished;

    @PositiveOrZero(message = "Number of rooms cannot be negative")
    @NotNull
    private Integer numberOfRooms;

    @PositiveOrZero(message = "Floor number cannot be negative")
    @NotNull
    private Integer floorNumber;

    @FutureOrPresent(message = "The date must be in the future or present")
    @NotNull
    private Date availableFrom;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private AdType adType;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private ApartmentType apartmentType;

    @NotNull
    private UUID ownerId;

}