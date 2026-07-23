package com.talha.slwms.util;

import com.talha.slwms.enums.VehicleType;
import com.talha.slwms.model.Bike;
import com.talha.slwms.model.Truck;
import com.talha.slwms.model.Van;
import com.talha.slwms.model.Vehicle;

public class VehicleFactory {
    private VehicleFactory() {}

    public static Vehicle create (VehicleType type, String driverName){
        return switch (type) {
            case TRUCK ->  new Truck(driverName, 2);
            case VAN ->   new Van(driverName);
            case BIKE ->   new Bike(driverName);
        };
    }
}
