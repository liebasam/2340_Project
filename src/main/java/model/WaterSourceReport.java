package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.xml.transform.Source;
import java.util.Date;

public class WaterSourceReport {

    private Integer LAST_REPORT_NUMBER = 0;

    // StringProperty is needed in order to populate the 'View Reports' table
    private final StringProperty SubmitterUsername;
    public String getSubmitterUsername() {return SubmitterUsername.get();}

    private final StringProperty location;
    public String getLocation() {return location.get();}

    private final StringProperty type;
    public String getType() {return type.get();}

    private final StringProperty quality;
    public String getQuality() {return quality.get();}

    private final StringProperty SubmissionDate;
    public String getSubmissionDate() {return SubmissionDate.get();}

    private final StringProperty reportNumber;
    public String getreportNumber() {return reportNumber.get();}

    public WaterSourceReport(String submitterUsername, String location,
                             SourceType type, QualityType quality) {
        this.SubmitterUsername = new SimpleStringProperty(submitterUsername);
        this.location = new SimpleStringProperty(location);
        this.type = new SimpleStringProperty(type.toString());
        this.quality = new SimpleStringProperty(quality.toString());
        this.SubmissionDate = new SimpleStringProperty((new Date()).toString());
        this.reportNumber = new SimpleStringProperty((LAST_REPORT_NUMBER++).toString());
    }

    @Override
    public String toString() {
        //TODO
        return reportNumber.toString();
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(reportNumber.toString());
    }

    //These classes are more or less just place-holders for future info-holders
    public class Location {
        double latitutde;
        double longitude;
    }

    public enum SourceType {
        //'STREAM' is a global variable that cannot be used
                //Thus, it is replaced with 'RIVER'
                BOTTLED, WELL, RIVER, LAKE, STREAM, OTHER
    }

    public enum QualityType {
        WASTE, TREATABLE_CLEAR, TREATABLE_MUDDY, POTABLE
    }
}
