package com.talha.slwms.simulation;

import com.talha.slwms.enums.ShipmentPriority;
import com.talha.slwms.model.Customer;
import com.talha.slwms.model.Shipment;
import com.talha.slwms.model.Warehouse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimulationEngine {
    private final Random random = new Random();

    public List<Customer> generateCustomers(int count){
        List<Customer> customers = new ArrayList<>();
        for(int i = 0; i < count; i++){
            customers.add(new Customer("Customer" +i,  "cust" +i +"@mail.com", "0300" + i, "Address " + i));
        }
        return customers;
    }

    public void simulateShipment(List<Customer> cutomers, List<Warehouse> warehouses, int shipmentCount) throws InterruptedException{
        ExecutorService executer= Executors.newFixedThreadPool(20);
        for(int i = 0; i < shipmentCount; i++){
            final int index = i;
            executer.submit(() -> {
                Customer sender = cutomers.get(random.nextInt(cutomers.size()));
                Warehouse warehouse = warehouses.get(random.nextInt(warehouses.size()));
                ShipmentPriority priority = ShipmentPriority.values()[random.nextInt(ShipmentPriority.values().length)];
                double weight = 1+ random.nextDouble()*50;

                try{
                    Shipment shipment = new Shipment(sender, "Destination - " +index,weight,priority);
                    warehouse.recieveShipment(shipment);
                } catch(Exception e){
                    System.out.println("Skipped Shipment " +index+": "+e.getMessage());
                }
            });
        }

        executer.shutdown();
        executer.awaitTermination(1, TimeUnit.MINUTES);

    }
}
