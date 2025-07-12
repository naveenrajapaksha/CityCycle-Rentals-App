package com.example.ccr_app;

public class Bike {
    private int bikeId;
    private String type;
    private String stationName;

    public Bike(int bikeId, String type, String stationName) {
        this.bikeId = bikeId;
        this.type = type;
        this.stationName = stationName;
    }

    public int getBikeId() { return bikeId; }
    public String getType() { return type; }
    public String getStationName() { return stationName; }
}