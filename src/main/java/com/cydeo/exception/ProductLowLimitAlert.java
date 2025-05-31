package com.cydeo.exception;

public class ProductLowLimitAlert extends RuntimeException {
    public ProductLowLimitAlert(String message) {
        super(message);
    }
}
