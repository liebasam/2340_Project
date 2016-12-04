package ui;

import controller.WelcomeController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import model.AccountType;
import model.Model;
import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

public class EditUserTest extends GuiTest
{
    private Model model;
    
    @Override
    public Parent getRootNode() {
        model = Model.getTestInstance();
        model.createAccount("user", "password", AccountType.User);
        
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
    
    @Before
    public void setup() {
        
    }
    
    @Test
    public void testInvalidEditUser() {
        
    }
    
    @Test
    public void testEditUser() {
    
    }
    
    private void editUser(String username, String password, String accountType) {
        
    }
}
