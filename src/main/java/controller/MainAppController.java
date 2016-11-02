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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.*;
import model.WaterSourceReport.QualityType;
import model.WaterSourceReport.SourceType;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainAppController extends Controller implements MapComponentInitializedListener
{
    private ResourceBundle resources;
    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        homeInit();
        menuInit();
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
        map.addMarker(new Marker(opt));
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
            ~ MENU BAR ~
     */
    @FXML
    MenuItem addQualityReportMenuItem;
    
    private void menuInit() {
        boolean authorized = Model.CURRENT_USER.getAccountType().isAuthorized(AccountType.Worker);
        addQualityReportMenuItem.setVisible(authorized);
    }
    
    @FXML
    private void onEditPressed() {
        EventHandler<WindowEvent> handler = event -> {
            initializeMap(map.getCenter(), map.getZoom());
            menuInit();
        };
        createModalWindow("/fxml/editUser.fxml", "Edit Account", handler);
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
        stage.setScene(new Scene(root));
        stage.show();

        WelcomeController controller = loader.getController();
        controller.setStage(stage);
    }
    
    @FXML
    private void onAddSourceReportPressed() {
        EventHandler<WindowEvent> handler = event -> initializeMap(map.getCenter(), map.getZoom());
        SourceReportController controller = (SourceReportController) createModalWindow("/fxml/sourceReport.fxml", "Add Source Report", handler);
        controller.setReportLocation(new Location(map.getCenter().getLatitude(), map.getCenter().getLongitude()));
    }
    
    @FXML
    private void onAddQualityReportPressed() {
        if(Model.CURRENT_USER.getAccountType().isAuthorized(AccountType.Worker)) {
            EventHandler<WindowEvent> handler = event -> initializeMap(map.getCenter(), map.getZoom());
            QualityReportController controller = (QualityReportController) createModalWindow("/fxml/qualityReport.fxml", "Add Quality Report", handler);
            controller.setReportLocation(new Location(map.getCenter().getLatitude(), map.getCenter().getLongitude()));
        }
    }
    
    @FXML
    private void onResetPressed() { initializeMap(); }
    
    private Controller createModalWindow(String path, String title, EventHandler<WindowEvent> onClosed) {
        Controller controller = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(path));
            Parent root = loader.load();
            Stage newStage = new Stage();
    
            newStage.setTitle(title);
            newStage.setOnHiding(onClosed);
            newStage.setScene(new Scene(root));
            newStage.initModality(Modality.WINDOW_MODAL);
            newStage.initOwner(stage.getScene().getWindow());
            newStage.show();
        
            controller = loader.getController();
            controller.setStage(newStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return controller;
    }
}