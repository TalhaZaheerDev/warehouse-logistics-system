package com.talha.slwms.model;

import com.talha.slwms.enums.VehicleType;

public class Truck extends Vehicle{


    private final int axles;

    public Truck(String driverName, int axles) {
        super(driverName);
        this.axles = axles;

    }



    @Override
    public double calculateDeliveryCapacity() {
        return axles*2500;
    }

    @Override
    public double costPerKm() {
        return 3.5;
    }

    @Override
    public VehicleType getType() {
        return VehicleType.TRUCK;
    }
}
