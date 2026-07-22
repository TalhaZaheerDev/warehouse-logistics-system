package com.talha.slwms.service;

import com.talha.slwms.model.Vehicle;

public interface Deliverable {
    void dispatch(Vehicle vehicle);
    boolean isDelivered();
}
