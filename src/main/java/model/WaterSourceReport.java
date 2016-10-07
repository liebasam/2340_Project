package model;

import java.util.Date;

public class WaterSourceReport {
    private final Date submissionDate;
    private final Integer reportNumber;
    private static Integer LAST_REPORT_NUMBER = 0;

    private final String submitterUsername;
    public String getSubmitterUsername() {return submitterUsername;}
    private final Location waterLocation;
    public Location getWaterLocation() {return waterLocation;}
    private final WaterType waterType;
    public WaterType getWaterType() {return waterType;}
    private final WaterQuality waterQuality;
    public WaterQuality getWaterQuality() {return waterQuality;}

    public WaterSourceReport(String submitterUsername, Location waterLocation,
                             WaterType waterType, WaterQuality waterQuality) {
        this.submitterUsername = submitterUsername;
        this.waterLocation = waterLocation;
        this.waterType = waterType;
        this.waterQuality = waterQuality;
        //Auto-generated when called
        this.submissionDate = new Date();
        this.reportNumber = WaterSourceReport.LAST_REPORT_NUMBER++;
    }

    @Override
    public String toString() {
        //TODO
        return reportNumber.toString();
    }

    @Override
    public int hashCode() {
        return reportNumber;
    }

    //These classes are more or less just place-holders for future info-holders
    public class Location {
        double latitutde;
        double longitude;
    }
    public enum WaterType {
        //'STREAM' is a global variable that cannot be used
        //Thus, it is replaced with 'RIVER'
        BOTTLED, WELL, RIVER, LAKE, STREAM, OTHER;
    }
    public enum WaterQuality {
        WASTE, TREATABLE_CLEAR, TREATABLE_MUDDY, POTABLE;
    }
}
