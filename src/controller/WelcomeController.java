package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Model;

import java.io.IOException;

public class WelcomeController
{
    private Stage stage;
    
    @FXML
    TextField usernameField;
    
    @FXML
    PasswordField passwordField;
    
    public void setStage(Stage stage)
    {
        this.stage = stage;
    }
    
    @FXML
    private void onSignInPressed()
    {
        boolean filledOutFields = true;
        String errorMessage = "";
        
        String username = usernameField.getText();
        if(isEmpty(username)) {
            filledOutFields = false;
            errorMessage += "Please fill out the username field.\n";
        }
    
        String password = passwordField.getText();
        if(isEmpty(password)) {
            filledOutFields = false;
            errorMessage += "Please fill out the password field.";
        }
        
        if(filledOutFields) {
            // TODO: This kinda demonstrates login like behavior.
            // NEED TO ASK TA IF THIS IS OK!!!!!!
            login(username, password);

        } else {
            createErrorMessage("All fields not filled out:", errorMessage);
        }
    }
    
    private boolean isEmpty(String input)
    {
        return input == null || input.length() == 0;
    }
    
    private void createErrorMessage(String header, String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.show();
    }



    private void login(String username, String password) {
        Model model = Model.getInstance();
        if (model.checkAccount(username, password)) {
            showMainApp(username, password);

        } else {
            createErrorMessage("Login Failure",
                    "Wrong username and/or password.");
        }
    }



    /**
     *
     * Calls in main page
     *
     */
    private void showMainApp(String username, String pw) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/app.fxml"));
            BorderPane page = loader.load();

            Scene openScene = new Scene(page);
            stage.setScene(openScene);
            stage.show();

            // TODO: THIS IS A NOT SECURE AT ALL< BUT AT LEAST IT KINDA WORKS
            // TODO: FIX IF NEEDED!!!!!

            MainAppController mainAppCon = loader.getController();
            mainAppCon.setStage(stage);
            mainAppCon.setUser(Model.getInstance().getAccount(username, pw));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
