package com.talha.slwms.exception;

public class ShipmentNotFoundException extends RuntimeException{
    public ShipmentNotFoundException(String message) {
        super(message);
    }
}
