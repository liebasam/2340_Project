package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import model.AccountType;
import model.Model;

public class EditUserController extends Controller
{
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField passwordConfirmField;
    @FXML
    private ChoiceBox<AccountType> accountTypeChoiceBox;
    
    @FXML
    private void initialize() {
        accountTypeChoiceBox.getItems().setAll(AccountType.values());
        
        usernameField.setText(Model.CURRENT_USER.getUsername());
        passwordField.setText(Model.CURRENT_USER.getPassword());
        passwordConfirmField.setText(Model.CURRENT_USER.getPassword());
        accountTypeChoiceBox.setValue(Model.CURRENT_USER.getAccountType());
    }
    
    @FXML
    private void onCancelPressed() {
        stage.close();
    }
    
    @FXML
    private void onKeyPressed(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
            onConfirmPressed();
        }
    }
    
    @FXML
    private void onConfirmPressed() {
        Model model = Model.getInstance();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confPass = passwordConfirmField.getText();
        AccountType accountType = accountTypeChoiceBox.getValue();
        
        if(ControllerUtils.isEmpty(username, password, confPass)) {
            ControllerUtils.createErrorMessage(stage, "Account Edit Error", "One or more fields are empty");
        } else if (!password.equals(confPass)) {
            ControllerUtils.createErrorMessage(stage, "Account Edit Error", "Passwords do not match");
        } else {
            String changes = "";
            if(!username.equals(Model.CURRENT_USER.getUsername())) {
                try {
                    model.modifyUserName(username);
                    changes += "Username changed to " + username + "\n";
                } catch (IllegalArgumentException e) {
                    ControllerUtils.createErrorMessage(stage, "Account Edit Error", "Username already exists");
                }
            }
            if(!password.equals(Model.CURRENT_USER.getPassword())) {
                try {
                    model.setPassword(password);
                    changes += "Password changed\n";
                } catch (IllegalArgumentException e) {
                    ControllerUtils.createErrorMessage(stage, "Account Edit Error", "New password is invalid");
                }
            }
            if(!accountType.equals(Model.CURRENT_USER.getAccountType())) {
                model.setAccountType(accountType);
                changes += "Account type changed to " + accountType.toString().toLowerCase();
                
            }
            if(changes.equals("")) {
                changes = "No changes made";
            }
            
            Alert message = ControllerUtils.createMessage(stage, "Account Edit", "Successfully edited account", changes, Alert.AlertType.INFORMATION);
            message.setOnCloseRequest(event -> stage.close());
        }
    }
}
