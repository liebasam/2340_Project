package model;

import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;

public class CreateAccountTest
{
    private User userObj1;
    private final String user1 = "user";
    private final String pass1 = "pass";

    private User userObj2;
    private final String user2 = "bob";

    private Model model;

    @Before
    public void setup() {
        model = Model.getTestInstance();
        userObj1 = model.createAccount(user1, pass1, AccountType.User);
//        assertEquals(userObj1, model.getUsers().get(user1));
    }

    @Test
    public void testCreateAccount() {
        String pass2 = "lel";
        userObj2 = model.createAccount(user2, pass2, AccountType.Manager);
//        assertEquals(userObj2, model.getUsers().get(user2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateDuplicateAccount() {
        model.createAccount(user1, pass1, AccountType.Worker);
    }

    @Test
    public void changeUsernameCreateAccount() {
        model.login(user1, pass1);
        model.modifyUserName("test_username");
        model.logout();
        userObj2 = model.createAccount(user1, pass1, AccountType.User);
        assertNotNull(userObj2);
//        assertEquals(userObj1, model.getUsers().get("test_username"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void changeUsernameCreateDuplicate() {
        model.login(user1, user2);
        model.modifyUserName("test_username");
        model.logout();
        model.createAccount("test_username", pass1, AccountType.User);
    }

}