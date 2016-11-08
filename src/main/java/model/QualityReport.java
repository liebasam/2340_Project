package model;

import java.io.Serializable;
import java.util.Date;

public class QualityReport implements Report, Serializable {
    private static Integer LAST_REPORT_NUMBER = 0;

    // StringProperty is needed in order to populate the 'View Reports' table
    private final User submitter;
    public User getSubmitter() {return submitter;}

    private final Date submissionDate;
    public Date getSubmissionDate() {return submissionDate;}

    private final Integer reportNumber;
    public Integer getReportNumber() {return reportNumber;}

    private final Location location;
    public Location getLocation() {return location;}

    private final WaterCondition waterCondition;
    public WaterCondition getWaterCondition() {return waterCondition;}

    private final Double virusPpm;
    public Double getVirusPpm() {return virusPpm;}

    private final Double contaminantPpm;
    public Double getContaminantPpm() {return contaminantPpm;}

    private boolean hidden;
    public boolean isHidden() {return hidden;}
    public void setHidden(boolean hidden) {this.hidden = hidden;}

    public QualityReport(User submitter, Location location, WaterCondition waterCondition,
                         Double virusPpm, Double contaminantPpm) {
        this.location = location;
        this.submitter = submitter;
        this.submissionDate = new Date();
        this.reportNumber = LAST_REPORT_NUMBER++;
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
