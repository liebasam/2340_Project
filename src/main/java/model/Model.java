package model;

import model.WaterSourceReport.QualityType;
import model.WaterSourceReport.SourceType;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Model implements Serializable {
    private static Model instance = new Model();
    public static Model getInstance() { return instance; }

    private static final String FILE_DIRECTORY = "./savedata/";
    private static final String FILE_NAME_EXT = "model.ser";

    private static int numUsers = 0;
    public static User CURRENT_USER;

    private final Map<String, User> users = new HashMap<>();
    public Map<String, User> getUsers() {return users;}
    private final Set<SecurityLogEntry> securityLog = new HashSet<>();
    public Set<SecurityLogEntry> getSecurityLog() {return securityLog;}
    private final Set<WaterSourceReport> waterSourceReports = new HashSet<>();
    public Set<WaterSourceReport> getWaterSourceReports() {return waterSourceReports;}
    private final Set<QualityReport> qualityReports = new HashSet<>();
    public Set<QualityReport> getQualityReports() {return qualityReports;}

    private Model() {
        //Attempt to load the model
        try {
            FileInputStream fis = new FileInputStream(FILE_DIRECTORY + FILE_NAME_EXT);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Model obj = (Model) ois.readObject();
            this.users.putAll(obj.users);
            this.securityLog.addAll(obj.securityLog);
            this.waterSourceReports.addAll(obj.waterSourceReports);
            this.qualityReports.addAll(obj.qualityReports);
            ois.close();
            fis.close();
            System.out.println("Model loaded");
        } catch (FileNotFoundException e) {
            System.out.println("Could not find serialized file");
            e.printStackTrace();
            createAccount("user", "pass", AccountType.Admin);
        } catch (Exception e) {
            System.out.println("Failed to load model");
            e.printStackTrace();
            createAccount("user", "pass", AccountType.Admin);
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
        if (users.containsKey(username.toLowerCase())) {
            throw new IllegalArgumentException("Username is taken");
        }
        User user = new User(username.toLowerCase(), pw, accountType, numUsers++);

        users.put(username.toLowerCase(), user);
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
        WaterSourceReport report = new WaterSourceReport(CURRENT_USER, location, source, quality);
        waterSourceReports.add(report);
        return report;
    }
    
    /**
     * Hides all nearby quality reports
     * @param l Location around which nearby quality reports will be hidden
     */
    public void hideQualityReportsNear(Location l) {
        for (Report closeReport : Model.getInstance().getQualityReportsNear(l)) {
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
        for (Report closeReport : Model.getInstance().getSourceReportsNear(l)) {
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
        QualityReport report = new QualityReport(CURRENT_USER, location, waterCondition, virusPpm, contaminantPpm);
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
        Set<Report> out = new HashSet<>();
        for (Report report : reportsToCheck) {
            if (!report.isHidden() && Math.abs(location.getLatitude() - report.getLocation().getLatitude()) * 69 < miles) {
                if (distBetween(location, report.getLocation()) < miles) {
                    out.add(report);
                }
            }
        }
        return out;
    }

    private double distBetween(Location loc1, Location loc2) {
        return loc1.distanceTo(loc2);
    }

    /**
     * Gets all NON-HIDDEN quality reports within 2 miles of the given location
     * @param location Search origin
     * @return Set of reports within 2 miles of location
     */
    public Set<Report> getQualityReportsNear(Location location) {
        return getReportsNear(location, 2.0, (Set) qualityReports);
    }
    /**
     * Gets all NON-HIDDEN source reports within 2 miles of the given location
     * @param location Search origin
     * @return Set of reports within 2 miles of location
     */
    public Set<Report> getSourceReportsNear(Location location) {
        return getReportsNear(location, 2.0, (Set) waterSourceReports);
    }
    
    /**
     * Modifies the username of a user
     * @param updatedUserName The new username of the user
     */
    public void modifyUserName(String updatedUserName) {
        if (CURRENT_USER == null) {
            throw new IllegalStateException("User is not logged in");
        } else if (users.containsKey(updatedUserName.toLowerCase())) {
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
     */
    public void login(String username, String pw) {
        logout();
        User user = users.get(username.toLowerCase());
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

    /**
     * Save the currently-held data
     */
    public void save() throws IOException {
        File file = new File(FILE_DIRECTORY + FILE_NAME_EXT);
        file.getParentFile().mkdirs();
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.close();
        fos.close();
        System.out.println("Model saved");
    }

}
