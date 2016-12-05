package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import model.AccountType;
import model.Model;

import java.io.IOException;

/**
 * Controller for the login/registration window
 */
public class WelcomeController extends Controller
{
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
    }
    
    @FXML
    private void onLoginKeyPressed(KeyEvent event)
    {
        if (event.getCode() == KeyCode.ENTER) {
            onSignInPressed();
        }
    }
    @FXML
    private void onSignInPressed()
    {
        String username = usernameField.getText();
        String password = passwordField.getText();
        try {
            model.login(username, password);
            showMainApp();
        } catch (IllegalArgumentException e) {
            createErrorMessage("Login Error", "Invalid user/pass");
        }
    }
    
    @FXML
    private void onRegisterKeyPressed(KeyEvent event)
    {
        if (event.getCode() == KeyCode.ENTER) {
            onRegisterPressed();
        }
    }
    @FXML
    private void onRegisterPressed() {
        final int MIN_PASS_LENGTH = 5;
        
        String username = regUsernameField.getText();
        String password = regPasswordField.getText();
        String confPass = regPasswordConfirmField.getText();

        if(isEmpty(username, password, confPass)) {
            createErrorMessage("Registration Error", "Not all fields are filled out");
        } else if (!password.equals(confPass)) {
            createErrorMessage("Registration Error", "Passwords do not match");
        } else if (!isValidUsername(username)) {
            createErrorMessage("Registration Error", "Username must be lowercase and alphanumeric");
        } else if(password.length() < MIN_PASS_LENGTH) {
            createErrorMessage("Registration Error", "Password must be at least 6 characters");
        } else {
            try {
                model.createAccount(username, password, regUserTypeChoiceBox.getValue());
                String userType = regUserTypeChoiceBox.getValue().toString().toLowerCase();
                createMessage("Registration", "Successfully registered", "New " + userType + " "
                        + username + " created", Alert.AlertType.INFORMATION);
                resetRegistration();
            } catch(IllegalArgumentException e) {
                createErrorMessage("Registration Error", "Username already exists");
            }
        }
    }
    
    private static boolean isValidUsername(String name) {
        final boolean[] isValid = {(name.length() <= 15)};
        name.chars().forEach(e -> isValid[0] = isValid[0] && (Character.isDigit(e) || (Character.isAlphabetic(e) &&
                Character.isLowerCase(e))));
        return isValid[0];
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
            stage.setTitle("Clean Water Finder");
            stage.show();

            MainAppController mainAppCon = loader.getController();
            mainAppCon.setStage(stage);
            mainAppCon.setModel(model);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
