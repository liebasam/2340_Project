package controller;

/**
 * Sample Skeleton for 'app.fxml' Controller Class
 */

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class MainAppController {

    private Stage stage;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // It will call Logout/exit menu
    private MenuBar AccountMenu;

    @FXML
    private MenuItem login_logout;

    @FXML
    private MenuItem exit;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

    }

    @FXML
    private void onExitPressed() {
        System.out.println("EXITP PLZ");
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void onLogPressed() throws Exception {
        System.out.println(current.getUsername() + " logged out");
        current = null;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../view/welcome.fxml"));
        Parent root = loader.load();

        stage.setTitle("Sign-in/Register");
        stage.setScene(new Scene(root, 400, 275));
        stage.show();

        WelcomeController controller = loader.getController();
        controller.setStage(stage);
    }

    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    // TODO: Not good? NEED more encapsulation???
    private model.User current;
    public void setUser(model.User user)  {
        current = user;
        System.out.println(user.getUsername() + " logged in");
    }
}