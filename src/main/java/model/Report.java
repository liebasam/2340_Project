package model;

import java.util.Date;

interface Report {
    boolean isHidden();
    void setHidden(boolean hidden);
    Location getLocation();
    Integer getReportNumber();
    Date getSubmissionDate();
    User getSubmitter();
}
