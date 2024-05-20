package com.house.hunter.constant;

public enum RequestFormSubject {
    PROPERTY,
    COMPLAINT,
    OTHER;

    // Constant regular expression that matches the enum values
    public static final String PATTERN = "PROPERTY|COMPLAINT|OTHER";

    @Override
    public String toString() {
        return name().toUpperCase();
    }
}
