package com.talha.slwms.app;

import com.talha.slwms.enums.ShipmentPriority;
import com.talha.slwms.enums.ShipmentStatus;
import com.talha.slwms.model.*;

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
        System.out.println(warehouse.toString());
        warehouse.receiveShipment(shipment1);
        warehouse.receiveShipment(shipment2);
        warehouse.receiveShipment(shipment3);

        System.out.println(warehouse.getCapacity());
        System.out.println(warehouse.getCurrentLoad());
        System.out.println(warehouse.getShipmentsByWeight());
        System.out.println(warehouse.getShipmentsByPriority(ShipmentPriority.URGENT));

        List<Vehicle> fleet = new ArrayList<>();
        fleet.add(new Truck("Ali", 2));
        fleet.add(new Van("Sara"));
        fleet.add(new Bike("Zain"));

        for (Vehicle v : fleet) {
            System.out.println(v);
            System.out.println("Cost for 50km: " + v.estimateCost(50));
        }

    }
}
