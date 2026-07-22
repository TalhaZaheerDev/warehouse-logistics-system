package com.talha.slwms.model;

import com.talha.slwms.enums.VehicleType;

import java.io.Serializable;
import java.util.UUID;

public abstract class Vehicle implements Serializable {
    private final String vehicleId;
    private final String driverName;
    private boolean available;

    protected Vehicle(String driverName){
        if (driverName == null || driverName.isBlank()) {
            throw new IllegalArgumentException("Driver name required");
        }
        this.vehicleId = UUID.randomUUID().toString();
        this.driverName = driverName;
        this.available = true;
    }

    public String getVehicleId() {return this.vehicleId;}
    public String getDriverName() {return this.driverName;}
    public boolean isAvailable() {return this.available;}

    public void markAvailable() {this.available = true;}
    public void markUnavailable() {this.available = false;}

    public abstract double calculateDeliveryCapacity();
    public abstract double costPerKm();
    public abstract VehicleType getType();

    public double estimateCost(double distanceKm) {
        return distanceKm * costPerKm();
    }

    @Override
    public String toString() {
        return getType() + "{id=" + vehicleId + ", driver=" + driverName +
                ", available=" + available + ", capacity=" + calculateDeliveryCapacity() + "kg}";
    }

}
