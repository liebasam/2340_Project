package model;

import java.util.Set;

public interface Database
{
    User getUser(String username) throws Exception;
    User editUser(String currentUsername, String newUsername, String newPassword,
                  AccountType newAccountType) throws Exception;
    User insertUser(String username, String password, AccountType accountType) throws Exception;
    
    Set<WaterSourceReport> getSourceReports() throws Exception;
    void insertSourceReport(WaterSourceReport report) throws Exception;
    
    Set<QualityReport> getQualityReports() throws Exception;
    void insertQualityReport(QualityReport report) throws Exception;
}
