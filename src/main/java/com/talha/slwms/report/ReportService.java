package com.talha.slwms.report;

import com.talha.slwms.enums.ShipmentStatus;
import com.talha.slwms.model.Customer;
import com.talha.slwms.model.Shipment;
import com.talha.slwms.model.Warehouse;

import java.util.*;
import java.util.stream.Collectors;

public class ReportService {

    // Group shipments by status — one line replaces a manual HashMap-building loop
    public Map<ShipmentStatus, List<Shipment>> groupByStatus(List<Shipment> shipments) {
        return shipments.stream()
                .collect(Collectors.groupingBy(Shipment::getStatus));
    }

    // Total revenue from delivered shipments only
    public double totalRevenue(List<Shipment> shipments, Map<String, Double> shipmentCharges) {
        return shipments.stream()
                .filter(s -> s.getStatus() == ShipmentStatus.DELIVERED)
                .mapToDouble(s -> shipmentCharges.getOrDefault(s.getShipmentId(), 0.0))
                .sum();
    }

    // Optional: might not find a "top" customer if list is empty
    public Optional<Customer> topCustomerByShipmentCount(List<Shipment> shipments) {
        Map<Customer, Long> countByCustomer = shipments.stream()
                .collect(Collectors.groupingBy(Shipment::getSender, Collectors.counting()));

        return countByCustomer.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    // Average weight of all shipments — Optional in case list is empty
    public OptionalDouble averageWeight(List<Shipment> shipments) {
        return shipments.stream()
                .mapToDouble(Shipment::getWeightKg)
                .average();
    }

    // Count shipments per warehouse — "most busy warehouse" building block
    public Map<Warehouse, Integer> countPerWarehouse(List<Warehouse> warehouses) {
        return warehouses.stream()
                .collect(Collectors.toMap(w -> w, Warehouse::getCurrentLoad));
    }
}