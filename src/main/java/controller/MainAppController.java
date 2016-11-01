package controller;

/**
 * @author Soo Hyung Park
 * @author Juan Duque
 */

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.service.geocoding.GeocoderStatus;
import com.lynden.gmapsfx.service.geocoding.GeocodingResult;
import com.lynden.gmapsfx.service.geocoding.GeocodingService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.*;
import model.WaterSourceReport.QualityType;
import model.WaterSourceReport.SourceType;

import java.net.URL;
import java.util.ResourceBundle;

public class MainAppController implements MapComponentInitializedListener {

    private Stage stage;
    public void setStage(Stage stage) { this.stage = stage; }
    private ResourceBundle resources;
    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        homeInit();
        editProfileInit();
        submitReportInit();
        submitQualityReportInit();
        viewReportInit();
        viewQReportInit();
    }

    /*
            ~ HOME ~
     */
    private GeocodingService geocodingService;


    private ObservableList<String> searchList = FXCollections.observableArrayList();

    @FXML
    private ComboBox<String> addressTextField;

    StringProperty address = new SimpleStringProperty();

    private GoogleMap map;
    @FXML
    private GoogleMapView mapView;
    @FXML // ResourceBundle that was given to the FXMLLoader
    private void homeInit() {
        mapView.addMapInializedListener(this);
        addressTextField.setItems(searchList);
        address.bind(addressTextField.getEditor().textProperty());
    }
    private void addMarker(WaterSourceReport report) {
        MarkerOptions opt = new MarkerOptions();
        Location l = report.getLocation();
        opt.position(new LatLong(l.getLatitude(), l.getLongitude()));
        opt.title("Water type: "+ report.getType().toString()
                + "\nWater quality: " + report.getQuality().toString()
                + "\nSubmitted by: " + report.getSubmitter().getUsername()
                + " on [" + report.getSubmissionDate().toString() + "]");

        Marker newMark = new Marker(opt);
        InfoWindowOptions infoWindowOptions = new InfoWindowOptions();
        infoWindowOptions.content("<h2>" + report.getType().toString() + "</h2>"
                + "Location: " + l.toString() + "<br>");
//        infoWindowOptions.disableAutoPan(true);

        InfoWindow infoWindow = new InfoWindow(infoWindowOptions);
        infoWindow.open(map, newMark);

        map.addMarker(newMark);
    }

    private void addMarker(QualityReport report) {
        MarkerOptions opt = new MarkerOptions();
        Location l = report.getLocation();
        opt.position(new LatLong(l.getLatitude(), l.getLongitude()));
        opt.title("Water condition: "+ report.getWaterCondition().toString()
                + "\nVirus PPM: " + report.getVirusPpm()
                + "\nContaminant PPM: " + report.getContaminantPpm()
                + "\nSubmitted by: " + report.getSubmitter().getUsername()
                + " on [" + report.getSubmissionDate().toString() + "]");
        map.addMarker(new Marker(opt));
    }
    @Override
    public void mapInitialized() {
        initializeMap();
    }

    private void initializeMap() {
        initializeMap(new LatLong(40, 40), 9);
    }
    private void initializeMap(LatLong center, int zoomLevel) {
        MapOptions mapOptions = new MapOptions();
        mapOptions.center(center)
                .zoom(zoomLevel)
                .streetViewControl(false)
                .mapType(MapTypeIdEnum.TERRAIN);
    
        map = mapView.createMap(mapOptions);
        for (WaterSourceReport report : Model.getInstance().getWaterSourceReports()) {
            addMarker(report);
        }
        for (QualityReport report : Model.getInstance().getQualityReports()) {
            addMarker(report);
        }
    
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
                searchList.clear();
                for (GeocodingResult result : results) {
                    searchList.add(result.getFormattedAddress());
                }
                addressTextField.show();
                addressTextField.getSelectionModel().select(0);
                latLong = new LatLong(results[0].getGeometry().getLocation().getLatitude(), results[0].getGeometry().getLocation().getLongitude());
            } else {
                searchList.clear();
                searchList.add(results[0].getFormattedAddress());
                addressTextField.getSelectionModel().select(0);
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
    private void onEditProfileKeyPressed(KeyEvent event)
    {
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
                    initializeMap(map.getCenter(), map.getZoom());
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
    private void onSubmitReportKeyPressed(KeyEvent event)
    {
        if(event.getCode() == KeyCode.ENTER) {
            onSubmitPressed();
        }
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
            WaterSourceReport report = model.createSourceReport(l, source, quality);
            viewReportInit();
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
            ~ SUBMIT QUALITY REPORT ~
     */
    @FXML
    private ChoiceBox<QualityReport.WaterCondition> conditionTypeChoiceBox;
    @FXML
    private TextField virusPpmField;
    @FXML
    private TextField contaminantPpmField;

    private void submitQualityReportInit() {
        conditionTypeChoiceBox.getItems().setAll(QualityReport.WaterCondition.values());
        virusPpmField.setText("");
        contaminantPpmField.setText("");
    }

    @FXML
    private void onSubmitQualityPressed() {
        Model model = Model.getInstance();
        QualityReport.WaterCondition waterCondition = conditionTypeChoiceBox.getValue();
        Double virusPpm, contaminantPpm;
        try {
            virusPpm = Double.parseDouble(virusPpmField.getText());
            contaminantPpm = Double.parseDouble(contaminantPpmField.getText());
        } catch (NumberFormatException e) {
            ControllerUtils.createErrorMessage(stage, "Submit Report Error", "Please enter a valid number");
            return;
        }


        if (waterCondition == null) {
            ControllerUtils.createErrorMessage(stage, "Submit Report Error", "Please select a Water Condition");
        } else if (virusPpm == null) {
            ControllerUtils.createErrorMessage(stage, "Submit Report Error", "Please enter Virus PPM");
        } else if (contaminantPpm == null) {
            ControllerUtils.createErrorMessage(stage, "Submit Report Error", "Please enter Contaminant PPM");
        } else {
            try {
                Location l = new Location(map.getCenter().getLatitude(), map.getCenter().getLongitude());
                QualityReport report = model.createQualityReport(l, waterCondition, virusPpm, contaminantPpm);
                viewQReportInit();
                ControllerUtils.createMessage(stage, "Submit Quality Report", "Success",
                        "Your water quality report has been added", Alert.AlertType.CONFIRMATION);
                addMarker(report);
            } catch (IllegalStateException e) {
                ControllerUtils.createErrorMessage(stage, "Submit Report Error", "Illegal Permissions");
            }
        }
    }

    @FXML
    private void onQualityReportCancelPressed() {
        submitQualityReportInit();
    }


    /*
            ~ VIEW REPORTS ~
     */

    @FXML
    private TableView<WaterSourceReport> SourceHistoryTable;
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
//    private TableColumn<WaterSourceReport, String> colReportID;
    private void viewReportInit() {
        SourceHistoryTable.setItems(getWaterSourceReports());
        colLocation.setCellValueFactory(new PropertyValueFactory<WaterSourceReport, String>("Location"));
        colQuality.setCellValueFactory(new PropertyValueFactory<WaterSourceReport, String>("Quality"));
        colSource.setCellValueFactory(new PropertyValueFactory<WaterSourceReport, String>("Type"));
        colDate.setCellValueFactory(new PropertyValueFactory<WaterSourceReport, String>("SubmissionDate"));
        colUser.setCellValueFactory(new PropertyValueFactory<WaterSourceReport, String>("Submitter"));
        //colReportID.setCellValueFactory(new PropertyValueFactory<WaterSourceReport, String>("reportNumber"));
        SourceHistoryTable.getColumns().setAll(colUser, colDate, colLocation, colSource, colQuality);
        SourceHistoryTable.setRowFactory( tv -> {
            TableRow<WaterSourceReport> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    WaterSourceReport rowData = row.getItem();
                    System.out.println("This is " + rowData.getLocation().toString());
                }
            });
            return row ;
        });
    }
    private ObservableList<WaterSourceReport> getWaterSourceReports() {
        return FXCollections.observableArrayList(Model.getInstance().getWaterSourceReports());
    }


    /*
            ~ VIEW QUALITY REPORTS ~
     */

    @FXML
    private TableView<QualityReport> QualityHistoryTable;
    @FXML
    private TableColumn<QualityReport, String> qColLocation;
    @FXML
    private TableColumn<QualityReport, String> qColVirPpm;
    @FXML
    private TableColumn<QualityReport, String> qColContPpm;
    @FXML
    private TableColumn<QualityReport, String> qColWaterCon;
    @FXML
    private TableColumn<QualityReport, String> qColDate;
    @FXML
    private TableColumn<QualityReport, String> qColUser;
    @FXML
//    private TableColumn<WaterSourceReport, String> colReportID;
    private void viewQReportInit() {
        QualityHistoryTable.setItems(getQualityReports());
        qColLocation.setCellValueFactory(new PropertyValueFactory<QualityReport, String>("Location"));
        qColWaterCon.setCellValueFactory(new PropertyValueFactory<QualityReport, String>("WaterCondition"));
        qColVirPpm.setCellValueFactory(new PropertyValueFactory<QualityReport, String>("VirusPpm"));
        qColContPpm.setCellValueFactory(new PropertyValueFactory<QualityReport, String>("ContaminantPpm"));
        qColDate.setCellValueFactory(new PropertyValueFactory<QualityReport, String>("SubmissionDate"));
        qColUser.setCellValueFactory(new PropertyValueFactory<QualityReport, String>("Submitter"));
        //colReportID.setCellValueFactory(new PropertyValueFactory<WaterSourceReport, String>("reportNumber"));
        QualityHistoryTable.getColumns().setAll(qColUser, qColDate, qColLocation, qColWaterCon, qColVirPpm, qColContPpm);
    }
    private ObservableList<QualityReport> getQualityReports() {
        return FXCollections.observableArrayList(Model.getInstance().getQualityReports());
    }

    
    /*
            ~ MENU BAR ~
     */
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
    @FXML
    private void onResetPressed() {
        initializeMap();
    }
}