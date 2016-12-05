package ui;

import controller.WelcomeController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import model.AccountType;
import model.Model;
import org.loadui.testfx.GuiTest;

public abstract class WaterReportingGUITest extends GuiTest
{
    protected Model model;
    
    @Override
    public Parent getRootNode() {
        model = Model.getTestInstance();
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
    
    protected void login(String username) {
        click("#usernameField");
        type(username);
        push(KeyCode.TAB);
        type("password");
        push(KeyCode.ENTER);
    }
    
    protected void logout() {
        click("#accountMenu");
        click("#logoutMenu");
    }
}
