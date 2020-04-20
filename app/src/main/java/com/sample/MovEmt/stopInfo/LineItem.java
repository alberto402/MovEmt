package com.sample.MovEmt.stopInfo;



public class LineItem {
    private String sour;
    private String dest;
    private String number;
    private String start;
    private String end;
    private String frequency;

    public LineItem(String sour, String dest, String number, String start,String end, String frequency ) {
        this.sour = sour;
        this.dest = dest;
        this.number=number;
        this.start = start;
        this.end = end;
        this.frequency=frequency;
    }

    public String getDest() {
        return dest;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getEnd() {
        return end;
    }

    public String getNumber() {
        return number;
    }

    public String getSour() {
        return sour;
    }

    public String getStart() {
        return start;
    }
}
