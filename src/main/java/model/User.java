package model;

import java.io.Serializable;

/**
 * Represents a single account
 */
public class User implements Serializable {

    private String username;
    private String password;
    private final Integer id;
    private AccountType accountType;
    
    /*
     * getters and setters
     */
    
    /**
     * @return The user's username
     */
    public String getUsername() { return username; }
    public void setUsername(String Username) { username = Username; }
    
    /**
     * @return The user's password
     */
    public String getPassword() { return password; }
    public void setPassword(String Password) { password = Password; }
    
    /**
     * @return The user's account ID
     */
    public Integer getId() { return id; }
    
    /**
     * @return The user's authorization level
     */
    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType newAccountType) { accountType = newAccountType; }
    
    /**
     * Returns whether this account type has sufficient permissions
     * @param authLevel minimum authorization level required
     * @return whether this account type is at or above authLevel
     */
    public boolean isAuthorized(AccountType authLevel) {
        return accountType.isAuthorized(authLevel);
    }

    @Override
    public String toString() { return getUsername(); }

    /**
     * Make a new user
     */
    public User(String Username, String Password, AccountType accountType, Integer Id) {
        username = Username;
        password = Password;
        this.accountType = accountType;
        id = Id;
    }
}

