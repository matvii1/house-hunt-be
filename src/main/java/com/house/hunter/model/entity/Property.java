package com.house.hunter.model.entity;

import com.house.hunter.constant.AdType;
import com.house.hunter.constant.ApartmentType;
import com.house.hunter.constant.IsFurnished;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @NotEmpty(message = "Title is required")
    private String title;

    @NotEmpty(message = "Address is required")
    private String address;

    @DecimalMin(value = "0", message = "Price must be a positive number")
    @NotNull
    private double price;

    @Positive(message = "Square meters must be positive")
    @NotNull
    private int squareMeters;

    @NotEmpty(message = "Description is required")
    @Size(min = 20, message = "Description must be at least 10 characters long")
    private String description;

    @Enumerated(EnumType.STRING)
    private IsFurnished isFurnished;

    @NotNull(message = "Number of rooms is required")
    private int numberOfRooms;

    @PositiveOrZero(message = "Floor number cannot be negative")
    @NotNull
    private int floorNumber;

    @Temporal(TemporalType.DATE)
    @FutureOrPresent(message = "The date must be in the future or present")
    @NotNull
    private Date availableFrom;

    @Enumerated(EnumType.STRING)
    @NotNull
    private AdType adType;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ApartmentType apartmentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @NotNull
    private User owner;

}
