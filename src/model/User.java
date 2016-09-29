package model;

/**
 * Created by Karel on 9/22/16.
 */
public class User implements IUser {

    private String username;
    private String password;
    private Integer id;
    private AccountType accountType;
    
    /**
     * getters and setters
     */
    public String getUsername() {return username;}
    public void setUsername(String Username) {username = Username;}

    public String getPassword() {return password;}
    public void setPassword(String Password) {password = Password;}

    public Integer getId() {return id;}
    public void setId(Integer Id) {id = Id;}
    
    public AccountType getAccountType() {return accountType;}
    public void setAccountType(AccountType newAccountType) {accountType = newAccountType;}

    /**
     * make a new user
     */
    public User(String Username, String Password, AccountType accountType, Integer Id) {
        username = Username;
        password = Password;
        id = Id;
    }
}

