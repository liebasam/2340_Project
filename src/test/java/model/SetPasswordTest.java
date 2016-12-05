package model;

import org.junit.Test;

public class SetPasswordTest
{
    private final String username = "person";
    private final String pass = "password";
    
    @Test
    public void testSetPassword() {
        Model model = Model.getTestInstance();
        final String newPass = "password123";
        
        model.createAccount(username, pass, AccountType.User);
        model.login(username, pass);
        model.setPassword(newPass);
        model.logout();
        model.login(username, newPass);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetPasswordTooShort() {
        Model model = Model.getTestInstance();
        model.createAccount(username, pass, AccountType.User);
        model.login(username, pass);
        model.setPassword("");
    }
    
    @Test(expected = IllegalStateException.class)
    public void testSetPasswordLoggedOut() {
        Model.getTestInstance().setPassword("cookie");
    }
}
