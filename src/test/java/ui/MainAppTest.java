package ui;

import controller.MainAppController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import model.AccountType;
import model.Model;
import org.junit.Before;
import org.loadui.testfx.GuiTest;

public class MainAppTest extends GuiTest
{
    @Override
    public Parent getRootNode() {
        Model model = Model.getTestInstance();
        model.createAccount("user", "password", AccountType.Admin);
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/welcome.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch(Exception e) {
            throw new RuntimeException("Error loading welcome.fxml");
        }
    
        MainAppController controller = loader.getController();
        controller.setStage(stage);
        controller.setModel(model);
        
        return root;
    }
    
    @Before
    public void setup() {
    
    }
}
