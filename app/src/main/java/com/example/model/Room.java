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
                new Router("Mobilne1", 1, 1, 0),
                new Router("Mobilne2", 1, 10.1f, 0),
                new Router("Mobilne3", 6.3f, 3.5f, 0),
                new Router("Mobilne4", 7, 7.8f, 0)
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
