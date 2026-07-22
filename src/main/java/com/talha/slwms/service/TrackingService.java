package com.talha.slwms.service;

import java.util.*;

public class TrackingService {
    private final Map<String, List<String>> trackingLog = new HashMap<>();

    public void logLocation(String shipmentId, String Location){
        trackingLog.computeIfAbsent(shipmentId, k -> new ArrayList<>()).add(Location);
    }

    public List<String> getHistory(String shipmentId){
        return trackingLog.getOrDefault(shipmentId, Collections.emptyList());
    }

    public String getCurrentLocation(String shipmentId){
        List<String> history = getHistory(shipmentId);
        if(history.isEmpty()){
            return "unknown";
        }
        return history.get(history.size()-1);
    }
}
