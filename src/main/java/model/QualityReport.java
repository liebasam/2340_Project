package model;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a quality report, which is a water report with more exact and
 * technical details on the water quality
 */
public class QualityReport implements Report, Serializable {
    private static Integer LAST_REPORT_NUMBER = 0;

    // StringProperty is needed in order to populate the 'View Reports' table
    private final User submitter;
    @Override public User getSubmitter() { return submitter; }

    private final Date submissionDate;
    @Override public Date getSubmissionDate() { return submissionDate; }

    private final Integer reportNumber;
    @Override public Integer getReportNumber() { return reportNumber; }

    private final Location location;
    @Override public Location getLocation() { return location; }

    private final WaterCondition waterCondition;
    /**
     * @return The relative safety of the water to drink
     */
    public WaterCondition getWaterCondition() {return waterCondition; }

    private final Double virusPpm;
    /**
     * @return The virus concentration in parts per million
     */
    public Double getVirusPpm() { return virusPpm; }

    private final Double contaminantPpm;
    /**
     * @return The contaminant concentration in parts per million
     */
    public Double getContaminantPpm() { return contaminantPpm; }

    private boolean hidden;
    @Override public boolean isHidden() {return hidden;}
    @Override public void setHidden(boolean hidden) {this.hidden = hidden; }

    private static Integer nextReportNumber() {
        Integer next = LAST_REPORT_NUMBER;
        LAST_REPORT_NUMBER++;
        return next;
    }

    public QualityReport(User submitter, Location location, WaterCondition waterCondition,
                         Double virusPpm, Double contaminantPpm) {
        this(submitter, location, waterCondition, virusPpm, contaminantPpm, new Date(), false);
    }
    public QualityReport(User submitter, Location location, WaterCondition waterCondition,
                         Double virusPpm, Double contaminantPpm, Date date, boolean hidden) {
        this.location = location;
        this.submitter = submitter;
        this.submissionDate = date;
        this.reportNumber = nextReportNumber();
        this.waterCondition = waterCondition;
        this.virusPpm = virusPpm;
        this.contaminantPpm = contaminantPpm;
        this.hidden = hidden;
    }

    public enum WaterCondition implements Serializable {
        SAFE, TREATABLE, UNSAFE
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }
}
