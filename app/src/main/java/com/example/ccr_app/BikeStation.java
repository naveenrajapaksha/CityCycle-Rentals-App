package com.example.ccr_app;

public class BikeStation {
    private int stationId;
    private String location;
    private int availableBikes;

    public BikeStation(int stationId, String location, int availableBikes) {
        this.stationId = stationId;
        this.location = location;
        this.availableBikes = availableBikes;
    }

    public int getStationId() { return stationId; }
    public String getLocation() { return location; }
    public int getAvailableBikes() { return availableBikes; }
}