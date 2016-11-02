package controller;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Model;

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
        primaryStage.setOnHiding(event -> {
            try {
                Model.getInstance().save();
            } catch (Exception e) {
                System.out.println("Could not save model");
                e.printStackTrace();
            }
        });
        primaryStage.show();
        
        WelcomeController controller = loader.getController();
        controller.setStage(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
