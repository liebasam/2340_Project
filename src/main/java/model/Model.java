package model;

import model.WaterSourceReport.QualityType;
import model.WaterSourceReport.SourceType;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A singleton interface between the controller and the model
 */
public final class Model
{
    private static final Model instance = new Model();

    /**
     * @return the singleton instance of Model
     */
    public static Model getInstance() { return instance; }

    private final boolean isTestInstance;
    /**
     * @return a fresh instance for testing purposes
     */
    public static Model getTestInstance() { return new Model(true); }

    private User CURRENT_USER;
    /**
     * @return the current logged in user, or null if logged out
     */
    public User getCurrentUser() { return CURRENT_USER; }

    private Set<SecurityLogEntry> securityLog = new HashSet<>();
    public Set<SecurityLogEntry> getSecurityLog() {return securityLog;}
    private Set<WaterSourceReport> waterSourceReports = new HashSet<>();
    public Set<WaterSourceReport> getWaterSourceReports() {return waterSourceReports;}
    private Set<QualityReport> qualityReports = new HashSet<>();
    public Set<QualityReport> getQualityReports() {return qualityReports;}

    private Database database = null;

    private Model() {
        this(false);
    }

    private Model(boolean testInstance) {
        this.isTestInstance = testInstance;
        if(testInstance) {
            database = TestDatabase.getInstance();
        } else {
            database = SqlDatabase.getInstance();
        }
    
        this.load();
    }

    public void load() {
        try {
            waterSourceReports = new HashSet<>(database.getSourceReports());
            qualityReports = new HashSet<>(database.getQualityReports());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new user object
     * @param username Username
     * @param pw Password
     * @param accountType The authorization level of the new user
     * @return The newly-created user
     * @throws IllegalArgumentException if username is already in use
     */
    public User createAccount(String username, String pw, AccountType accountType) {
        User user = null;
        username = username.toLowerCase();
        try {
            user = database.getUser(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (user != null) {
            throw new IllegalArgumentException("Username is taken");
        }
        try {
            user = database.insertUser(username, pw, accountType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
    
    /**
     * Returns a user with this username. Can only be used with
     * test instances.
     * @param username Username
     * @return A user with the specified username
     */
    public User getUser(String username) {
        if(isTestInstance || CURRENT_USER.isAuthorized(AccountType.Admin)) {
            try {
                return database.getUser(username);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Unexpected error occurred");
            }
        } else {
            throw new InvalidStateException("Illegal permissions");
        }
    }

    /**
     * Creates a new water source report
     * @param location Location of submission
     * @param source Source type
     * @param quality Quality type
     * @return The newly-created water source report
     */
    public WaterSourceReport createSourceReport(Location location, SourceType source, QualityType quality) {
        if (CURRENT_USER == null) {
            throw new IllegalStateException("User is not logged in");
        }

        WaterSourceReport report = new WaterSourceReport(CURRENT_USER, location, source, quality, new Date());
        try {
            database.insertSourceReport(report);
        } catch (Exception e) {
            e.printStackTrace();
        }
        waterSourceReports.add(report);
        
        return report;
    }
    
    /**
     * Hides all nearby quality reports
     * @param l Location around which nearby quality reports will be hidden
     */
    public void hideQualityReportsNear(Location l) {
        for (Report closeReport : (Set<Report>) getQualityReportsNear(l)) {
            closeReport.setHidden(true);
        }
    }

    /**
     * Creates a new water source report
     * @param lat Latitude of submission
     * @param lng Longitude of submission
     * @param source Source type
     * @param quality Quality type
     * @return The newly-created water source report
     */
    public WaterSourceReport createSourceReport(double lat, double lng, SourceType source, QualityType quality) {
        return createSourceReport(new Location(lat, lng), source, quality);
    }
    
    /**
     * Hides all nearby quality reports
     * @param l Location around which nearby source reports will be hidden
     */
    public void hideSourceReportsNear(Location l) {
        for (Report closeReport : (Set<Report>) getSourceReportsNear(l)) {
            closeReport.setHidden(true);
        }
    }

    /**
     * Creates a new quality report
     * @param location Location of submission
     * @param waterCondition Water condition
     * @param virusPpm Virus Parts per Million
     * @param contaminantPpm Contaminant Parts per Million
     * @return The newly-created quality report
     */
    public QualityReport createQualityReport(Location location, QualityReport.WaterCondition waterCondition,
                                             Double virusPpm, Double contaminantPpm) {
        if (CURRENT_USER == null) {
            throw new IllegalStateException("User is not logged in");
        }
        if (!CURRENT_USER.isAuthorized(AccountType.Worker)) {
            throw new IllegalStateException("Insufficient permissions");
        }
        QualityReport report = new QualityReport(CURRENT_USER, location, waterCondition, virusPpm, contaminantPpm);
        try {
            database.insertQualityReport(report);
        } catch (Exception e) {
            e.printStackTrace();
        }
        qualityReports.add(report);
        return report;
    }

    /**
     * Creates a new quality report
     * @param lat Latitude of submission
     * @param lng Longitude of submission
     * @param waterCondition Water condition
     * @param virusPpm Virus Parts per Million
     * @param contaminantPpm Contaminant
     * @return The newly-created quality report
     */
    public QualityReport createQualityReport(double lat, double lng, QualityReport.WaterCondition waterCondition,
                                             Double virusPpm, Double contaminantPpm) {
        return createQualityReport(new Location(lat, lng), waterCondition, virusPpm, contaminantPpm);
    }

    /**
     * Gets all reports within 2 miles of the given location
     * @param location Search origin
     * @return Set of reports within 2 miles of location
     */
    private Set getReportsNear(Location location, Set<Report> reportsToCheck) {
        final double NEAR_DIST_MILES = 2.0;
        return getReportsNear(location, NEAR_DIST_MILES, reportsToCheck);
    }

    /**
     * Gets all NON-HIDDEN reports within a distance of the given location
     * @param location Search origin
     * @param miles Distance from origin in miles
     * @return Set of reports within miles of location
     */
    private Set<Report> getReportsNear(Location location, Double miles, Set<Report> reportsToCheck) {
        return reportsToCheck.stream()
                .filter(report -> distBetween(location, report.getLocation()) < miles)
                .collect(Collectors.toSet());
    }

    private double distBetween(Location loc1, Location loc2) {
        return loc1.distanceTo(loc2);
    }

    /**
     * Gets all NON-HIDDEN quality reports within 2 miles of the given location
     * @param location Search origin
     * @return Set of reports within 2 miles of location
     */
    public Set getQualityReportsNear(Location location) {
        return getReportsNear(location, (Set) qualityReports);
    }
    /**
     * Gets all NON-HIDDEN source reports within 2 miles of the given location
     * @param location Search origin
     * @return Set of reports within 2 miles of location
     */
    public Set getSourceReportsNear(Location location) {
        return getReportsNear(location, (Set) waterSourceReports);
    }
    
    /**
     * Modifies the username of a user
     * @param updatedUserName The new username of the user
     */
    public void modifyUserName(String updatedUserName) {
        if (CURRENT_USER == null) {
            throw new IllegalStateException("User is not logged in");
        }
        
        User user = null;
        try {
            user = database.getUser(updatedUserName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (user != null) {
            throw new IllegalArgumentException("Username is taken");
        }
        
        try {
            CURRENT_USER = database.editUser(CURRENT_USER.getUsername(), updatedUserName, CURRENT_USER.getPassword(), CURRENT_USER.getAccountType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Checks the username/password pair to the database
     * @param username Username
     * @param pw Password
     */
    public void login(String username, String pw) {
        logout();
        User user = null;
        username = username.toLowerCase();
        try {
            user = database.getUser(username);
        } catch(Exception e) {
            e.printStackTrace();
        }
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

    /**
     * Sets the current user's password
     * @param newPass The updated password
     */
    public void setPassword(String newPass) {
        if (CURRENT_USER == null) {
            throw new IllegalStateException("User is not logged in");
        } else if(newPass == null || newPass.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        try {
            CURRENT_USER = database.editUser(CURRENT_USER.getUsername(), CURRENT_USER.getUsername(), newPass, CURRENT_USER.getAccountType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the permissions of the current user
     * @param type Account type to change to
     */
    public void setAccountType(AccountType type) {
        if (CURRENT_USER == null) {
            throw new IllegalStateException("User is not logged in");
        }
        try {
            CURRENT_USER = database.editUser(CURRENT_USER.getUsername(), CURRENT_USER.getUsername(), CURRENT_USER.getPassword(), type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs out the current user. If no user is logged in,
     * nothing will happen.
     */
    public void logout() {
        CURRENT_USER = null;
    }

}
