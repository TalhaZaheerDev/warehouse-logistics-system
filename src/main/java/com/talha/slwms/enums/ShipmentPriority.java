package com.talha.slwms.enums;

public enum ShipmentPriority {
    STANDARD(1),
    EXPRESS(2),
    URGENT(3);

    private int weight;

    ShipmentPriority(int value) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
