package com.shopme.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ERole {
    CUSTOMER("Customer"), ADMIN("Admin"), SHIPPER("Shipper"),
    SELLER("Seller");

    public static final String ROLE_PREFIX = "ROLE_";
    private String name;

    ERole(String role) {
        this.name = role;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static ERole fromString(String name) {
        for (ERole eRole : ERole.values()) {
            if (eRole.getName().equalsIgnoreCase(name)) {
                return eRole;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + name);
    }
}
