package com.house.hunter.constant;

public enum UserRole {
    ADMIN,
    LANDLORD,
    TENANT,
    GUEST;

    public String getRole() {
        return this.name();
    }
}
