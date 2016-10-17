package model;

/**
 * Created by Karel on 9/22/16.
 */
public class User {

    private String username;
    private String password;
    private Integer id;
    private AccountType accountType;
    
    /**
     * getters and setters
     */
    public String getUsername() {return username;}
    void setUsername(String Username) {username = Username;}

    public String getPassword() {return password;}
    void setPassword(String Password) {password = Password;}

    public Integer getId() {return id;}
    void setId(Integer Id) {id = Id;}
    
    public AccountType getAccountType() {return accountType;}
    void setAccountType(AccountType newAccountType) {accountType = newAccountType;}

    /**
     * make a new user
     */
    User(String Username, String Password, AccountType accountType, Integer Id) {
        username = Username;
        password = Password;
        this.accountType = accountType;
        id = Id;
    }
}

