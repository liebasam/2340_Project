package model;

import org.junit.Assert;
import org.junit.Test;

public class SetAccountTypeTest
{
    @Test
    public void testSetAccountType() {
        Model model = Model.getTestInstance();
        final String username = "hacker";
        final String password = "hax0r";
        
        model.createAccount(username, password, AccountType.User);
        model.login(username, password);
        
        model.setAccountType(AccountType.Admin);
        Assert.assertEquals(model.getCurrentUser().getAccountType(), AccountType.Admin);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testSetAccountTypeLoggedOut() {
        Model.getTestInstance().setAccountType(AccountType.Worker);
    }
}
