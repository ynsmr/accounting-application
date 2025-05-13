package com.cydeo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public enum ClientVendorType {
    
    VENDOR("Vendor"),
    CLIENT("Client");
    
    private final String value;
}
