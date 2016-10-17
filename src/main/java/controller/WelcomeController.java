package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.AccountType;
import model.Model;

import java.io.IOException;

public class WelcomeController
{
    private Stage stage;
    
    @FXML
    TextField usernameField;

    @FXML
    TextField regUsernameField;
    
    @FXML
    PasswordField passwordField;

    @FXML
    PasswordField regPasswordField;

    @FXML
    PasswordField regPasswordConfirmField;
    
    @FXML
    ChoiceBox<AccountType> regUserTypeChoiceBox;
    
    @FXML
    private void initialize() {
        regUserTypeChoiceBox.getItems().setAll(AccountType.values());
        regUserTypeChoiceBox.setValue(AccountType.User);
        Model.getInstance();
    }
    
    void setStage(Stage stage)
    {
        this.stage = stage;
    }
    
    @FXML
    private void onSignInPressed()
    {
        String username = usernameField.getText();
        String password = passwordField.getText();
        try {
            Model.getInstance().login(username, password);
            showMainApp();
        } catch (IllegalArgumentException e) {
            ControllerUtils.createErrorMessage(stage, "Login Error", "Invalid user/pass");
        }
    }

    @FXML
    public void onRegisterPressed() {
        String username = regUsernameField.getText();
        String password = regPasswordField.getText();
        String confPass = regPasswordConfirmField.getText();

        if(ControllerUtils.isEmpty(username, password, confPass)) {
            ControllerUtils.createErrorMessage(stage, "Registration Error", "Not all fields are filled out");
        } else if (!password.equals(confPass)) {
            ControllerUtils.createErrorMessage(stage, "Registration Error", "Passwords do not match");
        } else {
            try {
                Model.getInstance().createAccount(username, password, regUserTypeChoiceBox.getValue());
                String userType = regUserTypeChoiceBox.getValue().toString().toLowerCase();
                ControllerUtils.createMessage(stage, "Registration", "Successfully registered", "New " + userType + " " + username + " created", Alert.AlertType.INFORMATION);
                resetRegistration();
            } catch(IllegalArgumentException e) {
                ControllerUtils.createErrorMessage(stage, "Registration Error", "Username already exists");
            }
        }
    }

    @FXML
    private void onCancelPressed()
    {
        usernameField.setText("");
        passwordField.setText("");
    }

    @FXML
    private void onCancelRegPressed() {
        resetRegistration();
    }

    //
    //HELPER METHODS BELOW THIS LINE
    //
    
    /**
     * Sets all registration controls back to default state
     */
    private void resetRegistration() {
        regUsernameField.setText("");
        regPasswordField.setText("");
        regPasswordConfirmField.setText("");
        regUserTypeChoiceBox.setValue(AccountType.User);
    }

    /**
     * Calls in main page, user should be authenticated
     */
    private void showMainApp() {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/app.fxml"));
            BorderPane page = loader.load();

            Scene openScene = new Scene(page);
            stage.setScene(openScene);
            stage.show();

            // TODO: THIS IS A NOT SECURE AT ALL< BUT AT LEAST IT KINDA WORKS
            // TODO: FIX IF NEEDED!!!!!

            MainAppController mainAppCon = loader.getController();
            mainAppCon.setStage(stage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
