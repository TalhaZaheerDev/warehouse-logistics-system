package com.talha.slwms.model;

import com.talha.slwms.enums.VehicleType;

public class Van extends Vehicle {

    public Van(String driverName){
        super(driverName);
    }

    @Override
    public double calculateDeliveryCapacity() {
        return 1200.0;
    }

    @Override
    public double costPerKm() {
        return 1.8;
    }

    @Override
    public VehicleType getType() {
        return VehicleType.VAN;
    }
}
