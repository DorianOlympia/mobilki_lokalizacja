package com.example.model;

import java.io.Serializable;

public class Router implements Serializable {
    private String id;
    private float xPosMeters;
    private float yPosMeters;

    private float distance;

    public Router(String id, float xPosMeters, float yPosMeters, float distance) {
        this.id = id;
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
}
