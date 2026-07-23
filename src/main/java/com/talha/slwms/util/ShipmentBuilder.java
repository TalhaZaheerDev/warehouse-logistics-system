package com.talha.slwms.util;

import com.talha.slwms.enums.ShipmentPriority;
import com.talha.slwms.model.Customer;
import com.talha.slwms.model.Shipment;

public class ShipmentBuilder {
    private Customer sender;
    private String destinationAddress;
    private double weightKg;
    private ShipmentPriority priority=ShipmentPriority.STANDARD;

    public ShipmentBuilder sender(Customer c) {this.sender = c; return this;}
    public ShipmentBuilder destination(String d) {this.destinationAddress = d; return this;}
    public ShipmentBuilder  weight(double w) {this.weightKg = w; return this;}
    public ShipmentBuilder priority(ShipmentPriority p) {this.priority = p; return this;}
    public Shipment build() {
        return new Shipment(sender, destinationAddress, weightKg, priority);
    }
}
