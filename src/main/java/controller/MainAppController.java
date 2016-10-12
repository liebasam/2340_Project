package controller;

/**
 * @author Soo Hyung Park
 * @author Juan Duque
 */

import java.net.URL;
import java.util.ResourceBundle;

import com.sun.tools.javac.util.Name;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.*;
import model.WaterSourceReport.*;

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

    @FXML
    private TextField locationField;

    @FXML
    private ChoiceBox<SourceType> sourceTypeChoiceBox;

    @FXML
    private ChoiceBox<QualityType> qualityTypeChoiceBox;

    @FXML
    private TableView<WaterSourceReport> ReportsTable;

    @FXML
    private TableColumn<WaterSourceReport, String> colLocation;

    @FXML
    private TableColumn<WaterSourceReport, String> colQuality;

    @FXML
    private TableColumn<WaterSourceReport, String> colSource;

    @FXML
    private TableColumn<WaterSourceReport, String> colDate;

    @FXML
    private TableColumn<WaterSourceReport, String> colUser;

    @FXML
    private TableColumn<WaterSourceReport, String> colReportID;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        accountTypeChoiceBox.getItems().setAll(AccountType.values());
        sourceTypeChoiceBox.getItems().setAll(SourceType.values());
        qualityTypeChoiceBox.getItems().setAll(QualityType.values());

        ReportsTable.setItems(getWaterSourceReports());
        colLocation.setCellValueFactory(new PropertyValueFactory<WaterSourceReport, String>("location"));
        colQuality.setCellValueFactory(new PropertyValueFactory<WaterSourceReport, String>("quality"));
        colSource.setCellValueFactory(new PropertyValueFactory<WaterSourceReport, String>("type"));
        colDate.setCellValueFactory(new PropertyValueFactory<WaterSourceReport, String>("SubmissionDate"));
        colUser.setCellValueFactory(new PropertyValueFactory<WaterSourceReport, String>("SubmitterUsername"));
        //colReportID.setCellValueFactory(new PropertyValueFactory<WaterSourceReport, String>("reportNumber"));
        ReportsTable.getColumns().setAll(colUser, colDate, colLocation, colSource, colQuality);
    }

    private ObservableList<WaterSourceReport> getWaterSourceReports() {
        Model model = Model.getInstance();
        ObservableList<WaterSourceReport> waterSourceReports = model.waterSourceReports;
        return waterSourceReports;
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
    private void onSubmitPressed() {
        Model model = Model.getInstance();
        String location = locationField.getText();
        SourceType source = sourceTypeChoiceBox.getValue();
        QualityType quality = qualityTypeChoiceBox.getValue();

        if(ControllerUtils.isEmpty(location)) {
            ControllerUtils.createErrorMessage(stage, "Submit Report Error", "One or more fields are empty");
        } else if(source == null) {
            ControllerUtils.createErrorMessage(stage, "Submit Report Error", "Please select a source type");
        } else if(quality == null) {
            ControllerUtils.createErrorMessage(stage, "Submit Report Error", "Please select a quality type");
        } else {
            model.createReport(currentUser.getUsername(), location, source, quality);
            ControllerUtils.createMessage(stage, "Submit Report", "Success",
                    "Your water source report has been added", Alert.AlertType.CONFIRMATION);
        }
    }

    @FXML
    private void onReportCancelPressed() {
        locationField.setText("");
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