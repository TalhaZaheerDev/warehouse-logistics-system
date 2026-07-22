package com.talha.slwms.exception;

public class WarehouseFullException extends RuntimeException{
    public WarehouseFullException(String message){
        super(message);
    }
}
