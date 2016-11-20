package jdbc;

import model.*;

import javax.jws.soap.SOAPBinding;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Juan on 01/11/2016.
 */
public class DataAccessObject {

    private static Connection connection;

    private static final String USER_TABLE = "Users";
    private static final String WATER_REPORT_TABLE = "WaterSourceReports";
    private static final String QUALITY_REPORT_TABLE = "QualityReports";

    public DataAccessObject() throws SQLException {

    }

    private static void loadDriver() throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private static void establishConnection() throws SQLException {
        try {
            loadDriver();
            connection = DriverManager.getConnection("jdbc:mysql://50.62.209.113:3306",
                            "Juan", "Ifrit2340");
        } catch (SQLException e) {
            // handle any errors
            throw new SQLException("ERROR: Obtaining connection: " + e.getMessage());
        } catch (Exception e) {
            throw new SQLException("ERROR: Loading driver: " + e.getMessage());
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

    public void insertUser(String id, String name, String password, String type) throws Exception {
        this.establishConnection();
        Statement statement = connection.createStatement();
        statement.executeQuery("USE juanduquevan_");
        statement.executeUpdate("INSERT INTO " + USER_TABLE + " VALUES ('" + id + "', '" + name + "', '" + password +
                "', '" + type + "')");
        this.closeConnection();
    }

    public static User getUser(String username) throws Exception {
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
        return new User(name, password, accountType, id);
    }

    public Map<String,User> getUsers() throws Exception {
        Map<String, User> users = new HashMap<>();
        establishConnection();
        Statement statement = connection.createStatement();
        statement.executeQuery("USE juanduquevan_");
        ResultSet rs = statement.executeQuery("SELECT * FROM " + USER_TABLE);
        if (!rs.next() ) {
            return users;
        }
        while(rs.next()) {
            Integer id = rs.getInt("ID");
            String name = rs.getString("NAME");
            String password = rs.getString("PASSWORD");
            String type = rs.getString("TYPE");
            AccountType accountType = null;
            if("User".equals(type)) {
                accountType = AccountType.User;
            } else if("Worker".equals(type)) {
                accountType = AccountType.Worker;
            } else if("Manager".equals(type)) {
                accountType = AccountType.Manager;
            } else {
                accountType = AccountType.Admin;
            }
            users.put(name, new User(name, password, accountType, id));
        }
        closeConnection();
        return users;
    }

    public void deleteUser(String username) throws Exception {
        this.establishConnection();
        Statement statement = connection.createStatement();
        statement.executeQuery("USE juanduquevan_");
        statement.executeUpdate("DELETE FROM " + USER_TABLE + " WHERE NAME = " + username);
        this.closeConnection();
    }

    public void insertSourceReport(String id, String username, String latitude, String longitude, String source,
                                  String quality, String hidden) throws Exception {
        this.establishConnection();
        Statement statement = connection.createStatement();
        statement.executeQuery("USE juanduquevan_");
        statement.executeUpdate("INSERT INTO " + WATER_REPORT_TABLE + " VALUES ('" + id + "', '" + username + "', '" +
                latitude + "', '" + longitude + "', '" + source + "', '" + quality + "', NOW(), '" + hidden + "')");
        this.closeConnection();
    }

    public Set<WaterSourceReport> getSourceReports() throws Exception{
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

    public void insertQualityReport(String id, String username, String latitude, String longitude, String condition,
                                   String virusPPM, String contaminantPPM, String hidden) throws Exception {
        this.establishConnection();
        Statement statement = connection.createStatement();
        statement.executeQuery("USE juanduquevan_");
        statement.executeUpdate("INSERT INTO " + QUALITY_REPORT_TABLE + " VALUES ('" + id + "', '" + username + "', '" +
                latitude + "', '" + longitude + "', '" + condition + "', '" + virusPPM + "', '" + contaminantPPM
                + "', NOW(), '" + hidden + "')");
        this.closeConnection();
    }

    public Set<QualityReport> getQualityReports() throws Exception{
        Set<QualityReport> qualityReports = new HashSet<>();
        this.establishConnection();
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

            QualityReport report = new QualityReport(user, location, waterCondition, date, virusPPM, contaminantPPM,
                    hidden);
            qualityReports.add(report);
        }
        return qualityReports;
    }

}

