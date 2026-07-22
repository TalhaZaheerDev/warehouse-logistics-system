package com.talha.slwms.app;

import com.talha.slwms.enums.ShipmentPriority;
import com.talha.slwms.enums.ShipmentStatus;
import com.talha.slwms.model.*;
import com.talha.slwms.repository.FileStorageUtil;
import com.talha.slwms.service.DeliveryEngine;
import com.talha.slwms.service.TrackingService;
import com.talha.slwms.util.BillingUtil;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Customer customer1 = new Customer("Talha", "Mianwali", "talha@gmail.com", "0315309060");
        Customer customer2 = new Customer("Ali", "Lahore", "ali@gmail.com", "0315309060");
        Customer customer3 = new Customer("Umar", "Islamabad", "umar@gmail.com", "0315309060");
        Customer customer4 = new Customer("Usman", "Rawalpindi", "usman@gmail.com", "0315309060");

        Shipment shipment1 = new Shipment(customer1, "Lahore", 10, ShipmentPriority.EXPRESS);
        Shipment shipment2 = new Shipment(customer2, "Lahore", 10, ShipmentPriority.URGENT);
        Shipment shipment3 = new Shipment(customer3, "Lahore", 10, ShipmentPriority.STANDARD);
        Shipment shipment4 = new Shipment(customer4, "Lahore", 10, ShipmentPriority.EXPRESS);

        Warehouse warehouse= new Warehouse("Lahore", 3);
        warehouse.receiveShipment(shipment1);
        warehouse.receiveShipment(shipment2);
        warehouse.receiveShipment(shipment3);


        Vehicle v1 = new Truck("Hassan", 3);
        Vehicle v2 = new Van("Abrar");
        Vehicle v3 = new Bike("Ahmad");

        DeliveryEngine deliveryEngine= new DeliveryEngine();
        deliveryEngine.assignAndDispatch(shipment1, v1, 20 );

        TrackingService trackingService = new TrackingService();
        trackingService.getCurrentLocation(shipment1.getShipmentId());
        System.out.println(BillingUtil.calculateTotal(v1.estimateCost(30)));


        List<Customer> customers = new ArrayList<>();
        customers.add(customer1);
        customers.add(customer2);
        customers.add(customer3);
        customers.add(customer4);


        FileStorageUtil.writeCustomersToCsv(customers, "data/customers.csv");






        FileStorageUtil.saveObject(warehouse, "data/warehouse_snapshot.ser.");






    }
}
