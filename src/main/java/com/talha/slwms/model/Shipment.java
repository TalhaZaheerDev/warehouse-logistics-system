package com.talha.slwms.model;

import com.talha.slwms.enums.ShipmentPriority;
import com.talha.slwms.enums.ShipmentStatus;
import com.talha.slwms.exception.InvalidWeightException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Shipment implements Serializable {
    private final String shipmentId;
    private final Customer sender;
    private final String destinationAddress;
    private final double weightKg;
    private ShipmentStatus  status;
    private final ShipmentPriority priority;
    private final LocalDateTime createdAt;

    public Shipment(Customer sender, String destinationAddress, double weightKg, ShipmentPriority priority) {
        if(sender == null) {
            throw new IllegalArgumentException("sender can't be null");
        }
        if(weightKg <= 0) {
            throw new InvalidWeightException("weightKg must be positive");
        }

        this.shipmentId = UUID.randomUUID().toString();
        this.sender = sender;
        this.destinationAddress = destinationAddress;
        this.weightKg = weightKg;
        this.priority = priority;
        this.status=ShipmentStatus.CREATED;
        this.createdAt = LocalDateTime.now();
    }


    public String getShipmentId() { return shipmentId; }
    public Customer getSender() { return sender; }
    public String getDestinationAddress() { return destinationAddress; }
    public double getWeightKg() { return weightKg; }
    public ShipmentStatus getStatus() { return status; }
    public ShipmentPriority getPriority() { return priority; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void updateStatus(ShipmentStatus newStatus) {
        if(this.status == ShipmentStatus.DELIVERED || this.status == ShipmentStatus.CANCELLED) {
            throw new IllegalArgumentException("Can't change status of a " + this.status + " shipment");
        }
        this.status = newStatus;
    }

    @Override
    public String toString() {
        return "Shipment{id="+shipmentId+",sender="+sender.getName()+",status="+status+",priority="+priority+",weight="+weightKg+"kg}";
    }
}
