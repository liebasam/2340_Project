package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

}
