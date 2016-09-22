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
    public void start(Stage primaryStage) throws Exception{
        System.out.println(getClass().getClassLoader().getResource("../welcome.fxml"));
        URL[] urls = ((URLClassLoader)ClassLoader.getSystemClassLoader()).getURLs();
        for(URL url: urls) {
            System.out.println(url.getFile());
        }
        Parent root = FXMLLoader.load(getClass().getResource("../welcome.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
