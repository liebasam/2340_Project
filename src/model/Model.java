package model;

import java.util.HashMap;
import java.util.Map;

public class Model {
    private static final Model instance = new Model();
    public static Model getInstance() { return instance; }

    // TODO: I USED HASHMAP HERE! IDK IF WE HAVE TO CHANGE THIS TO OBSEVABLE LIST.
    private final Map<String, User> users = new HashMap<>();

    private Model() {
        users.put("abc", new User("abc", "def", 1));
    }

    /**
     * Checks the username/password pair to the database
     * @param username Username
     * @param pw Password
     * @return True if the user/pass combo is valid, false othewise
     */
    public boolean checkAccount(String username, String pw) {
        return users.get(username)
                != null && users.get(username).getPassword().equals(pw);
    }

    /**
     * Creates a new username/password pair
     * @param username Username
     * @param pw Password
     * @return The newly-created user
     */
    public User createAccount(String username, String pw) {
        if (users.get(username) != null) {
            throw new IllegalArgumentException("Username is taken");
        }
        int id = 1; //UPDATE TO BE MEANINGFUL
        User user = new User(username, pw, id);
        users.put(username, user);
        return user;
    }

    /**
     * Retrieves an account with the given username/password combo
     * @param username Username
     * @param pw Password
     * @return The user if the user/pass is correct, null otherwise
     */
    public User getAccount(String username, String pw) {
        if (checkAccount(username, pw)) {
            return users.get(username);
        }
        return null;
    }


}
