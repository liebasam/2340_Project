package jdbc;

import model.*;

import javax.jws.soap.SOAPBinding;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataAccessObject {

    private static Connection connection;

    private static final String USER_TABLE = "Users";
    private static final String WATER_REPORT_TABLE = "WaterSourceReports";
    private static final String QUALITY_REPORT_TABLE = "QualityReports";
    private static final Map<String, User> usersCache = new HashMap<>();
    private static Integer numUsers;

    private static void loadDriver() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
    }

    private static void establishConnection() throws SQLException, Exception {
        loadDriver();
        connection = DriverManager.getConnection("jdbc:mysql://50.62.209.113:3306",
                        "Juan", "Ifrit2340");
        Statement statement = connection.createStatement();
        statement.executeQuery("USE juanduquevan_");
        ResultSet rs = statement.executeQuery("SELECT ID FROM Users ORDER BY ID DESC LIMIT 1");
        if (!rs.next()) {
            System.out.println("No users found");
            numUsers = 0;
        } else {
            numUsers = rs.getInt("ID");
        }
    }

    private static void closeConnection() throws Exception {
        try {
            connection.close();
            connection = null;
        } catch (SQLException exception) {
            throw new Exception("ERROR: DAO: closeConnection() = closing connection.");
        }
    }

    /**
     * Inserts a user into the database and user cache
     * @param name Username
     * @param password Password
     * @param type Account type
     * @throws Exception
     */
    public static User insertUser(String name, String password, AccountType type) throws Exception {
        establishConnection();
        Statement statement = connection.createStatement();
        statement.executeQuery("USE juanduquevan_");
        statement.executeUpdate("INSERT INTO " + USER_TABLE + " VALUES ('" + ++numUsers + "', '" + name + "', '" + password +
                "', '" + type + "')");
        User user = new User(name, password, type, numUsers);
        usersCache.put(name, user);
        closeConnection();
        return user;
    }

    /**
     * Gets a user from the database or user cache
     * @param username Username of the user
     * @return The User object, if it exists. Null otherwise
     * @throws Exception
     */
    public static User getUser(String username) throws Exception {
        if (usersCache.get(username) != null) {
            return usersCache.get(username);
        }
        establishConnection();
        Statement statement = connection.createStatement();
        statement.executeQuery("USE juanduquevan_");
        ResultSet rs = statement.executeQuery("SELECT * FROM " + USER_TABLE + " WHERE NAME = '" + username + "'");
        if (!rs.next() ) {
            return null;
        }
        Integer id = rs.getInt("ID");
        String name = rs.getString("NAME");
        String password = rs.getString("PASSWORD");
        String type = rs.getString("TYPE");
        AccountType accountType;
        if("User".equals(type)) {
            accountType = AccountType.User;
        } else if("Worker".equals(type)) {
            accountType = AccountType.Worker;
        } else if("Manager".equals(type)) {
            accountType = AccountType.Manager;
        } else {
            accountType = AccountType.Admin;
        }
        closeConnection();
        User userObj = new User(name, password, accountType, id);
        usersCache.put(name, userObj);
        return userObj;
    }

    /**
     * Deletes a user from the database and cache
     * @param username Username of user to delete
     * @throws Exception
     */
    public static void deleteUser(String username) throws Exception {
        establishConnection();
        Statement statement = connection.createStatement();
        statement.executeQuery("USE juanduquevan_");
        statement.executeUpdate("DELETE FROM " + USER_TABLE + " WHERE NAME = " + username);
        usersCache.remove(username);
        closeConnection();
    }

    /**
     * Edits a user's information
     * @param username Username of user to change
     * @param newName New username
     * @param password New password
     * @param type New account type
     * @throws Exception
     */
    public static User editUser(String username, String newName, String password, AccountType type) throws Exception {
        establishConnection();
        Statement statement = connection.createStatement();
        statement.executeQuery("USE juanduquevan_");
        statement.executeUpdate("UPDATE " + USER_TABLE + " SET NAME='" + newName + "',PASSWORD='" + password + "',TYPE='" + type + "' WHERE NAME='" + username + "'");
        User userObj = usersCache.get(username);
        userObj.setUsername(newName);
        userObj.setPassword(password);
        userObj.setAccountType(type);
        usersCache.remove(username);
        usersCache.put(newName, userObj);
        closeConnection();
        return userObj;
    }

    public static void insertSourceReport(String id, String username, String latitude, String longitude, String source,
                                  String quality, String hidden) throws Exception {
        establishConnection();
        Statement statement = connection.createStatement();
        statement.executeQuery("USE juanduquevan_");
        statement.executeUpdate("INSERT INTO " + WATER_REPORT_TABLE + " VALUES ('" + id + "', '" + username + "', '" +
                latitude + "', '" + longitude + "', '" + source + "', '" + quality + "', NOW(), '" + hidden + "')");
        closeConnection();
    }

    public static Set<WaterSourceReport> getSourceReports() throws Exception{
        Set<WaterSourceReport> waterSourceReports = new HashSet<>();
        establishConnection();
        Statement statement = connection.createStatement();
        statement.executeQuery("USE juanduquevan_");
        ResultSet rs = statement.executeQuery("SELECT * FROM " + WATER_REPORT_TABLE);
        if(!rs.next()) {
            return waterSourceReports;
        }
        while(rs.next()) {
            Double latitude = rs.getDouble("LATITUDE");
            Double longitude = rs.getDouble("LONGITUDE");
            String username = rs.getString("USER");
            String source = rs.getString("SOURCE_TYPE");
            String quality = rs.getString("QUALITY_TYPE");
            User user = getUser(username);
            WaterSourceReport.SourceType sourceType;
            WaterSourceReport.QualityType qualityType;
            Location location = new Location(latitude, longitude);
            Date date = rs.getDate("SUBMIT_DATE");
            if("BOTTLED".equals(source)) {
                sourceType = WaterSourceReport.SourceType.BOTTLED;
            } else if("WELL".equals(source)) {
                sourceType = WaterSourceReport.SourceType.WELL;
            } else if("RIVER".equals(source)) {
                sourceType = WaterSourceReport.SourceType.RIVER;
            } else if("LAKE".equals(source)) {
                sourceType = WaterSourceReport.SourceType.LAKE;
            } else if("STREAM".equals(source)) {
                sourceType = WaterSourceReport.SourceType.STREAM;
            } else {
                sourceType = WaterSourceReport.SourceType.OTHER;
            }
            if("WASTE".equals(quality)) {
                qualityType = WaterSourceReport.QualityType.WASTE;
            } else if("TREATABLE_CLEAR".equals(quality)) {
                qualityType = WaterSourceReport.QualityType.TREATABLE_CLEAR;
            } else if("TREATABLE_MUDDY".equals(quality)) {
                qualityType = WaterSourceReport.QualityType.TREATABLE_MUDDY;
            } else {
                qualityType = WaterSourceReport.QualityType.POTABLE;
            }
            WaterSourceReport report = new WaterSourceReport(user, location, sourceType, qualityType, date);
            waterSourceReports.add(report);
        }
        return waterSourceReports;
    }

    public static void insertQualityReport(String id, String username, String latitude, String longitude, String condition,
                                   String virusPPM, String contaminantPPM, String hidden) throws Exception {
        establishConnection();
        Statement statement = connection.createStatement();
        statement.executeQuery("USE juanduquevan_");
        statement.executeUpdate("INSERT INTO " + QUALITY_REPORT_TABLE + " VALUES ('" + id + "', '" + username + "', '" +
                latitude + "', '" + longitude + "', '" + condition + "', '" + virusPPM + "', '" + contaminantPPM
                + "', NOW(), '" + hidden + "')");
        closeConnection();
    }

    public static Set<QualityReport> getQualityReports() throws Exception{
        Set<QualityReport> qualityReports = new HashSet<>();
        establishConnection();
        Statement statement = connection.createStatement();
        statement.executeQuery("USE juanduquevan_");
        ResultSet rs = statement.executeQuery("SELECT * FROM " + QUALITY_REPORT_TABLE);
        if(!rs.next()) {
            return qualityReports;
        }
        while(rs.next()) {
            Double latitude = rs.getDouble("LATITUDE");
            Double longitude = rs.getDouble("LONGITUDE");
            String username = rs.getString("USER");
            String condition = rs.getString("WATER_CONDITION");
            Double virusPPM = rs.getDouble("VIRUS_PPM");
            Double contaminantPPM = rs.getDouble("CONTAMINANT_PPM");
            Boolean hidden = rs.getBoolean("IS_HIDDEN");
            User user = getUser(username);
            QualityReport.WaterCondition waterCondition;
            Location location = new Location(latitude, longitude);
            Date date = rs.getDate("SUBMIT_DATE");
            if("SAFE".equals(condition)) {
                waterCondition = QualityReport.WaterCondition.SAFE;
            } else if("TREATABLE".equals(condition)) {
                waterCondition = QualityReport.WaterCondition.TREATABLE;
            } else {
                waterCondition = QualityReport.WaterCondition.UNSAFE;
            }

            QualityReport report = new QualityReport(user, location, waterCondition, virusPPM, contaminantPPM, date,
                    hidden);
            qualityReports.add(report);
        }
        return qualityReports;
    }

}

