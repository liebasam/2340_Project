package controller;

/**
 * @author Soo Hyung Park
 * @author Juan Duque
 */

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.service.geocoding.GeocoderStatus;
import com.lynden.gmapsfx.service.geocoding.GeocodingResult;
import com.lynden.gmapsfx.service.geocoding.GeocodingService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.AccountType;
import model.Location;
import model.Model;
import model.WaterSourceReport;
import model.WaterSourceReport.QualityType;
import model.WaterSourceReport.SourceType;
import netscape.javascript.JSObject;

import java.net.URL;
import java.util.ResourceBundle;

public class MainAppController implements MapComponentInitializedListener {

    private Stage stage;
    public void setStage(Stage stage)
    {
        this.stage = stage;
    }
    private ResourceBundle resources;
    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        homeInit();
        editProfileInit();
        submitReportInit();
        //viewReportInit();
    }

    /*
            ~ HOME ~
     */
    private GeocodingService geocodingService;


    @FXML
    private TextField addressTextField;

    StringProperty address = new SimpleStringProperty();

    private GoogleMap map;
    @FXML
    private GoogleMapView mapView;
    @FXML // ResourceBundle that was given to the FXMLLoader
    private void homeInit() {
        mapView.addMapInializedListener(this);
        address.bind(addressTextField.textProperty());
    }
    private void addMarker(WaterSourceReport report) {
        MarkerOptions opt = new MarkerOptions();
        Location l = report.getLocation();
        opt.position(new LatLong(l.getLatitude(), l.getLongitude()));
        opt.title("Type of the water source: "+ report.getType().toString()
                + "\nThe quality of water: " + report.getQuality().toString()
                + "\nSubmitted by: " + report.getSubmitterUsername()
                + ", on [" + report.getSubmissionDate().toString() + "]");
        map.addMarker(new Marker(opt));
    }
    @Override
    public void mapInitialized() {
        MapOptions mapOptions = new MapOptions();
        LatLong center = new LatLong(40, 40);
        mapOptions.center(center)
                .zoom(9)
                .streetViewControl(false)
                .mapType(MapTypeIdEnum.TERRAIN);

        map = mapView.createMap(mapOptions);
        for (WaterSourceReport report : Model.getInstance().getWaterSourceReports()) {
            addMarker(report);
        }

        /* TODO: This should create a new marker every time the user clicks, but if you step through
         * it in the dubugger it seems to be interrupted midway by another handler. */
        map.addUIEventHandler(UIEventType.click, (JSObject event) -> {
            MarkerOptions markerOptions = new MarkerOptions();
            LatLong pos = (LatLong) event.getMember("latlng");
            markerOptions.position(pos)
                    .visible(true)
                    .title("title");

            map.addMarker(new Marker(markerOptions));
        });

        geocodingService = new GeocodingService();
    }



    @FXML
    public void onAddressSearchButtonClicked(ActionEvent event) {
        geocodingService.geocode(address.get(), (GeocodingResult[] results, GeocoderStatus status) -> {

            LatLong latLong = null;

            if( status == GeocoderStatus.ZERO_RESULTS) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No locations matches with your search.");
                alert.show();
                return;
            } else if( results.length > 1 ) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Showing best result.\nAdd more specific keywords next time.");
                alert.show();
                latLong = new LatLong(results[0].getGeometry().getLocation().getLatitude(), results[0].getGeometry().getLocation().getLongitude());
            } else {
                latLong = new LatLong(results[0].getGeometry().getLocation().getLatitude(), results[0].getGeometry().getLocation().getLongitude());
            }

            map.setCenter(latLong);

        });
    }

    /*
            ~ EDIT PROFILE ~
     */
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField passwordConfirmField;
    @FXML
    private ChoiceBox<AccountType> accountTypeChoiceBox;
    private void editProfileInit() {
        usernameField.setText(Model.CURRENT_USER.getUsername());
        passwordField.setText(Model.CURRENT_USER.getPassword());
        passwordConfirmField.setText(Model.CURRENT_USER.getPassword());
        accountTypeChoiceBox.setValue(Model.CURRENT_USER.getAccountType());
    }
    @FXML
    private void onCancelPressed() {
        editProfileInit();
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

            ControllerUtils.createMessage(stage, "Account Edit", "Successfully edited account", changes, Alert.AlertType.INFORMATION);
        }
    }

    /*
            ~ SUBMIT REPORT ~
     */
    @FXML
    private ChoiceBox<SourceType> sourceTypeChoiceBox;
    @FXML
    private ChoiceBox<QualityType> qualityTypeChoiceBox;
    private void submitReportInit() {
        accountTypeChoiceBox.getItems().setAll(AccountType.values());
        sourceTypeChoiceBox.getItems().setAll(SourceType.values());
        qualityTypeChoiceBox.getItems().setAll(QualityType.values());
    }
    @FXML
    private void onSubmitPressed() {
        Model model = Model.getInstance();
        SourceType source = sourceTypeChoiceBox.getValue();
        QualityType quality = qualityTypeChoiceBox.getValue();

        if(source == null) {
            ControllerUtils.createErrorMessage(stage, "Submit Report Error", "Please select a source type");
        } else if(quality == null) {
            ControllerUtils.createErrorMessage(stage, "Submit Report Error", "Please select a quality type");
        } else {
            Location l = new Location(map.getCenter().getLatitude(), map.getCenter().getLongitude());
            WaterSourceReport report = model.createReport(Model.CURRENT_USER.getUsername(), l, source, quality);
            ControllerUtils.createMessage(stage, "Submit Report", "Success",
                    "Your water source report has been added", Alert.AlertType.CONFIRMATION);
            addMarker(report);
        }
    }

    @FXML
    private void onReportCancelPressed() {
        submitReportInit();
    }

    /*
            ~ VIEW REPORTS ~
     */
    /*
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
    private void viewReportInit() {
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
        return Model.getInstance().waterSourceReports;
    }
    */
    /*
            ~ MENU BAR ~
     */
    @FXML // It will call Logout/exit menu
    private MenuBar AccountMenu;
    @FXML
    private MenuItem logout;
    @FXML
    private MenuItem exit;
    private void menuBarInit() {

    }
    @FXML
    private void onExitPressed() {
        Platform.exit();
        System.exit(0);
    }
    @FXML
    private void onLogoutPressed() throws Exception {
        Model.getInstance().logout();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/welcome.fxml"));
        Parent root = loader.load();

        stage.setTitle("Sign-in/Register");
        stage.setScene(new Scene(root, 400, 275));
        stage.show();

        WelcomeController controller = loader.getController();
        controller.setStage(stage);
    }

}