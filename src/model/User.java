/**
 * Created by Karel on 9/22/16.
 */
public class User {

    private String username;
    private String password;

    private Integer id;

    private String firstname;
    private String lastname;

    private String email;
    private String homeAddress;

    /**
     * getters and setters
     */
    public String getUsername() {return username;}
    public void setUsername(String Username) {username = Username;}

    public String getPassword() {return password;}
    public void setPassword(String Password) {password = Password;}

    public Integer getId() {return id;}
    public void setId(Integer Id) {id = Id;}

    public String getFirstname() {return firstname;}
    public void setFirstname(String Firstname) {firstname = Firstname;}

    public String getLastname() {return lastname;}
    public void setLastname(String Lastname) {lastname = Lastname;}

    public String getEmail() {return email;}
    public void setEmail(String Email) {email = Email;}

    public String gethomeAddress() {return homeAddress;}
    public void sethomeAddress(String HomeAddress) {homeAddress = HomeAddress;}

    /**
     * make a new user
     */
    public User(String Username, String Password, Integer Id,
                String Firstname, String Lastname,
                String Email, String HomeAddress) {
        username = Username;
        password = Password;
        id = Id;
        firstname = Firstname;
        lastname = Lastname;
        email = Email;
        homeAddress = HomeAddress;
    }
}

