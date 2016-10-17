package model;

import java.io.Serializable;
import java.util.Date;

public class WaterSourceReport implements Serializable {

    private static Integer LAST_REPORT_NUMBER = 0;

    // StringProperty is needed in order to populate the 'View Reports' table
    private final String submitterUsername;
    public String getSubmitterUsername() {return submitterUsername;}

    private final Location location;
    public Location getLocation() {return location;}

    private final SourceType type;
    public SourceType getType() {return type;}

    private final QualityType quality;
    public QualityType getQuality() {return quality;}

    private final Date submissionDate;
    public Date getSubmissionDate() {return submissionDate;}

    private final Integer reportNumber;
    public Integer getreportNumber() {return reportNumber;}

    public WaterSourceReport(String submitterUsername, Location location,
                             SourceType type, QualityType quality) {
        this.submitterUsername = submitterUsername;
        this.location = location;
        this.type = type;
        this.quality = quality;
        this.submissionDate = new Date();
        this.reportNumber = LAST_REPORT_NUMBER++;
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

    public enum SourceType implements Serializable {
        //'STREAM' is a global variable that cannot be used
                //Thus, it is replaced with 'RIVER'
                BOTTLED, WELL, RIVER, LAKE, STREAM, OTHER
    }

    public enum QualityType implements Serializable {
        WASTE, TREATABLE_CLEAR, TREATABLE_MUDDY, POTABLE
    }
}
