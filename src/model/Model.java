package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Model {
    private static final Model instance = new Model();
    public static Model getInstance() { return instance; }
    
    private final Map<String, User> users = new HashMap<>();
    private final Set<SecurityLogEntry> securityLog = new HashSet<>();

    private Model() {}

    /**
     * Creates a new username/password pair
     * @param username Username
     * @param pw Password
     * @return The newly-created user
     * @throws IllegalArgumentException if username is already in use
     */
    public User createAccount(String username, String pw, AccountType accountType) {
        if (users.get(username) != null) {
            throw new IllegalArgumentException("Username is taken");
        }
        int id = 1; //UPDATE TO BE MEANINGFUL
        User user = new User(username, pw, accountType, id);
        users.put(username, user);
        return user;
    }
    
    /**
     * Checks the username/password pair to the database
     * @param username Username
     * @param pw Password
     * @return True if the user/pass combo is valid, false otherwise
     */
    public boolean checkAccount(String username, String pw) {
        User user = users.get(username);
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
        if(users.get(username) == null) {
            throw new IllegalArgumentException("Invalid username");
        }
        return users.get(username);
    }
}
