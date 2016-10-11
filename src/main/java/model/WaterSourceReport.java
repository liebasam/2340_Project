package model;

import java.util.Date;

public class WaterSourceReport {
    private final Date submissionDate;
    private final Integer reportNumber;
    private static Integer LAST_REPORT_NUMBER = 0;

    private final String submitterUsername;
    public String getSubmitterUsername() {return submitterUsername;}
    private final String waterLocation;
    public String getWaterLocation() {return waterLocation;}
    private final SourceType waterType;
    public SourceType getWaterType() {return waterType;}
    private final QualityType waterQuality;
    public QualityType getWaterQuality() {return waterQuality;}

    public WaterSourceReport(String submitterUsername, String waterLocation,
                             SourceType waterType, QualityType waterQuality) {
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

}
