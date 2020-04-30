package com.sample.MovEmt.routeInfo;

import java.util.ArrayList;

public class RouteItem {
    private String distance;
    private String departureTime;
    private String description;
    private String arrivalTime;
    private String duration;
    private ArrayList<SectionItem> sections;

    public RouteItem(String distance, String departureTime, String description,String arrivalTime,String duration,ArrayList<SectionItem> sections) {
        this.distance = distance;
        this.departureTime = departureTime;
        this.description=description;
        this.arrivalTime=arrivalTime;
        this.duration=duration;
        this.sections=sections;
    }

    public String getDistance() {
        return distance;
    }
    public String getDepartureTime() {
        return departureTime;
    }
    public String getDescription() {
        return description;
    }
    public String getArrivalTime() {
        return arrivalTime;
    }
    public String getDuration() { return  duration; }
    public ArrayList<SectionItem> getLines() {
        return sections;
    }
}
