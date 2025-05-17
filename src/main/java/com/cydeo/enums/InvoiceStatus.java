package com.cydeo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
public enum InvoiceStatus {
    AWAITING_APPROVAL("Awaiting Approval"),
    APPROVED("Approved");
    
    private final String value;
}
