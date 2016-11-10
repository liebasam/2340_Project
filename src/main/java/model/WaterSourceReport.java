package model;

import java.io.Serializable;
import java.util.Date;

public class WaterSourceReport implements Report, Serializable {

    private static Integer LAST_REPORT_NUMBER = 0;

    // StringProperty is needed in order to populate the 'View Reports' table
    private final User submitter;
    @Override public User getSubmitter() {return submitter;}

    private final Location location;
    @Override public Location getLocation() {return location;}

    private final SourceType type;
    public SourceType getType() {return type;}

    private final QualityType quality;
    public QualityType getQuality() {return quality;}

    private final Date submissionDate;
    @Override public Date getSubmissionDate() {return submissionDate;}

    private final Integer reportNumber;
    @Override public Integer getReportNumber() {return reportNumber;}

    private boolean hidden;
    @Override public boolean isHidden() {return hidden;}
    @Override public void setHidden(boolean hidden) {this.hidden = hidden;}
    
    private static Integer nextReportNumber() {
        Integer next = LAST_REPORT_NUMBER;
        LAST_REPORT_NUMBER++;
        return next;
    }

    WaterSourceReport(User submitter, Location location,
                             SourceType type, QualityType quality) {
        this.submitter = submitter;
        this.location = location;
        this.type = type;
        this.quality = quality;
        this.submissionDate = new Date();
        this.reportNumber = nextReportNumber();
        this.hidden = false;
    }

    @Override
    public String toString() {
        //TODO
        return reportNumber.toString();
    }

    @Override
    public int hashCode() {
        return location.hashCode();
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
