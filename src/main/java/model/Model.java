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

    private Model() {
        //Attempt to load the model
        try {
            FileInputStream fis = new FileInputStream(FILE_DIRECTORY + FILE_NAME_EXT);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Model obj = (Model) ois.readObject();
            this.users.putAll(obj.users);
            this.securityLog.addAll(obj.securityLog);
            this.waterSourceReports.addAll(obj.waterSourceReports);
            ois.close();
            fis.close();
            System.out.println("Model loaded");
            System.out.println(waterSourceReports.size());
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
     * @param username Username of the submitter
     * @param location Location of submission
     * @param source Source type
     * @param quality Quality type
     * @return The newly-created water source report
     */
    public WaterSourceReport createReport(String username, Location location, SourceType source, QualityType quality) {
        if (CURRENT_USER == null) {
            throw new IllegalStateException("User is not logged in");
        }
        WaterSourceReport report = new WaterSourceReport(username, location, source, quality);
        waterSourceReports.add(report);
        return report;
    }

    public WaterSourceReport createReport(String username, double lat, double lng, SourceType source, QualityType quality) {
        return this.createReport(username, new Location(lat, lng), source, quality);
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
        try {logout();} catch (IllegalStateException e) {}
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
