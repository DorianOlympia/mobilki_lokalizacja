package com.example.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Room implements Serializable {
    private float xLengthMeters;
    private float yLengthMeters;
    private List<Router> routerList;

    public Room(float xLengthMeters, float yLengthMeters) {
        this.xLengthMeters = xLengthMeters;
        this.yLengthMeters = yLengthMeters;

        routerList = Arrays.asList(
                new Router("Mobilne", 1, 1, 5.5f),
                new Router("r2", 1, 9, 3),
                new Router("r3", 9, 9, 3)
        );
    }

    public float getxLengthMeters() {
        return xLengthMeters;
    }

    public float getyLengthMeters() {
        return yLengthMeters;
    }

    public List<Router> getRouterList() {
        return routerList;
    }
}
