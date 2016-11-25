package model;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author Juan
 */
public class LoginTest
{

    private static final String username = "Leopold";
    private static final String password = "Ulysses";
    private final Model model = Model.getTestInstance();


    @Before
    public void setup() throws Exception{
        try {
            model.createAccount(username, password, AccountType.Manager);
        } catch(Exception e) {
            ;
        }
    }

    @Test
    public void loginTest() {
        model.login(username, password);
        assertEquals(model.getCurrentUser().getUsername(), username.toLowerCase());
        assertEquals(model.getCurrentUser().getPassword(), password);
        model.logout();
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void invalidLoginTest() {
        model.login(username, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidUsernameLoginTest() {
        Random random = new Random(Integer.MAX_VALUE);
        String invalid = "" + random.nextInt();
        model.login(invalid, password);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidPasswordLoginTest() {
        Random random = new Random(Integer.MAX_VALUE);
        String invalid = "" + random.nextInt();
        model.login(username, invalid);
    }
}
