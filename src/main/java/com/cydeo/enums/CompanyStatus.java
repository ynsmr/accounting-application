package com.cydeo.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CompanyStatus {
    ACTIVE("Active"),
    PASSIVE("Passive");

    private final String value;

}
