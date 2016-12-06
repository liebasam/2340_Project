package model;

import jdbc.DataAccessObject;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class TestDatabase implements Database
{
    private int idCounter = 0;
    
    private final Map<String, User> users;
    private final Set<WaterSourceReport> sourceReports;
    private final Set<QualityReport> qualityReports;
    
    private TestDatabase() {
        users = new HashMap<>();
        sourceReports = new HashSet<>();
        qualityReports = new HashSet<>();
    }
    
    public static Database getInstance() { return new TestDatabase(); }
    
    @Override
    public User getUser(String username) throws Exception {
        return users.get(username);
    }
    
    @Override
    public User editUser(String currentUsername, String newUsername, String newPassword,
                         AccountType newAccountType) throws Exception {
        User user = users.get(currentUsername);
        
        users.remove(currentUsername);
        user.setUsername(newUsername);
        users.put(newUsername, user);
        
        user.setPassword(newPassword);
        
        user.setAccountType(newAccountType);
        
        return user;
    }
    
    @Override
    public User insertUser(String username, String password, AccountType accountType) throws Exception {
        User newUser = new User(username, password, accountType, idCounter);
        users.put(username, newUser);
        idCounter++;
        return newUser;
    }
    
    @Override
    public Set<WaterSourceReport> getSourceReports() throws Exception {
        return sourceReports;
    }
    
    @Override
    public void insertSourceReport(WaterSourceReport report) throws Exception {
        sourceReports.add(report);
    }
    
    @Override
    public Set<QualityReport> getQualityReports() throws Exception {
        return qualityReports;
    }
    
    @Override
    public void insertQualityReport(QualityReport report) throws Exception {
        qualityReports.add(report);
    }
}
