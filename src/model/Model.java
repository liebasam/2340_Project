package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stanley on 9/23/2016.
 */
public class Model {
    private static final Model instance = new Model();
    public static Model getInstance() { return instance; }

    // TODO: I USED HASHMAP HERE! IDK IF WE HAVE TO CHANGE THIS TO OBSEVABLE LIST.
    private final Map<String, User> users = new HashMap<>();

    private Model() {
        users.put("abc", new User("abc", "def", 1));
    }

    public boolean checkAccount(String username, String pw) {
        if (users.get(username) == null) {
            return false;
        }
        if (users.get(username).getPassword().equals(pw)) {
            return true;
        } else {
            return false;
        }
    }

    public User getAccount(String username) {
        return users.get(username);
    }

}
