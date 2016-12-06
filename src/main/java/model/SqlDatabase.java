package model;

import jdbc.DataAccessObject;

import java.util.Set;

class SqlDatabase implements Database
{
    private static final Database instance = new SqlDatabase();
    public static Database getInstance() { return instance; }
    
    private static final DataAccessObject dao = new DataAccessObject();
    
    private SqlDatabase() { }
    
    @Override
    public User getUser(String username) throws Exception {
        return DataAccessObject.getUser(username);
    }
    
    @Override
    public User editUser(String currentUsername, String newUsername, String newPassword,
                  AccountType newAccountType) throws Exception {
        return DataAccessObject.editUser(currentUsername, newUsername, newPassword, newAccountType);
    }
    
    @Override
    public User insertUser(String username, String password, AccountType accountType) throws Exception {
        return DataAccessObject.insertUser(username, password, accountType);
    }
    
    @Override
    public Set<WaterSourceReport> getSourceReports() throws Exception {
        return DataAccessObject.getSourceReports();
    }
    
    @Override
    public void insertSourceReport(WaterSourceReport report) throws Exception {
        DataAccessObject.insertSourceReport(report.getReportNumber().toString(), report.getSubmitter().getUsername(),
                report.getLocation().getLatitude() + "", report.getLocation().getLongitude() + "",
                report.getType().toString(), report.getQuality().toString(), report.isHidden() + "");
    }
    
    @Override
    public Set<QualityReport> getQualityReports() throws Exception {
        return DataAccessObject.getQualityReports();
    }
    
    @Override
    public void insertQualityReport(QualityReport report) throws Exception {
        DataAccessObject.insertQualityReport(report.getReportNumber().toString(), report.getSubmitter().getUsername(),
                report.getLocation().getLatitude() + "", report.getLocation().getLongitude() + "",
                report.getWaterCondition().toString(), report.getVirusPpm().toString(),
                report.getContaminantPpm().toString(), report.isHidden() + "");
    }
}
