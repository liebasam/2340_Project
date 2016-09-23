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
        return users.get(username)
                != null && users.get(username).getPassword().equals(pw);
    }

    public User getAccount(String username, String pw) {
        if (checkAccount(username, pw)) {
            return users.get(username);
        }
        return null;
    }

}
