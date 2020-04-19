package com.sample.MovEmt.busStopItem;

public class BusItem {
    private String bus;
    private int minutes;

    public BusItem(String bus, int minutes) {
        this.bus = bus;
        this.minutes = minutes;
    }

    public String getBus() {
        return bus;
    }

    public int getMinutes() {
        return minutes;
    }
}
