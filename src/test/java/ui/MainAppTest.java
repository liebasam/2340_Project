package ui;

import controller.WelcomeController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import model.AccountType;
import model.Model;
import org.junit.Assert;
import org.junit.Test;
import static org.loadui.testfx.Assertions.assertNodeExists;
import static org.loadui.testfx.Assertions.verifyThat;

import org.loadui.testfx.Assertions;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.exceptions.NoNodesFoundException;
import org.loadui.testfx.exceptions.NoNodesVisibleException;

public class MainAppTest extends GuiTest
{
    @Override
    public Parent getRootNode() {
        Model model = Model.getTestInstance();
        model.createAccount("user", "password", AccountType.User);
        model.createAccount("worker", "password", AccountType.Worker);
        model.createAccount("manager", "password", AccountType.Manager);
        model.createAccount("admin", "password", AccountType.Admin);
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/welcome.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch(Exception e) {
            throw new RuntimeException("Error loading welcome.fxml");
        }
    
        WelcomeController controller = loader.getController();
        controller.setStage(stage);
        controller.setModel(model);
        
        return root;
    }
    
    @Test
    public void testPermissions() {
        login("user");
        testPermissions(AccountType.User);
        
        changeLogin("worker");
        testPermissions(AccountType.Worker);
        
        changeLogin("manager");
        testPermissions(AccountType.Manager);
        
        changeLogin("admin");
        testPermissions(AccountType.Admin);
        
        logout();
    }
    
    private void testPermissions(AccountType authorization) {
        boolean addQualityAuthorized = authorization.isAuthorized(AccountType.Worker);
        boolean qualityHistoryAuthorized = authorization.isAuthorized(AccountType.Manager);
        
        //add quality report via menu
        click("#mapMenu");
        move("#addMenu");
        Assert.assertTrue(addQualityAuthorized == nodeVisible("#addQualityMenu"));
        click("#mapMenu");
        
        //add quality report via right click
        rightClick("#mapView");
        Assert.assertTrue(addQualityAuthorized == nodeExists("Quality Report"));
        click("Cancel");
        
        //view quality report history
        click("#viewQualityTab");
        Assert.assertTrue(qualityHistoryAuthorized == nodeVisible("#QualityHistoryTable"));
        click("#homeTab");
    }
    
    private boolean nodeExists(String node) {
        try {
            find(node);
            return true;
        } catch(NoNodesFoundException e) {
            return false;
        }
    }
    
    private boolean nodeVisible(String node) {
        try {
            find(node);
            return true;
        } catch(NoNodesVisibleException e) {
            return false;
        }
    }
    
    private void changeLogin(String username) {
        logout();
        login(username);
    }
    
    private void login(String username) {
        click("#usernameField");
        type(username);
        push(KeyCode.TAB);
        type("password");
        push(KeyCode.ENTER);
    }
    
    private void logout() {
        click("#accountMenu");
        click("#logoutMenu");
    }
}
