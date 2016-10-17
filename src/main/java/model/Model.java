package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.WaterSourceReport.*;

public class Model {
    private static final Model instance = new Model();
    public static Model getInstance() { return instance; }
    
    private static int numUsers = 0;

    private final Map<String, User> users = new HashMap<>();
    private final Set<SecurityLogEntry> securityLog = new HashSet<>();
    public static User CURRENT_USER;

    public final ObservableList<WaterSourceReport> waterSourceReports = FXCollections.observableArrayList();

    private Model() {
        createAccount("user", "pass", AccountType.Admin);
    }

    /**
     * Creates a new username/password pair
     * @param username Username
     * @param pw Password
     * @return The newly-created user
     * @throws IllegalArgumentException if username is already in use
     */
    public User createAccount(String username, String pw, AccountType accountType) {
        if (users.containsKey(username)) {
            throw new IllegalArgumentException("Username is taken");
        }
        User user = new User(username, pw, accountType, numUsers++);

        users.put(username, user);
        return user;
    }

    /**
     * Creates a new water source report
     * @param username Username of the submitter
     * @param location Location of submission
     * @param source Source type
     * @param quality Quality type
     * @return The newly-created water source report
     */
    public WaterSourceReport createReport(String username, String location, SourceType source, QualityType quality) {
        if (CURRENT_USER == null) {
            throw new IllegalStateException("User is not logged in");
        }
        WaterSourceReport report = new WaterSourceReport(username, location, source, quality);
        waterSourceReports.add(report);
        return report;
    }
    
    /**
     * Modifies the username of a user
     * @param updatedUserName The new username of the user
     */
    public void modifyUserName(String updatedUserName) {
        if (CURRENT_USER == null) {
            throw new IllegalStateException("User is not logged in");
        } else if (users.containsKey(updatedUserName)) {
            throw new IllegalArgumentException("Username is taken");
        }
        
        users.remove(CURRENT_USER.getUsername());
        users.put(updatedUserName, CURRENT_USER);
        CURRENT_USER.setUsername(updatedUserName);
    }
    
    /**
     * Checks the username/password pair to the database
     * @param username Username
     * @param pw Password
     * @return True if the user/pass combo is valid, false otherwise
     */
    public void login(String username, String pw) {
        try {logout();} catch (IllegalStateException e) {}
        User user = users.get(username);
        if (user == null) {
            securityLog.add(SecurityLogEntry.loginAttempt(null, SecurityLogEntry.EventStatus.INVALID_USER));
            throw new IllegalArgumentException("Invalid user/pass");
        }
        if (!user.getPassword().equals(pw)) {
            securityLog.add(SecurityLogEntry.loginAttempt(user.getId(), SecurityLogEntry.EventStatus.INVALID_PASS));
            throw new IllegalArgumentException("Invalid user/pass");
        }
        securityLog.add(SecurityLogEntry.loginAttempt(user.getId(), SecurityLogEntry.EventStatus.SUCCESS));
        CURRENT_USER = user;
    }

    public void setPassword(String newPass) {
        if (CURRENT_USER == null) {
            throw new IllegalStateException("User is not logged in");
        }
        if (newPass.equals("")) {
            throw new IllegalArgumentException("Invalid password");
        }
        CURRENT_USER.setPassword(newPass);
    }

    public void setAccountType(AccountType type) {
        if (CURRENT_USER == null) {
            throw new IllegalStateException("User is not logged in");
        }
        CURRENT_USER.setAccountType(type);
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        if (CURRENT_USER == null) {
            throw new IllegalStateException("User is not logged in");
        }
        CURRENT_USER = null;
    }

}
