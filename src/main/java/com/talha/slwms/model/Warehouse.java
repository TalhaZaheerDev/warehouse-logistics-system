package com.talha.slwms.model;

import com.talha.slwms.enums.ShipmentPriority;
import com.talha.slwms.enums.ShipmentStatus;
import com.talha.slwms.exception.ShipmentNotFoundException;
import com.talha.slwms.exception.WarehouseFullException;

import java.util.*;

public class Warehouse {
    private final String warehouseId;
    private final String location;
    private final int capacity;
    private final List<Shipment> shipments;
    private final Map<String, Shipment> shipmentIndex;

    public Warehouse( String location, int capacity) {
        if(capacity<=0){
            throw new IllegalArgumentException("Capacity must be Positive");
        }

        this.warehouseId = UUID.randomUUID().toString();
        this.location = location;
        this.capacity = capacity;
        this.shipments = new ArrayList<>();
        this.shipmentIndex = new HashMap<>();
    }

    public String getWarehouseId() { return warehouseId; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }
    public int getCurrentLoad() { return shipments.size(); }

    public List<Shipment> getShipments() {
        return Collections.unmodifiableList(shipments);
    }

    public void receiveShipment(Shipment shipment) {
       if(shipments.size() >= capacity){
           throw new WarehouseFullException("Warehouse " +location +" is full");
       }

       shipment.updateStatus(ShipmentStatus.IN_WAREHOUSE);
       shipments.add(shipment);
       shipmentIndex.put(shipment.getShipmentId(), shipment);
    }

    public Shipment findShipmentById(String shipmentId) {
        Shipment s = shipmentIndex.get(shipmentId);
        if(s==null){
            throw new ShipmentNotFoundException("Shipment with id "+shipmentId+" not found");
        }

        return s;
    }

    //sort by priority weight, descending (Urgent First)
    public List<Shipment> getShipmentsByPriority(ShipmentPriority priority) {
        List<Shipment> copy = new ArrayList<>(shipments);
        copy.sort(Comparator.comparingInt((Shipment s) -> s.getPriority().getWeight()).reversed());
        return copy;
    }

    //sort by weight, ascending
    public List<Shipment> getShipmentsByWeight(){
        List<Shipment> copy = new ArrayList<>(shipments);
        copy.sort(Comparator.comparingDouble(Shipment::getWeightKg));
        return copy;
    }

    //search using a stream filter
    public List<Shipment> findByStatus(ShipmentStatus status) {
        List<Shipment> result = new ArrayList<>();
        for (Shipment s : shipments) {
            if (s.getStatus() == status) {
                result.add(s);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Warehouse{id=" + warehouseId + ", location=" + location +
                ", load=" + getCurrentLoad() + "/" + capacity + "}";
    }

}
