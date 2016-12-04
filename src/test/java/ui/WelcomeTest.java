package ui;

import javafx.scene.input.KeyCode;
import org.junit.Test;
import static org.loadui.testfx.controls.TextInputControls.clearTextIn;
import static org.loadui.testfx.Assertions.assertNodeExists;
import static org.loadui.testfx.Assertions.verifyThat;
import static org.loadui.testfx.controls.Commons.hasText;

public class WelcomeTest extends WaterReportingGUITest
{
    @Test
    public void testInvalidLogin() {
        //all fields empty
        click("#signIn");
        assertNodeExists("Login Error");
        
        push(KeyCode.ENTER);
        
        //password empty
        click("#usernameField");
        type("user");
        push(KeyCode.ENTER);
        assertNodeExists("Login Error");
    
        push(KeyCode.ENTER);
        clearTextIn("#usernameField");
        
        //username empty
        click("#passwordField");
        type("password");
        push(KeyCode.ENTER);
        assertNodeExists("Login Error");
    
        push(KeyCode.ENTER);
        clearTextIn("#passwordField");
    
        //incorrect login credentials
        click("#usernameField");
        type("wrong");
        push(KeyCode.TAB);
        type("wrong");
        push(KeyCode.ENTER);
        assertNodeExists("Login Error");
    
        push(KeyCode.ENTER);
        clearTextIn("#usernameField");
        clearTextIn("#passwordField");
    }
    
    @Test
    public void testValidLoginMouse() {
        click("#usernameField");
        type("user");
        verifyThat("#usernameField", hasText("user"));
        click("#passwordField");
        type("password");
        click("#signIn");
        assertNodeExists("#mainApp");
    
        logout();
    }
    
    @Test
    public void testValidLoginKeyboard() {
        click("#usernameField");
        type("user");
        verifyThat("#usernameField", hasText("user"));
        push(KeyCode.TAB);
        type("password");
        push(KeyCode.ENTER);
        assertNodeExists("#mainApp");
        
        logout();
    }
    
    @Test
    public void testInvalidRegistration() {
        //all fields empty
        click("#regTab");
        click("#register");
        assertNodeExists("Registration Error");
        
        push(KeyCode.ENTER);
        
        //passwords don't match
        click("#regUsernameField");
        type("nomatch");
        push(KeyCode.TAB);
        type("password");
        push(KeyCode.TAB);
        type("password123");
        push(KeyCode.ENTER);
        assertNodeExists("Registration Error");
        
        push(KeyCode.ENTER);
        clearTextIn("#regUsernameField");
        clearTextIn("#regPasswordField");
        clearTextIn("#regPasswordConfirmField");
    
        //username already exists
        click("#regUsernameField");
        type("user");
        push(KeyCode.TAB);
        type("password");
        push(KeyCode.TAB);
        type("password");
        push(KeyCode.ENTER);
        assertNodeExists("Registration Error");
    
        push(KeyCode.ENTER);
        clearTextIn("#regUsernameField");
        clearTextIn("#regPasswordField");
        clearTextIn("#regPasswordConfirmField");
    
        //password too short
        click("#regUsernameField");
        type("user");
        push(KeyCode.TAB);
        type("pass");
        push(KeyCode.TAB);
        type("pass");
        push(KeyCode.ENTER);
        assertNodeExists("Registration Error");
    
        push(KeyCode.ENTER);
        clearTextIn("#regUsernameField");
        clearTextIn("#regPasswordField");
        clearTextIn("#regPasswordConfirmField");
        click("#loginTab");
    }
    
    @Test
    public void testValidRegistrationMouse() {
        click("#regTab");
        click("#regUsernameField");
        type("mouse");
        verifyThat("#regUsernameField", hasText("mouse"));
        click("#regPasswordField");
        type("password");
        click("#regPasswordConfirmField");
        type("password");
        click("#register");
        assertNodeExists("Successfully registered");
        
        click("OK");
        click("#loginTab");
    }
    
    @Test
    public void testValidRegistrationKeyBoard() {
        click("#regTab");
        click("#regUsernameField");
        type("keyboard");
        verifyThat("#regUsernameField", hasText("keyboard"));
        push(KeyCode.TAB);
        type("password");
        push(KeyCode.TAB);
        type("password");
        push(KeyCode.ENTER);
        assertNodeExists("Successfully registered");
        
        push(KeyCode.ENTER);
        click("#loginTab");
    }
}
