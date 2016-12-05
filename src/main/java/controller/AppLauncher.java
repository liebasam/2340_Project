package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.Model;

/**
 * Starting point for the application
 */
public class AppLauncher extends Application
{
    //Run -> Run 'AppLauncher'
    // sample username: abc, pw: def
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/welcome.fxml"));
        Parent root = loader.load();
    
        primaryStage.setTitle("Sign-in/Register");
        primaryStage.setScene(new Scene(root, 400, 275));
        primaryStage.getIcons().add(new Image("/icons/icon.png"));
        primaryStage.show();
        WelcomeController controller = loader.getController();
        controller.setStage(primaryStage);
        controller.setModel(Model.getInstance());
    }

    /**
     * Launches the application
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
