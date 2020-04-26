package com.sample.MovEmt.stopInfo;


import java.util.ArrayList;

public class StopItem {
    private String idStop;
    private String nameStop;
    private String direction;
    private ArrayList<LineItem> lines;

    public StopItem(String idStop, String nameStop, ArrayList<LineItem> lines,String direction) {
        this.idStop = idStop;
        this.nameStop = nameStop;
        this.lines=lines;
        this.direction=direction;
    }

    public String getIdStop() {
        return idStop;
    }

    public String getDirection() {
        return direction;
    }
    public String getNameStop() {
        return nameStop;
    }
    public ArrayList<LineItem> getLines() {
        return lines;
    }
}
