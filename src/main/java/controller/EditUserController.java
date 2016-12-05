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
import model.User;

/**
 * Controller for the edit account dialog
 */
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
    
    @Override
    public void setModel(Model model) {
        this.model = model;
        User user = model.getCurrentUser();
        usernameField.setText(user.getUsername());
        passwordField.setText(user.getPassword());
        passwordConfirmField.setText(user.getPassword());
        accountTypeChoiceBox.setValue(user.getAccountType());
    }
    
    @FXML
    private void initialize() {
        accountTypeChoiceBox.getItems().setAll(AccountType.values());
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
        final int MIN_PASS_LENGTH = 5;
        User user = model.getCurrentUser();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confPass = passwordConfirmField.getText();
        AccountType accountType = accountTypeChoiceBox.getValue();
        
        if(isEmpty(username, password, confPass)) {
            createErrorMessage("Account Edit Error", "One or more fields are empty");
        } else if (!password.equals(confPass)) {
            createErrorMessage("Account Edit Error", "Passwords do not match");
        } else if (!isValidUsername(username)) {
            createErrorMessage("Account Edit Error", "Username must be lowercase and alphanumeric");
        } else if(password.length() < MIN_PASS_LENGTH) {
            createErrorMessage("Account Edit Error", "Password must be at least 6 characters");
        } else {
            String changes = "";
            if(!username.equals(user.getUsername())) {
                try {
                    model.modifyUserName(username);
                    changes += "Username changed to " + username + "\n";
                } catch (IllegalArgumentException e) {
                    createErrorMessage("Account Edit Error", "Username already exists");
                    return;
                }
            }
            if(!password.equals(user.getPassword())) {
                try {
                    model.setPassword(password);
                    changes += "Password changed\n";
                } catch (IllegalArgumentException e) {
                    createErrorMessage("Account Edit Error", "New password is invalid");
                    return;
                }
            }
            if(!accountType.equals(user.getAccountType())) {
                model.setAccountType(accountType);
                changes += "Account type changed to " + accountType.toString().toLowerCase();
            }
            if(changes.length() < 1) {
                changes = "No changes made";
            }
            
            Alert message = createMessage("Account Edit", "Successfully edited account",
                    changes, Alert.AlertType.INFORMATION);
            message.setOnCloseRequest(event -> stage.close());
        }
    }

    private static boolean isValidUsername(String name) {
        final boolean[] isValid = {(name.length() <= 15)};
        name.chars().forEach(e -> isValid[0] = isValid[0] && (Character.isDigit(e) || (Character.isAlphabetic(e) &&
                Character.isLowerCase(e))));
        return isValid[0];
    }
}
