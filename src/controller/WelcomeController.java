package controller;

import javafx.event.ActionEvent;
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
    TextField regUsernameField;
    
    @FXML
    PasswordField passwordField;

    @FXML
    PasswordField regPasswordField;

    @FXML
    PasswordField regPasswordConfirmField;
    
    void setStage(Stage stage)
    {
        this.stage = stage;
    }
    
    @FXML
    private void onSignInPressed()
    {
        String username = usernameField.getText();
        String password = passwordField.getText();
        login(username, password);
    }

    @FXML
    public void onRegisterPressed() {
        String username = regUsernameField.getText();
        String password = regPasswordField.getText();
        String confPass = regPasswordConfirmField.getText();

        if(isEmpty(username, password, confPass)) {
            createErrorMessage("Registration Error", "Not all fields are filled out");
        } else if (!password.equals(confPass)) {
            createErrorMessage("Registration Error", "Passwords do not match");
        } else {
            try {
                register(username, password);
                createMessage("Registration", "Successfully registered", "New user " + username + " created", Alert.AlertType.INFORMATION);
            } catch(IllegalArgumentException e) {
                createErrorMessage("Registration Error", "Username already exists");
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
        regUsernameField.setText("");
        regPasswordField.setText("");
        regPasswordConfirmField.setText("");
    }

    //
    //HELPER METHODS BELOW THIS LINE
    //

    /**
     * Helper method for testing if a group of strings is non-null & non-empty
     * @param fields String(s) to test
     * @return true if ANY entry is empty, false otherwise
     */
    private boolean isEmpty(String... fields) {
        for (String field : fields) {
            if (field == null || field.length() == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates and shows an error alert with the given header and message
     * @param header The error's header
     * @param message The error's message
     */
    private void createErrorMessage(String header, String message) {
        createMessage("Error", header, message, Alert.AlertType.ERROR);
    }
    
    /**
     * Creates and shows an alert with the given title, header, message, and alertType
     * @param title The message's title
     * @param header The message's header
     * @param message The message's body text (ie message)
     * @param alertType The type of alert the message is
     */
    private void createMessage(String title, String header, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.show();
    }

    /**
     * Logs in the user and shows the main app
     * @param username Username
     * @param password Password
     */
    private void login(String username, String password) {
        Model model = Model.getInstance();
        if(isEmpty(username, password)) {
            createErrorMessage("Signin Error", "Not all fields are filled out");
        } else if (model.checkAccount(username, password)) {
            showMainApp(username);
        } else {
            createErrorMessage("Signin Error", "Wrong username and/or password.");
        }
    }

    /**
     * Registers a new user
     * @param username Username
     * @param password Password
     */
    private void register(String username, String password) {
        Model model = Model.getInstance();
        model.createAccount(username, password);
    }

    /**
     * Calls in main page, user should be authenticated
     * @param username Username of logged in user
     */
    private void showMainApp(String username) {
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
            mainAppCon.setUser(Model.getInstance().getAccount(username));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
