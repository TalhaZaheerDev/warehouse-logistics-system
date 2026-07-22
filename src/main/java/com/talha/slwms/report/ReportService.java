package com.talha.slwms.report;

import com.talha.slwms.enums.ShipmentStatus;
import com.talha.slwms.model.Customer;
import com.talha.slwms.model.Shipment;
import com.talha.slwms.model.Warehouse;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReportService {
    public double calculateTotalRevenue(List<Shipment> shipments, Map<String, Double> shipmentCosts) {
        return shipments.stream()
                .filter(s->s.getStatus() == ShipmentStatus.DELIVERED)
                .mapToDouble(s->shipmentCosts.getOrDefault(s.getShipmentId(), 0.0))
                .sum();
    }

    public Map<ShipmentStatus, Long> countByStatus(List<Shipment> shipments) {
        return shipments.stream()
                .collect(Collectors.groupingBy(Shipment::getStatus, Collectors.counting()));
    }

    public Optional<Warehouse> findBusiestWarehouse(List<Warehouse> warehouses) {
        return warehouses.stream()
                .max(Comparator.comparingInt(Warehouse::getCurrentLoad));
    }

    public Optional<Map.Entry<Customer, Long>> findTopCustomer(List<Shipment> shipments) {
        return shipments.stream()
                .collect(Collectors.groupingBy(Shipment::getSender, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue());
    }

    public List<Shipment> findPendingShipments(List<Shipment> shipments) {
        return shipments.stream()
                .filter(s -> s.getStatus() != ShipmentStatus.DELIVERED
                        && s.getStatus() != ShipmentStatus.CANCELLED)
                .collect(Collectors.toList());
    }
}
