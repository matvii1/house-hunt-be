package com.house.hunter.constant;

public enum ApartmentType {
    ONE_KK("1+kk"),
    ONE_ONE("1+1"),
    TWO_KK("2+kk"),
    TWO_ONE("2+1"),
    THREE_KK("3+kk"),
    THREE_ONE("3+1"),
    FOUR_KK("4+kk"),
    FOUR_ONE("4+1"),
    FIVE_KK("5+kk"),
    FIVE_ONE("5+1"),
    SIX_KK("6+kk"),
    SIX_ONE("6+1"),
    SEVEN_KK("7+kk"),
    SEVEN_ONE("7+1");

    private final String value;

    ApartmentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
