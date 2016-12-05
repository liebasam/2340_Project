package ui;

import javafx.scene.input.KeyCode;
import model.AccountType;
import org.junit.Assert;
import static org.loadui.testfx.Assertions.assertNodeExists;
import static org.loadui.testfx.controls.TextInputControls.clearTextIn;
import org.junit.Test;

public class EditUserTest extends WaterReportingGUITest
{
    @Test
    public void testInvalidEditUser() {
        login("user");
        
        click("#accountMenu");
        click("#editMenu");
        
        //all fields empty
        setUsername("");
        setPassword("");
        expectError();
        
        //password fields empty
        setUsername("available");
        expectError();
        
        //passwords don't match
        click("#passwordField");
        type("password123");
        press(KeyCode.TAB);
        type("password321");
        expectError();
                
        //password too short
        setPassword("pass");
        expectError();
        
        //username already exists
        setUsername("manager");
        setPassword("password");
        expectError();

        click("#cancel");
        logout();
    }
    
    private void expectError() {
        click("#confirm");
        assertNodeExists("Account Edit Error");
        click("OK");
    }
    
    @Test
    public void testEditUser() {
        login("user");
        
        editUser("jimmy", null, null);
        Assert.assertEquals(model.getCurrentUser().getUsername(), "jimmy");
        
        editUser(null, "jimmypass", null);
        Assert.assertEquals(model.getCurrentUser().getPassword(), "jimmypass");
        
        editUser(null, null, "Admin");
        Assert.assertEquals(model.getCurrentUser().getAccountType(), AccountType.Admin);
        
        editUser("user", "password", "User");
        Assert.assertEquals(model.getCurrentUser().getUsername(), "user");
        Assert.assertEquals(model.getCurrentUser().getPassword(), "password");
        Assert.assertEquals(model.getCurrentUser().getAccountType(), AccountType.User);
        
        logout();
    }
    
    private void editUser(String username, String password, String accountType) {
        click("#accountMenu");
        click("#editMenu");
        
        if(username != null) {
            setUsername(username);
        }
        
        if(password != null) {
            setPassword(password);
        }
        
        if(accountType != null) {
            setAccountType(accountType);
        }
        
        click("#confirm");
        click("OK");
    }
    
    private void setUsername(String username) {
        clearTextIn("#usernameField");
        click("#usernameField");
        type(username);
    }
    
    private void setPassword(String password) {
        clearTextIn("#passwordField");
        click("#passwordField");
        type(password);
        clearTextIn("#passwordConfirmField");
        click("#passwordConfirmField");
        type(password);
    }
    
    private void setAccountType(String accountType) {
        click("#accountTypeChoiceBox");
        click(accountType);
    }
}
