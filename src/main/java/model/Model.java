package model;

import jdbc.DataAccessObject;
import model.WaterSourceReport.QualityType;
import model.WaterSourceReport.SourceType;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Model implements Serializable {

    private static Model instance = new Model();
    public static Model getInstance() { return instance; }
    public static Model getTestInstance() throws SQLException { return new Model(true); }

    private static final String FILE_DIRECTORY = "./savedata/";
    private static final String FILE_NAME_EXT = "model.ser";

    private static int numUsers = 0;
    public static User CURRENT_USER;

    private static Map<String, User> users = new HashMap<>();
    public Map<String, User> getUsers() {return users;}
    private final Set<SecurityLogEntry> securityLog = new HashSet<>();
    public Set<SecurityLogEntry> getSecurityLog() {return securityLog;}
    private static Set<WaterSourceReport> waterSourceReports = new HashSet<>();
    public Set<WaterSourceReport> getWaterSourceReports() {return waterSourceReports;}
    private static Set<QualityReport> qualityReports = new HashSet<>();
    public Set<QualityReport> getQualityReports() {return qualityReports;}

    private static DataAccessObject dao = null;
    
    private Model() {
        this(false);
    }

    private Model(boolean testInstance) {
        if(!testInstance) {
            //Attempt to load the model
            try {
                dao = new DataAccessObject();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void load() {
        try {
            dao = new DataAccessObject();
            users = dao.getUsers();
            waterSourceReports = dao.getSourceReports();
            qualityReports = dao.getQualityReports();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new username/password pair
     * @param username Username
     * @param pw Password
     * @return The newly-created user
     * @throws IllegalArgumentException if username is already in use
     */
    public User createAccount(String username, String pw, AccountType accountType) {
        User user = null;
        Integer id = username.hashCode();
        try {
            user = dao.getUser(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (user != null) {
            throw new IllegalArgumentException("Username is taken");
        }
        user = new User(username.toLowerCase(), pw, accountType, id);
        try {
            dao.insertUser("" + id, username, pw, accountType.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
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
        String latitude = (new Double(location.getLatitude())).toString();
        String longitude =(new Double(location.getLongitude())).toString();
        String id = (new Double(CURRENT_USER.hashCode())).toString() + latitude
                + longitude;
        WaterSourceReport report = new WaterSourceReport(CURRENT_USER, location, source, quality, new Date());
        try {
            dao.insertSourceReport(id.substring(0, Math.min(20, id.length())), CURRENT_USER.getUsername(), latitude,
                    longitude, source.toString(), quality.toString(), false + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return report;
    }
    
    /**
     * Hides all nearby quality reports
     * @param l Location around which nearby quality reports will be hidden
     */
    public void hideQualityReportsNear(Location l) {
        for (Report closeReport : (Set<Report>) Model.getInstance().getQualityReportsNear(l)) {
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
        return this.createSourceReport(new Location(lat, lng), source, quality);
    }
    
    /**
     * Hides all nearby quality reports
     * @param l Location around which nearby source reports will be hidden
     */
    public void hideSourceReportsNear(Location l) {
        for (Report closeReport : (Set<Report>) Model.getInstance().getSourceReportsNear(l)) {
            closeReport.setHidden(true);
        }
    }

    /**
     * Creates a new quality report
     * @param location Location of submission
     * @param waterCondition Water condition
     * @param virusPpm Virus Parts per Million
     * @param contaminantPpm Contaminent Parts per Million
     * @return The newly-created quality report
     */
    public QualityReport createQualityReport(Location location, QualityReport.WaterCondition waterCondition,
                                             Double virusPpm, Double contaminantPpm) {
        if (CURRENT_USER == null) {
            throw new IllegalStateException("User is not logged in");
        }
        if (!CURRENT_USER.getAccountType().isAuthorized(AccountType.Worker)) {
            throw new IllegalStateException("Insufficient permissions");
        }
        QualityReport report = new QualityReport(CURRENT_USER, location, waterCondition, new Date(), virusPpm,
                contaminantPpm, false);
        try {
            dao.insertQualityReport(CURRENT_USER.hashCode() + "" + location.hashCode(), CURRENT_USER.getUsername(),
                    location.getLatitude() + "", location.getLongitude() + "", waterCondition.toString(), virusPpm + "",
                    contaminantPpm + "", false + "");
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
        return this.createQualityReport(new Location(lat, lng), waterCondition, virusPpm, contaminantPpm);
    }

    /**
     * Gets all NON-HIDDEN reports within a distance of the given location
     * @param location Search origin
     * @param miles Distance from origin in miles
     * @return Set of reports within miles of location
     */
    private Set<Report> getReportsNear(Location location, Double miles, Set<Report> reportsToCheck) {
        //Degree of latitude is ~69 miles apart
        //lol 69
        return reportsToCheck.stream()
                .filter(report -> !report.isHidden() && Math.abs(location.getLatitude() - report.getLocation().getLatitude()) * 69 < miles)
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
        return getReportsNear(location, 2.0, (Set) qualityReports);
    }
    /**
     * Gets all NON-HIDDEN source reports within 2 miles of the given location
     * @param location Search origin
     * @return Set of reports within 2 miles of location
     */
    public Set getSourceReportsNear(Location location) {
        return getReportsNear(location, 2.0, (Set) waterSourceReports);
    }
    
    /**
     * Modifies the username of a user
     * @param updatedUserName The new username of the user
     */
    public void modifyUserName(String updatedUserName) {
        if (CURRENT_USER == null) {
            throw new IllegalStateException("User is not logged in");
        } else try {
            if (dao.getUser(CURRENT_USER.getUsername()) != null) {
                throw new IllegalArgumentException("Username is taken");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            dao.deleteUser(CURRENT_USER.getUsername());
            dao.insertUser(CURRENT_USER.getId() + "", updatedUserName, CURRENT_USER.getPassword(),
                    CURRENT_USER.getAccountType().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        users.remove(CURRENT_USER.getUsername());
        users.put(updatedUserName, CURRENT_USER);
        CURRENT_USER.setUsername(updatedUserName);
    }
    
    /**
     * Checks the username/password pair to the database
     * @param username Username
     * @param pw Password
     */
    public void login(String username, String pw) {
        logout();
        User user = null;
        try {
            user = dao.getUser(username);
        } catch (Exception e) {
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
        }
        if (newPass.equals("")) {
            throw new IllegalArgumentException("Invalid password");
        }
        CURRENT_USER.setPassword(newPass);
    }

    /**
     * Sets the permissions of the current user
     * @param type Account type to change to
     */
    public void setAccountType(AccountType type) {
        if (CURRENT_USER == null) {
            throw new IllegalStateException("User is not logged in");
        }
        CURRENT_USER.setAccountType(type);
    }

    /**
     * Logs out the current user. If no user is logged in,
     * nothing will happen.
     */
    public void logout() {
        CURRENT_USER = null;
    }

}
