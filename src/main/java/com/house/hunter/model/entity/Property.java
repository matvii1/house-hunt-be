package com.house.hunter.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.annotations.GenericGenerator;

import java.awt.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @NotEmpty(message = "Title is required")
    private String title;

    @NotEmpty(message = "Address is required")
    private String address;

    private double locationX;
    private double locationY;

    @DecimalMin(value = "0", message = "Price must be a positive number")
    private double price;

    @Positive(message = "Square meters must be positive")
    private int squareMeters;

    @NotEmpty(message = "Type is required")
    private String type;

    private boolean isFurnished;

    @PositiveOrZero(message = "Floor number cannot be negative")
    private int floorNumber;

    @Temporal(TemporalType.DATE)
    private Date moveInDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Image> images = new HashSet<>();

    // Constructors, getters, and setters
}

