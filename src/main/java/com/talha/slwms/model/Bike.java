package com.talha.slwms.model;

import com.talha.slwms.enums.VehicleType;

public class Bike extends Vehicle {

    public Bike(String driverName) {
        super(driverName);
    }

    @Override
    public double calculateDeliveryCapacity() {
        return 25.0;
    }

    @Override
    public double costPerKm() {
        return 0.5;
    }

    @Override
    public VehicleType getType() {
        return VehicleType.BIKE;
    }
}
