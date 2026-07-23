package com.talha.slwms.app;

import com.talha.slwms.enums.ShipmentPriority;
import com.talha.slwms.enums.ShipmentStatus;
import com.talha.slwms.enums.VehicleType;
import com.talha.slwms.exception.InvalidWeightException;
import com.talha.slwms.exception.ShipmentNotFoundException;
import com.talha.slwms.exception.VehicleUnavailableException;
import com.talha.slwms.exception.WarehouseFullException;
import com.talha.slwms.model.*;
import com.talha.slwms.report.ReportService;
import com.talha.slwms.repository.FileStorageUtil;
import com.talha.slwms.repository.Repository;
import com.talha.slwms.service.DeliveryEngine;
import com.talha.slwms.service.TrackingService;
import com.talha.slwms.simulation.SimulationEngine;
import com.talha.slwms.util.BillingUtil;
import com.talha.slwms.util.ShipmentBuilder;
import com.talha.slwms.util.VehicleFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.*;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        // ---------- CUSTOMERS ----------
        Customer customer1 = new Customer("Ali Raza", "Lahore","ali@mail.com",   "0301-1111111");
        Customer customer2 = new Customer("Sara Khan", "Karachi","sara@mail.com",   "0302-2222222");

        // Repository<T> = generic storage, reused for any type (Repository pattern)
        Repository<Customer> customerRepo = new Repository<>(Customer::getCustomerId);
        customerRepo.save(customer1);
        customerRepo.save(customer2);
        System.out.println("Customer lookup: " + customerRepo.findById(customer1.getCustomerId()).orElseThrow());

        // ---------- SHIPMENTS (Builder pattern instead of raw constructor) ----------
        Shipment s1 = new ShipmentBuilder()
                .sender(customer1).destination("Multan").weight(10).priority(ShipmentPriority.STANDARD)
                .build();
        Shipment s2 = new ShipmentBuilder()
                .sender(customer2).destination("Islamabad").weight(25).priority(ShipmentPriority.URGENT)
                .build();

        Repository<Shipment> shipmentRepo = new Repository<>(Shipment::getShipmentId);
        shipmentRepo.save(s1);
        shipmentRepo.save(s2);

        // ---------- WAREHOUSE (Collections, sorting, custom exceptions) ----------
        Warehouse warehouseA = new Warehouse("Lahore-Hub", 2);
        warehouseA.receiveShipment(s1);
        warehouseA.receiveShipment(s2);
        try {
            warehouseA.receiveShipment(new ShipmentBuilder().sender(customer1).destination("X").weight(5).build());
        } catch (WarehouseFullException e) {
            System.out.println("Expected: " + e.getMessage());
        }
        System.out.println("By priority: " + warehouseA.getShipmentsByPriority());

        // ---------- VEHICLES (Factory pattern instead of "new Truck(...)") ----------
        Vehicle truck = VehicleFactory.create(VehicleType.TRUCK, "Ali Driver");
        Vehicle van   = VehicleFactory.create(VehicleType.VAN, "Sara Driver");
        Vehicle bike  = VehicleFactory.create(VehicleType.BIKE, "Zain Driver");
        List<Vehicle> fleet = List.of(truck, van, bike);
        fleet.forEach(v -> System.out.println(v + " | 50km cost: " + v.estimateCost(50))); // polymorphism/Strategy

        // ---------- DELIVERY ENGINE (Interfaces + dynamic dispatch) ----------
        DeliveryEngine deliveryEngine = new DeliveryEngine();
        deliveryEngine.assignAndDispatch(s1, truck, 80);
        try {
            deliveryEngine.assignAndDispatch(s2, truck, 30); // truck already busy
        } catch (VehicleUnavailableException e) {
            System.out.println("Expected: " + e.getMessage());
        }
        deliveryEngine.markDelivered(s1, truck);

        // ---------- TRACKING (Singleton pattern) ----------
        TrackingService.getInstance().logLocation(s1.getShipmentId(), "Lahore-Hub");
        TrackingService.getInstance().logLocation(s1.getShipmentId(), "Multan-Delivered");
        System.out.println("Tracking history: " + TrackingService.getInstance().getHistory(s1.getShipmentId()));

        // ---------- BILLING (BigDecimal) ----------
        BigDecimal bill = BillingUtil.calculateTotal(truck.estimateCost(80));
        System.out.println("Final bill: " + bill);

        // ---------- FILE STORAGE (NIO + Serialization) ----------
        FileStorageUtil.writeCustomersToCsv(customerRepo.findAll(), "data/customers.csv");
        FileStorageUtil.saveObject(warehouseA, "data/warehouseA.ser");
        Warehouse restored = (Warehouse) FileStorageUtil.loadObject("data/warehouseA.ser");
        System.out.println("Restored warehouse: " + restored);

        // ---------- REPORTS (Streams, Optional, Collectors) ----------
        ReportService reportService = new ReportService();
        List<Shipment> allShipments = shipmentRepo.findAll();
        Map<String, Double> charges = Map.of(s1.getShipmentId(), truck.estimateCost(80));

        System.out.println("Revenue: " + reportService.totalRevenue(allShipments, charges));
        reportService.topCustomerByShipmentCount(allShipments)
                .ifPresentOrElse(
                        c -> System.out.println("Top customer: " + c.getName()),
                        () -> System.out.println("No data"));

        // ---------- SIMULATION (Threads + ExecutorService + synchronized access) ----------
        SimulationEngine simulationEngine = new SimulationEngine();
        List<Customer> simCustomers = simulationEngine.generateCustomers(500);
        List<Warehouse> simWarehouses = IntStream.range(0, 5)
                .mapToObj(i -> new Warehouse("Warehouse-" + i, 500))
                .collect(Collectors.toList());

        simulationEngine.simulateShipment(simCustomers, simWarehouses, 2000);

        int totalReceived = simWarehouses.stream().mapToInt(Warehouse::getCurrentLoad).sum();
        long wrongStatus = simWarehouses.stream()
                .flatMap(w -> w.getShipments().stream())
                .filter(sh -> sh.getStatus() != ShipmentStatus.IN_WAREHOUSE)
                .count();
        System.out.println("Simulated total received: " + totalReceived + " | wrong-status count: " + wrongStatus);

        // ---------- ANALYTICS (reuses ReportService — no new logic needed) ----------
        List<Shipment> allSimShipments = simWarehouses.stream()
                .flatMap(w -> w.getShipments().stream())
                .collect(Collectors.toList());

        reportService.topCustomerByShipmentCount(allSimShipments)
                .ifPresent(c -> System.out.println("Most active customer: " + c.getName()));

        simWarehouses.stream()
                .max(Comparator.comparingInt(Warehouse::getCurrentLoad))
                .ifPresent(w -> System.out.println("Busiest warehouse: " + w));

        System.out.println("\nALL MODULES + DESIGN PATTERNS EXECUTED SUCCESSFULLY");
    }
}