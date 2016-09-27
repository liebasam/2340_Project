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

        if(!isEmpty(username, password)) {
            // TODO: This kinda demonstrates login like behavior.
            // NEED TO ASK TA IF THIS IS OK!!!!!!
            login(username, password);

        } else {
            createErrorMessage("Signin Error", "Not all fields are filled out");
        }
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
     * Helper method for testing if a group of strings is non-empty
     * @param fields Strings to test
     * @return true if ANY entry is empty, false otherwise
     */
    private boolean isEmpty(String... fields) {
        for (String field : fields) {
            if (isEmpty(field)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests if a string is null or empty
     * @param input The string to test
     * @return true if the string is empty
     */
    private boolean isEmpty(String input)
    {
        return input == null || input.length() == 0;
    }

    /**
     * Creates and shows an alert with the given header and message
     * @param header The error's header
     * @param message The error's message
     */
    private void createErrorMessage(String header, String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle("Error");
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
        if (model.checkAccount(username, password)) {
            showMainApp(username, password);

        } else {
            createErrorMessage("Login Failure",
                    "Wrong username and/or password.");
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
