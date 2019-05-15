package com.example.model;

import java.io.Serializable;

public class Router implements Serializable {
    private String mac;
    private float xPosMeters;
    private float yPosMeters;

    private float distance;
    private int rssi = 0;
    private float rssiDistance = 0.0f;



    public Router(String mac, float xPosMeters, float yPosMeters, float distance) {
        this.mac = mac;
        this.xPosMeters = xPosMeters;
        this.yPosMeters = yPosMeters;
        this.distance = distance;

    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getxPosMeters() {
        return xPosMeters;
    }

    public float getyPosMeters() {
        return yPosMeters;
    }

    public String getMac() {
        return mac;
    }
    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public float getRssiDistance() {
        return rssiDistance;
    }

    public void setRssiDistance(float rssiDistance) {
        this.rssiDistance = rssiDistance;
    }



    @Override
    public String toString() {
        return mac + " " + xPosMeters + " " + yPosMeters + " " + distance;
    }
}
