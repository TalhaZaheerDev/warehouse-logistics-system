package com.talha.slwms.service;

import com.talha.slwms.enums.ShipmentStatus;
import com.talha.slwms.model.Shipment;
import com.talha.slwms.model.Vehicle;

public class DeliveryEngine {
    public void assignAndDispatch(Shipment shipment, Vehicle vehicle, double distanceKm) {
        if(!vehicle.isAvailable()) {
            throw new IllegalArgumentException("Vehicle is not available: " +vehicle.getVehicleId());
        }

        if(shipment.getWeightKg() > vehicle.calculateDeliveryCapacity()){
            throw new IllegalArgumentException("Vehicle Can't carry this shipment's weight");
        }

        vehicle.markUnavailable();
        shipment.updateStatus(ShipmentStatus.DISPATCHED);
        double cost = vehicle.estimateCost(distanceKm);
        System.out.println("Dispatched "+ shipment.getShipmentId() + " via "+ vehicle.getType() + " | cost : " +cost);
    }

    public void markDelivered(Shipment shipment, Vehicle vehicle) {
        shipment.updateStatus(ShipmentStatus.DELIVERED);
        vehicle.markAvailable();
    }
}
