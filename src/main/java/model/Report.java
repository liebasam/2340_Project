package model;

import java.util.Date;

/**
 * Interface for water reports to allow polymorphism
 */
interface Report {
    boolean isHidden();
    void setHidden(boolean hidden);
    Location getLocation();
    Integer getReportNumber();
    Date getSubmissionDate();
    User getSubmitter();
}
