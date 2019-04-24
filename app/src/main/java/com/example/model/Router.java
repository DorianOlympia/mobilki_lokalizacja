package com.example.model;

public class Router {
    private String id;
    private float xPosMeters;
    private float yPosMeters;
    private float mockedDistance;

    public Router(String id, float xPosMeters, float yPosMeters, float mockedDistance) {
        this.id = id;
        this.xPosMeters = xPosMeters;
        this.yPosMeters = yPosMeters;
        this.mockedDistance = mockedDistance;
    }

    public float getMockedDistance() {
        return mockedDistance;
    }

    public float getxPosMeters() {
        return xPosMeters;
    }

    public float getyPosMeters() {
        return yPosMeters;
    }
}
