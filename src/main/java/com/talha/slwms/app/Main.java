package com.talha.slwms.app;

import com.talha.slwms.enums.ShipmentPriority;
import com.talha.slwms.enums.ShipmentStatus;
import com.talha.slwms.exception.InvalidWeightException;
import com.talha.slwms.exception.ShipmentNotFoundException;
import com.talha.slwms.exception.VehicleUnavailableException;
import com.talha.slwms.exception.WarehouseFullException;
import com.talha.slwms.model.*;
import com.talha.slwms.report.ReportService;
import com.talha.slwms.repository.FileStorageUtil;
import com.talha.slwms.service.DeliveryEngine;
import com.talha.slwms.service.TrackingService;
import com.talha.slwms.simulation.SimulationEngine;
import com.talha.slwms.util.BillingUtil;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.*;

public class Main {

    public static void main(String[] args) throws InterruptedException {

//        System.out.println("========== MODULE 1: CUSTOMER ==========");
//        // Valid customers
//        Customer customer1 = new Customer("Ali Raza", "ali@mail.com", "0301-1111111", "Lahore");
//        Customer customer2 = new Customer("Sara Khan", "sara@mail.com", "0302-2222222", "Karachi");
//        System.out.println(customer1);
//        System.out.println(customer2);
//
//        // Deliberately trigger validation failure to prove the constructor protects itself
//        try {
//            Customer badCustomer = new Customer("Bad Guy", "not-an-email", "0300-0000000", "Nowhere");
//        } catch (IllegalArgumentException e) {
//            System.out.println("Expected failure caught: " + e.getMessage());
//        }
//
//
//        System.out.println("\n========== MODULE 2: SHIPMENT (ENUMS + COMPOSITION) ==========");
//        Shipment shipment1 = new Shipment(customer1, "Multan", 12.5, ShipmentPriority.EXPRESS);
//        System.out.println(shipment1);
//
//        shipment1.updateStatus(ShipmentStatus.DISPATCHED);
//        System.out.println("After dispatch: " + shipment1);
//
//        shipment1.updateStatus(ShipmentStatus.DELIVERED);
//        System.out.println("After delivery: " + shipment1);
//
//        // Lifecycle guard: once DELIVERED, no further status change is allowed
//        try {
//            shipment1.updateStatus(ShipmentStatus.IN_TRANSIT);
//        } catch (IllegalStateException e) {
//            System.out.println("Expected lifecycle failure: " + e.getMessage());
//        }
//
//
//        System.out.println("\n========== MODULE 3: WAREHOUSE (COLLECTIONS + COMPARATOR) ==========");
//        Warehouse warehouseA = new Warehouse("Lahore-Hub", 3); // small capacity on purpose, to test the full-exception
//
//        Shipment s1 = new Shipment(customer1, "Multan", 10, ShipmentPriority.STANDARD);
//        Shipment s2 = new Shipment(customer2, "Islamabad", 25, ShipmentPriority.URGENT);
//        Shipment s3 = new Shipment(customer1, "Faisalabad", 5, ShipmentPriority.EXPRESS);
//        Shipment s4 = new Shipment(customer2, "Peshawar", 40, ShipmentPriority.STANDARD);
//
//        warehouseA.receiveShipment(s1);
//        warehouseA.receiveShipment(s2);
//        warehouseA.receiveShipment(s3);
//
//        // Warehouse is now full (capacity 3) -> this must throw our CUSTOM exception (Module 8)
//        try {
//            warehouseA.receiveShipment(s4);
//        } catch (WarehouseFullException e) {
//            System.out.println("Expected custom exception caught: " + e.getMessage());
//        }
//
//        System.out.println("By priority (URGENT first): " + warehouseA.getShipmentsByPriority());
//        System.out.println("By weight (ascending): " + warehouseA.getShipmentsByWeight());
//
//        // Custom exception for a lookup miss
//        try {
//            warehouseA.findShipmentById("fake-id-123");
//        } catch (ShipmentNotFoundException e) {
//            System.out.println("Expected custom exception caught: " + e.getMessage());
//        }
//
//
//        System.out.println("\n========== MODULE 4: VEHICLES (INHERITANCE + POLYMORPHISM) ==========");
//        List<Vehicle> fleet = new ArrayList<>();
//        fleet.add(new Truck("Ali Driver", 2));   // capacity = 2 * 2500 = 5000kg
//        fleet.add(new Van("Sara Driver"));       // capacity = 1200kg
//        fleet.add(new Bike("Zain Driver"));      // capacity = 25kg
//
//        // Same loop, same method calls, DIFFERENT behavior per object -> polymorphism / dynamic dispatch
//        for (Vehicle v : fleet) {
//            System.out.println(v + " | cost for 50km: " + v.estimateCost(50));
//        }
//
//
//        System.out.println("\n========== MODULE 5: DELIVERY ENGINE (INTERFACES) ==========");
//        DeliveryEngine deliveryEngine = new DeliveryEngine();
//        Vehicle truck = fleet.get(0);
//
//        // s1 (10kg) easily fits in the truck (5000kg capacity)
//        deliveryEngine.assignAndDispatch(s1, truck, 80);
//
//        // Truck is now marked unavailable -> trying to dispatch again with the SAME vehicle must fail
//        try {
//            deliveryEngine.assignAndDispatch(s2, truck, 30);
//        } catch (VehicleUnavailableException e) {
//            System.out.println("Expected custom exception caught: " + e.getMessage());
//        }
//
//        deliveryEngine.markDelivered(s1, truck); // frees the truck up again
//        System.out.println("Truck available again: " + truck.isAvailable());
//
//
//        System.out.println("\n========== MODULE 6: TRACKING (HASHMAP) ==========");
//        TrackingService trackingService = new TrackingService();
//        trackingService.logLocation(s1.getShipmentId(), "Lahore-Hub");
//        trackingService.logLocation(s1.getShipmentId(), "Multan-Waypoint");
//        trackingService.logLocation(s1.getShipmentId(), "Multan-Delivered");
//
//        System.out.println("Current location: " + trackingService.getCurrentLocation(s1.getShipmentId()));
//        System.out.println("Full history: " + trackingService.getHistory(s1.getShipmentId()));
//
//
//        System.out.println("\n========== MODULE 7: BILLING (BIGDECIMAL) ==========");
//        double rawCharge = truck.estimateCost(80); // cost for the 80km delivery above
//        BigDecimal finalBill = BillingUtil.calculateTotal(rawCharge);
//        System.out.println("Raw charge: " + rawCharge + " | Final bill (with tax, 2dp): " + finalBill);
//
//
//        System.out.println("\n========== MODULE 8: CUSTOM EXCEPTIONS ==========");
//        // Already exercised above (WarehouseFullException, ShipmentNotFoundException, VehicleUnavailableException).
//        // Here we trigger InvalidWeightException specifically.
//        try {
//            Shipment invalid = new Shipment(customer1, "Nowhere", -5, ShipmentPriority.STANDARD);
//        } catch (InvalidWeightException e) {
//            System.out.println("Expected custom exception caught: " + e.getMessage());
//        }
//
//
//        System.out.println("\n========== MODULE 9: FILE STORAGE ==========");
//        List<Customer> customersToSave = List.of(customer1, customer2);
//
//        // Text-based CSV write + read
//        FileStorageUtil.writeCustomersToCsv(customersToSave, "data/customers.csv");
//        List<String> readBack = FileStorageUtil.readLines("data/customers.csv");
//        System.out.println("CSV contents read back from disk:");
//        readBack.forEach(System.out::println);
//
//        // Full object serialization: save the whole Warehouse, then load it into a NEW variable
//        FileStorageUtil.saveObject(warehouseA, "data/warehouseA.ser");
//        Warehouse restoredWarehouse = (Warehouse) FileStorageUtil.loadObject("data/warehouseA.ser");
//        System.out.println("Original : " + warehouseA);
//        System.out.println("Restored : " + restoredWarehouse);
//
//
//        System.out.println("\n========== MODULE 10: REPORTS (STREAMS/OPTIONAL) ==========");
//        ReportService reportService = new ReportService();
//
//        List<Shipment> allShipments = new ArrayList<>(warehouseA.getShipments());
//        allShipments.add(s1); // include the delivered one too, for a richer report
//
//        Map<ShipmentStatus, List<Shipment>> byStatus = reportService.groupByStatus(allShipments);
//        System.out.println("Grouped by status: " + byStatus.keySet());
//
//        // Fake charges map, just for this demo (id -> amount)
//        Map<String, Double> fakeCharges = new HashMap<>();
//        fakeCharges.put(s1.getShipmentId(), rawCharge);
//        double revenue = reportService.totalRevenue(allShipments, fakeCharges);
//        System.out.println("Total revenue (delivered only): " + revenue);
//
//        // Optional in action: handles both "found" and "not found" explicitly, no null checks
//        Optional<Customer> topCustomer = reportService.topCustomerByShipmentCount(allShipments);
//        topCustomer.ifPresentOrElse(
//                c -> System.out.println("Top customer: " + c.getName()),
//                () -> System.out.println("No shipments yet")
//        );
//
//        OptionalDouble avgWeight = reportService.averageWeight(allShipments);
//        System.out.println("Average shipment weight: " + avgWeight.orElse(0.0));
//

        System.out.println("\n========== MODULE 11: SIMULATION (THREADS + CONCURRENCY) ==========");
        ReportService reportService = new ReportService();
        SimulationEngine simulationEngine = new SimulationEngine();

        List<Customer> simCustomers = simulationEngine.generateCustomers(500);

        List<Warehouse> simWarehouses = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            simWarehouses.add(new Warehouse("Warehouse-" + i, 500)); // 5 warehouses x 500 capacity = 2500 total room
        }

        // Runs 2000 shipment attempts across a 20-thread pool, hitting synchronized receiveShipment()
        simulationEngine.simulateShipment(simCustomers, simWarehouses, 2000);

        // TEST 1: total received must never exceed total capacity (proves synchronization worked)
        int totalReceived = simWarehouses.stream().mapToInt(Warehouse::getCurrentLoad).sum();
        int totalCapacity = simWarehouses.stream().mapToInt(Warehouse::getCapacity).sum();
        System.out.println("Total received: " + totalReceived + " / capacity: " + totalCapacity);

        // TEST 2: no single warehouse should ever exceed its own capacity
        for (Warehouse w : simWarehouses) {
            System.out.println(w + " | load=" + w.getCurrentLoad() + "/" + w.getCapacity());
        }

        // TEST 3: every shipment that made it in must have the correct status (no partially-finished thread work)
        long wrongStatusCount = simWarehouses.stream()
                .flatMap(w -> w.getShipments().stream())
                .filter(sh -> sh.getStatus() != ShipmentStatus.IN_WAREHOUSE)
                .count();
        System.out.println("Shipments with wrong status (should be 0): " + wrongStatusCount);


        System.out.println("\n========== MODULE 12: ANALYTICS (REUSES REPORTSERVICE) ==========");
        List<Shipment> allSimShipments = simWarehouses.stream()
                .flatMap(w -> w.getShipments().stream())
                .collect(Collectors.toList());

        Optional<Customer> busiestCustomer = reportService.topCustomerByShipmentCount(allSimShipments);
        busiestCustomer.ifPresentOrElse(
                c -> System.out.println("Most active simulated customer: " + c.getName()),
                () -> System.out.println("No data")
        );

        // Busiest warehouse = the one with the highest current load
        Optional<Warehouse> busiestWarehouse = simWarehouses.stream()
                .max(Comparator.comparingInt(Warehouse::getCurrentLoad));
        busiestWarehouse.ifPresent(w -> System.out.println("Busiest warehouse: " + w));

        System.out.println("\n========== ALL MODULES EXECUTED ==========");
    }
}