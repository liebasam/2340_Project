package model;

import java.io.Serializable;

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
    void setUsername(String Username) { username = Username; }
    
    /**
     * @return The user's password
     */
    public String getPassword() { return password; }
    void setPassword(String Password) { password = Password; }
    
    /**
     * @return The user's account ID
     */
    public Integer getId() { return id; }
    
    /**
     * @return The user's authorization level
     */
    public AccountType getAccountType() { return accountType; }
    void setAccountType(AccountType newAccountType) { accountType = newAccountType; }

    @Override
    public String toString() { return getUsername(); }

    /**
     * Make a new user
     */
    User(String Username, String Password, AccountType accountType, Integer Id) {
        username = Username;
        password = Password;
        this.accountType = accountType;
        id = Id;
    }
}

