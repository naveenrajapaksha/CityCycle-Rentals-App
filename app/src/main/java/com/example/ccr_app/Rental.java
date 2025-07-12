package com.example.ccr_app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Rental {
    private int rentalId;
    private String bikeType;
    private String stationName;
    private long durationMillis;
    private double cost;
    private long startTime;

    public Rental(int rentalId, String bikeType, String stationName, long durationMillis, double cost, long startTime) {
        this.rentalId = rentalId;
        this.bikeType = bikeType;
        this.stationName = stationName;
        this.durationMillis = durationMillis;
        this.cost = cost;
        this.startTime = startTime;
    }

    public int getRentalId() { return rentalId; }
    public String getBikeType() { return bikeType; }
    public String getStationName() { return stationName; }
    public String getDuration() {
        long hours = durationMillis / (1000 * 60 * 60);
        long minutes = (durationMillis / (1000 * 60)) % 60;
        long seconds = (durationMillis / 1000) % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    public String getCost() { return String.format("$%.2f", cost); }
    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(startTime));
    }
}