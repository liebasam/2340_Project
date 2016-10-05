package controller;

/**
 * Sample Skeleton for 'app.fxml' Controller Class
 * @author Soo Hyung Park
 */

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.AccountType;
import model.Model;
import model.User;

public class MainAppController {

    private Stage stage;
    private User currentUser;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // It will call Logout/exit menu
    private MenuBar AccountMenu;

    @FXML
    private MenuItem logout;

    @FXML
    private MenuItem exit;

    @FXML
    private Text login_name;
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField passwordConfirmField;
    
    @FXML
    private ChoiceBox<AccountType> accountTypeChoiceBox;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        accountTypeChoiceBox.getItems().setAll(AccountType.values());
    }

    @FXML
    private void onExitPressed() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void onLogoutPressed() throws Exception {
        currentUser = null;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/welcome.fxml"));
        Parent root = loader.load();

        stage.setTitle("Sign-in/Register");
        stage.setScene(new Scene(root, 400, 275));
        stage.show();

        WelcomeController controller = loader.getController();
        controller.setStage(stage);
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
            if(!username.equals(currentUser.getUsername())) {
                if(model.usernameTaken(username)) {
                    ControllerUtils.createErrorMessage(stage, "Account Edit Error", "Username already exists");
                    return;
                }
                model.modifyUserName(currentUser, username);
                resetHome();
                changes += "Username changed to " + username + "\n";
            }
            if(!password.equals(currentUser.getPassword())) {
                currentUser.setPassword(password);
                changes += "Password changed\n";
            }
            if(!accountType.equals(currentUser.getAccountType())) {
                currentUser.setAccountType(accountType);
                changes += "Account type changed to " + accountType.toString().toLowerCase();
            }
            if(changes.equals("")) {
                changes = "No changes made";
            }
            
            ControllerUtils.createMessage(stage, "Account Edit", "Successfully edited account", changes, Alert.AlertType.INFORMATION);
        }
    }
    
    @FXML
    private void onCancelPressed() {
        resetEditUser();
    }

    public void setStage(Stage stage)
    {
        this.stage = stage;
    }
    
    public void setUser(User user)  {
        currentUser = user;
        resetHome();
        resetEditUser();
    }
    
    private void resetEditUser() {
        usernameField.setText(currentUser.getUsername());
        passwordField.setText(currentUser.getPassword());
        passwordConfirmField.setText(currentUser.getPassword());
        accountTypeChoiceBox.setValue(currentUser.getAccountType());
    }
    
    private void resetHome() {
        login_name.setText("Hello, " + currentUser.getUsername() + "!");
    }
}