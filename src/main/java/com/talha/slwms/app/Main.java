package com.talha.slwms.app;import com.talha.slwms.enums.ShipmentPriority;
import com.talha.slwms.enums.ShipmentStatus;
import com.talha.slwms.enums.VehicleType;
import com.talha.slwms.exception.VehicleUnavailableException;
import com.talha.slwms.exception.WarehouseFullException;
import com.talha.slwms.model.Customer;
import com.talha.slwms.model.Shipment;
import com.talha.slwms.model.Vehicle;
import com.talha.slwms.model.Warehouse;
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

    // ---------- small output helpers: this is what makes console output "look professional" ----------

    private static void section(String title) {
        System.out.println();
        System.out.println("=".repeat(70));
        System.out.println("  " + title.toUpperCase());
        System.out.println("=".repeat(70));
    }

    private static void line(String label, Object value) {
        System.out.printf("  %-28s : %s%n", label, value);
    }

    private static void expected(String message) {
        System.out.println("  [handled]  " + message);
    }

    public static void main(String[] args) throws InterruptedException {

        section("Customers");
        Customer customer1 = new Customer("Ali Raza", "ali@mail.com", "0301-1111111", "Lahore");
        Customer customer2 = new Customer("Sara Khan", "sara@mail.com", "0302-2222222", "Karachi");

        Repository<Customer> customerRepo = new Repository<>(Customer::getCustomerId);
        customerRepo.save(customer1);
        customerRepo.save(customer2);

        line("Registered", customer1.getName());
        line("Registered", customer2.getName());
        line("Lookup by ID", customerRepo.findById(customer1.getCustomerId()).orElseThrow());


        section("Shipments");
        Shipment s1 = new ShipmentBuilder()
                .sender(customer1).destination("Multan").weight(10).priority(ShipmentPriority.STANDARD)
                .build();
        Shipment s2 = new ShipmentBuilder()
                .sender(customer2).destination("Islamabad").weight(25).priority(ShipmentPriority.URGENT)
                .build();

        Repository<Shipment> shipmentRepo = new Repository<>(Shipment::getShipmentId);
        shipmentRepo.save(s1);
        shipmentRepo.save(s2);

        line("Created", s1);
        line("Created", s2);


        section("Warehouse");
        Warehouse warehouseA = new Warehouse("Lahore-Hub", 2);
        warehouseA.receiveShipment(s1);
        warehouseA.receiveShipment(s2);
        line("Status", warehouseA);

        try {
            warehouseA.receiveShipment(
                    new ShipmentBuilder().sender(customer1).destination("X").weight(5).build());
        } catch (WarehouseFullException e) {
            expected(e.getMessage());
        }

        line("Sorted by priority", warehouseA.getShipmentsByPriority().size() + " shipments");
        warehouseA.getShipmentsByPriority().forEach(s -> System.out.println("    - " + s));


        section("Vehicle Fleet");
        Vehicle truck = VehicleFactory.create(VehicleType.TRUCK, "Ali Driver");
        Vehicle van   = VehicleFactory.create(VehicleType.VAN, "Sara Driver");
        Vehicle bike  = VehicleFactory.create(VehicleType.BIKE, "Zain Driver");

        for (Vehicle v : List.of(truck, van, bike)) {
            System.out.printf("    %-6s driver=%-12s capacity=%-8.1fkg  cost/50km=%.2f%n",
                    v.getType(), v.getDriverName(), v.calculateDeliveryCapacity(), v.estimateCost(50));
        }


        section("Delivery Engine");
        DeliveryEngine deliveryEngine = new DeliveryEngine();
        deliveryEngine.assignAndDispatch(s1, truck, 80);
        line("Dispatched", s1.getShipmentId().substring(0, 8) + " via " + truck.getType());

        try {
            deliveryEngine.assignAndDispatch(s2, truck, 30);
        } catch (VehicleUnavailableException e) {
            expected(e.getMessage());
        }

        deliveryEngine.markDelivered(s1, truck);
        line("Delivered & vehicle freed", truck.isAvailable());


        section("Tracking");
        TrackingService.getInstance().logLocation(s1.getShipmentId(), "Lahore-Hub");
        TrackingService.getInstance().logLocation(s1.getShipmentId(), "Multan-Delivered");
        line("History", TrackingService.getInstance().getHistory(s1.getShipmentId()));


        section("Billing");
        double rawCharge = truck.estimateCost(80);
        BigDecimal bill = BillingUtil.calculateTotal(rawCharge);
        line("Raw charge", rawCharge);
        line("Final bill (with tax)", bill);


        section("File Storage");
        FileStorageUtil.writeCustomersToCsv(customerRepo.findAll(), "data/customers.csv");
        line("CSV written", "data/customers.csv");

        FileStorageUtil.saveObject(warehouseA, "data/warehouseA.ser");
        Warehouse restored = (Warehouse) FileStorageUtil.loadObject("data/warehouseA.ser");
        line("Serialized & restored", restored);


        section("Reports");
        ReportService reportService = new ReportService();
        List<Shipment> allShipments = shipmentRepo.findAll();
        Map<String, Double> charges = Map.of(s1.getShipmentId(), rawCharge);

        line("Revenue (delivered only)", reportService.totalRevenue(allShipments, charges));
        reportService.topCustomerByShipmentCount(allShipments)
                .ifPresentOrElse(
                        c -> line("Top customer", c.getName()),
                        () -> line("Top customer", "none yet"));


        section("Simulation (500 customers / 2000 shipments / 20 threads)");
        SimulationEngine simulationEngine = new SimulationEngine();
        List<Customer> simCustomers = simulationEngine.generateCustomers(500);
        List<Warehouse> simWarehouses = IntStream.range(0, 5)
                .mapToObj(i -> new Warehouse("Warehouse-" + i, 500))
                .collect(Collectors.toList());

        simulationEngine.simulateShipment(simCustomers, simWarehouses, 2000);

        int totalReceived = simWarehouses.stream().mapToInt(Warehouse::getCurrentLoad).sum();
        int totalCapacity = simWarehouses.stream().mapToInt(Warehouse::getCapacity).sum();
        long wrongStatus = simWarehouses.stream()
                .flatMap(w -> w.getShipments().stream())
                .filter(sh -> sh.getStatus() != ShipmentStatus.IN_WAREHOUSE)
                .count();

        line("Total received / capacity", totalReceived + " / " + totalCapacity);
        line("Wrong-status count (should be 0)", wrongStatus);
        System.out.println();
        simWarehouses.forEach(w ->
                System.out.printf("    %-14s load=%d/%d%n", w.getLocation(), w.getCurrentLoad(), w.getCapacity()));


        section("Analytics");
        List<Shipment> allSimShipments = simWarehouses.stream()
                .flatMap(w -> w.getShipments().stream())
                .collect(Collectors.toList());

        reportService.topCustomerByShipmentCount(allSimShipments)
                .ifPresent(c -> line("Most active customer", c.getName()));

        simWarehouses.stream()
                .max(Comparator.comparingInt(Warehouse::getCurrentLoad))
                .ifPresent(w -> line("Busiest warehouse", w.getLocation() + " (" + w.getCurrentLoad() + " shipments)"));

        System.out.println();
        System.out.println("=".repeat(70));
        System.out.println("  ALL MODULES + DESIGN PATTERNS EXECUTED SUCCESSFULLY");
        System.out.println("=".repeat(70));
    }
}