package model;

import java.io.Serializable;
import java.util.Date;

public class QualityReport implements Report, Serializable {
    private static Integer LAST_REPORT_NUMBER = 0;

    // StringProperty is needed in order to populate the 'View Reports' table
    private final User submitter;
    @Override public User getSubmitter() {return submitter;}

    private final Date submissionDate;
    @Override public Date getSubmissionDate() {return submissionDate;}

    private final Integer reportNumber;
    @Override public Integer getReportNumber() {return reportNumber;}

    private final Location location;
    @Override public Location getLocation() {return location;}

    private final WaterCondition waterCondition;
    public WaterCondition getWaterCondition() {return waterCondition;}

    private final Double virusPpm;
    public Double getVirusPpm() {return virusPpm;}

    private final Double contaminantPpm;
    public Double getContaminantPpm() {return contaminantPpm;}

    private boolean hidden;
    @Override public boolean isHidden() {return hidden;}
    @Override public void setHidden(boolean hidden) {this.hidden = hidden;}
    
    private static Integer nextReportNumber() {
        Integer next = LAST_REPORT_NUMBER;
        LAST_REPORT_NUMBER++;
        return next;
    }

    QualityReport(User submitter, Location location, WaterCondition waterCondition,
                         Double virusPpm, Double contaminantPpm) {
        this.location = location;
        this.submitter = submitter;
        this.submissionDate = new Date();
        this.reportNumber = nextReportNumber();
        this.waterCondition = waterCondition;
        this.virusPpm = virusPpm;
        this.contaminantPpm = contaminantPpm;
        this.hidden = false;
    }

    public enum WaterCondition implements Serializable {
        SAFE, TREATABLE, UNSAFE
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }
}
