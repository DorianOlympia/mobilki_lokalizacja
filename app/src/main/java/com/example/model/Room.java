package com.example.model;

import java.util.Arrays;
import java.util.List;

public class Room {
    private float xLengthMeters;
    private float yLengthMeters;
    private List<Router> routerList;

    public Room(float xLengthMeters, float yLengthMeters) {
        this.xLengthMeters = xLengthMeters;
        this.yLengthMeters = yLengthMeters;

        routerList = Arrays.asList(
                new Router("r1", 1, 1, 5.5f),
                new Router("r2", 1, 9, 3.5f),
                new Router("r3", 9, 9, 6.5f)
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
