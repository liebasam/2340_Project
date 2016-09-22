package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

import java.net.URLClassLoader;

public class AppLauncher extends Application {
    //Run -> Run 'AppLauncher'
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../view/welcome.fxml"));
        Parent root = loader.load();
    
        primaryStage.setTitle("Sign-in/Register");
        primaryStage.setScene(new Scene(root, 400, 275));
        primaryStage.show();
        
        WelcomeController controller = loader.getController();
        controller.setStage(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
