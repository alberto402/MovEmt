package com.sample.MovEmt.routeInfo;

public class SectionItem {
    private String sectionDistance;
    private String sectionDuration;
    private String order;
    private String type;
    private String idLine;
    private String sourceIdStop;
    private String sourceName;
    private String sourceDescription;
    private String destinationName;

    public SectionItem (String sectionDistance,String sectionDuration,String order,String type,String idLine,
                        String sourceIdStop,String sourceName, String sourceDescription, String destinationName){
        this.sectionDistance=sectionDistance;
        this.sectionDuration=sectionDuration;
        this.order=order;
        this.type=type;
        this.idLine=idLine;
        this.sourceIdStop=sourceIdStop;
        this.sourceName=sourceName;
        this.sourceDescription=sourceDescription;
        this.destinationName=destinationName;
    }

    public String getSectionDistance() { return sectionDistance; }
    public String getSectionDuration() { return sectionDuration; }
    public String getOrder() { return order; }
    public String getType() { return type; }
    public String getIdLine() { return idLine; }
    public String getSourceIdStop() { return sourceIdStop; }
    public String getSourceName() { return sourceName; }
    public String getSourceDescription() { return sourceDescription; }
    public String getDestinationName() { return destinationName; }
}
