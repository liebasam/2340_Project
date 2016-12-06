package model;

import model.AccountType;
import model.Model;
import model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class ModifyUsernameTest
{
    private final String testUsername1 = "pizzadude";
    private final String testUsername2 = "lamplover";
    private final String password = "pass";
    
    private Model model;
    
    @Before
    public void setup() {
        model = Model.getTestInstance();
        model.createAccount(testUsername1, password, AccountType.User);
        model.createAccount(testUsername2, password, AccountType.User);
    }
    
    @Test
    public void testModifyUsername() {
        model.login(testUsername1, password);
    
        final String updatedUsername = "420harambememz";
        
        model.modifyUserName(updatedUsername);
        testUsernameChange(testUsername1, updatedUsername);
        
        model.modifyUserName(testUsername1);
        testUsernameChange(updatedUsername, testUsername1);
    }
    
    private void testUsernameChange(String oldUsername, String newUsername) {
//        Map<String, User> users = model.getUsers();
//        Assert.assertFalse("Old username should not be kept", users.containsKey(oldUsername));
//        Assert.assertTrue("New username should be mapped to user", users.containsKey(newUsername));
//        Assert.assertEquals("User object should be updated", newUsername, users.get(newUsername).getUsername());
    }
    
    @Test(expected = IllegalStateException.class)
    public void testModifyUsernameNotLoggedIn() {
        model.logout();
        model.modifyUserName("bagelmuncher69");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testModifyUsernameTakenUsername() {
        model.login(testUsername1, password);
        model.modifyUserName(testUsername2);
    }
}
