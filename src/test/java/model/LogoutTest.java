package model;

import org.junit.Assert;
import org.junit.Test;

public class LogoutTest
{
    @Test
    public void testLogout() {
        Model model = Model.getTestInstance();
        String username = "boogabooga";
        String password = "youwontguessthis";
        
        model.createAccount(username, password, AccountType.Worker);
        model.login(username, password);
        model.logout();
        Assert.assertNull(model.getCurrentUser());
    }
}
