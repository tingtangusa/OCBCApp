package com.jason.ocbcapp;

public class Branch {
    private int id;
    private String name;
    private int waitingTime;
    private double latitude;
    private double longitude;
    private double distFromUser;

    Branch(int id, String name, int waitingTime, double latitude,
            double longitude) {
        this.id = id;
        this.name = name;
        this.waitingTime = waitingTime;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDistFromUser() {
        return distFromUser;
    }

    public void setDistFromUser(double distFromUser) {
        this.distFromUser = distFromUser;
    }

    @Override
    public String toString() {
        return String.format("%d: %s, lat = %f, long = %f", id, name, latitude,
                longitude);
    }
}