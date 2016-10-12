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
    
    private final Map<String, Integer> ids = new HashMap<>();
    private final Map<Integer, User> users = new HashMap<>();
    private final Set<SecurityLogEntry> securityLog = new HashSet<>();

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
        if (usernameTaken(username)) {
            throw new IllegalArgumentException("Username is taken");
        }
        int id = numUsers;
        numUsers++;
        User user = new User(username, pw, accountType, id);
        
        ids.put(username, id);
        users.put(id, user);
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
        WaterSourceReport report = new WaterSourceReport(username, location, source, quality);
        waterSourceReports.add(report);
        return report;
    }
    
    /**
     * Modifies the username of a user
     * @param user The user
     * @param updatedUserName The new username of the user
     * @throws IllegalArgumentException if user is not found in database
     */
    public void modifyUserName(User user, String updatedUserName) {
        if(!users.containsKey(user.getId())) {
            throw new IllegalArgumentException("Invalid id");
        }
        
        ids.remove(user.getUsername());
        ids.put(updatedUserName, user.getId());
        
        user.setUsername(updatedUserName);
    }
    
    /**
     * Checks the username/password pair to the database
     * @param username Username
     * @param pw Password
     * @return True if the user/pass combo is valid, false otherwise
     */
    public boolean checkAccount(String username, String pw) {

        User user = getAccount(username);
        if (user == null) {
            securityLog.add(SecurityLogEntry.loginAttempt(null, SecurityLogEntry.EventStatus.INVALID_USER));
            return false;
        }
        if (!user.getPassword().equals(pw)) {
            securityLog.add(SecurityLogEntry.loginAttempt(user.getId(), SecurityLogEntry.EventStatus.INVALID_PASS));
            return false;
        }
        securityLog.add(SecurityLogEntry.loginAttempt(user.getId(), SecurityLogEntry.EventStatus.SUCCESS));
        return true;
    }
    
    /**
     * Retrieves an account with the given username
     * @param username Username
     * @return The user corresponding to the username
     * @throws IllegalArgumentException if username is not associated with an account
     */
    public User getAccount(String username) {
        if(!usernameTaken(username)) {
            throw new IllegalArgumentException("Invalid username");
        }
        return users.get(ids.get(username));
    }
    
    /**
     * Returns whether a given username is already taken
     * @param username Username
     * @return Whether username is taken
     */
    public boolean usernameTaken(String username) {
        return ids.containsKey(username);
    }
}
