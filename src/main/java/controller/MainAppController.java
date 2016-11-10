package controller;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.service.geocoding.GeocoderStatus;
import com.lynden.gmapsfx.service.geocoding.GeocodingResult;
import com.lynden.gmapsfx.service.geocoding.GeocodingService;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.InfoWindowOptions;
import com.lynden.gmapsfx.javascript.object.InfoWindow;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableColumn;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Model;
import model.AccountType;
import model.WaterSourceReport;
import model.QualityReport;
import model.Location;
import netscape.javascript.JSObject;
import java.util.List;
import java.util.Set;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainAppController extends Controller implements MapComponentInitializedListener {
    
    private Stage stage;
    public void setStage(Stage stage) { this.stage = stage; }
    private ResourceBundle resources;
    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        homeInit();
        menuInit();
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

    private StringProperty address = new SimpleStringProperty();

    private GoogleMap map;
    @FXML
    private GoogleMapView mapView;
    @FXML
    Tab viewQualityTab;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private void homeInit() {
        mapView.addMapInializedListener(this);
        addressTextField.setItems(searchList);
        address.bind(addressTextField.getEditor().textProperty());
        viewQualityTab.setDisable(!Model.getInstance().getCurrentUser().getAccountType().isAuthorized(AccountType.Manager));
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
        //infoWindowOptions.disableAutoPan(true);
        map.addUIEventHandler(newMark,
                UIEventType.click,
                (JSObject obj) -> {
                    //InfoWindowOptions infoWindowOptions = new InfoWindowOptions();
                    //infoWindowOptions.content(l.getDescription());
                    Alert reportEdit = new Alert(Alert.AlertType.CONFIRMATION, "Would you like to edit this source report?",
                            new ButtonType("Edit"),
                            new ButtonType("Add a new report at this location"),
                            new ButtonType("Delete"),
                            new ButtonType("Cancel", ButtonBar.ButtonData.BACK_PREVIOUS));
                    reportEdit.show();

                    InfoWindow window = new InfoWindow(infoWindowOptions);
                    window.open(map, newMark);
                });

        //InfoWindow infoWindow = new InfoWindow(infoWindowOptions);
        //infoWindow.open(map, newMark);
        
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
        Marker newMark = new Marker(opt);
        map.addUIEventHandler(newMark,
                UIEventType.click,
                (JSObject obj) -> {
                    Alert reportEdit;
                    if(Model.getInstance().getCurrentUser().getAccountType().isAuthorized(AccountType.Manager)) {
                        reportEdit = new Alert(Alert.AlertType.CONFIRMATION, "Would you like to edit this quality report?",
                                new ButtonType("Edit"),
                                new ButtonType("Add a new report at this location"),
                                new ButtonType("Delete"),
                                new ButtonType("See History"),
                                new ButtonType("Cancel", ButtonBar.ButtonData.BACK_PREVIOUS));
                    } else {
                        reportEdit = new Alert(Alert.AlertType.CONFIRMATION, "Would you like to edit this quality report?",
                                new ButtonType("Edit"),
                                new ButtonType("Add a new report at this location"),
                                new ButtonType("Delete"),
                                new ButtonType("Cancel", ButtonBar.ButtonData.BACK_PREVIOUS));
                    }

                    reportEdit.showAndWait();
                    if (Model.getInstance().getCurrentUser().getAccountType().isAuthorized(AccountType.Manager) && "See History".equals(reportEdit.getResult().getText())) {
                        Set<QualityReport> qlists = Model.getInstance().getQualityReports();
                        List<QualityReport> neighbors = qlists.stream()
                                .filter(e ->
                                        e.getLocation().distanceTo(report.getLocation()) <= 2.0)
                                .collect(Collectors.toList());
                        QGraphController controller;
                        controller = (QGraphController) createModalWindow("/fxml/GraphView.fxml", "Graph");
                        controller.QualityGraphInit(neighbors);
                    }
                });
        
        map.addMarker(newMark);
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
        Model.getInstance().getWaterSourceReports().stream()
                .filter(report -> !report.isHidden())
                .forEachOrdered(this::addMarker);
        Model.getInstance().getQualityReports().stream()
                .filter(report -> !report.isHidden())
                .forEachOrdered(this::addMarker);
    
        geocodingService = new GeocodingService();
    }

    @FXML
    public void onAddressSearchButtonClicked() {
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
    //private TableColumn<WaterSourceReport, String> colReportID;
    private void viewReportInit() {
        SourceHistoryTable.setItems(getWaterSourceReports());
        colLocation.setCellValueFactory(new PropertyValueFactory<>("Location"));
        colQuality.setCellValueFactory(new PropertyValueFactory<>("Quality"));
        colSource.setCellValueFactory(new PropertyValueFactory<>("Type"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("SubmissionDate"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("Submitter"));
        //colReportID.setCellValueFactory(new PropertyValueFactory<>("reportNumber"));
        SourceHistoryTable.getColumns().setAll(colUser, colDate, colLocation, colSource, colQuality);
        SourceHistoryTable.setRowFactory( tv -> {
            TableRow<WaterSourceReport> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if ((event.getClickCount() == 2) && (! row.isEmpty()) ) {
                    WaterSourceReport rowData = row.getItem();
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
    //private TableColumn<WaterSourceReport, String> colReportID;
    private void viewQReportInit() {
        QualityHistoryTable.setItems(getQualityReports());
        qColLocation.setCellValueFactory(new PropertyValueFactory<>("Location"));
        qColWaterCon.setCellValueFactory(new PropertyValueFactory<>("WaterCondition"));
        qColVirPpm.setCellValueFactory(new PropertyValueFactory<>("VirusPpm"));
        qColContPpm.setCellValueFactory(new PropertyValueFactory<>("ContaminantPpm"));
        qColDate.setCellValueFactory(new PropertyValueFactory<>("SubmissionDate"));
        qColUser.setCellValueFactory(new PropertyValueFactory<>("Submitter"));
        //colReportID.setCellValueFactory(new PropertyValueFactory<>("reportNumber"));
        QualityHistoryTable.getColumns().setAll(qColUser, qColDate, qColLocation, qColWaterCon, qColVirPpm, qColContPpm);
    }
    private ObservableList<QualityReport> getQualityReports() {
        return FXCollections.observableArrayList(Model.getInstance().getQualityReports());
    }

    
    /*
            ~ MENU BAR ~
     */
    @FXML
    MenuItem addQualityReportMenuItem;

    private void menuInit() {
        boolean authorized = Model.getInstance().getCurrentUser().getAccountType().isAuthorized(AccountType.Worker);
        addQualityReportMenuItem.setVisible(authorized);
    }

    @FXML
    private void onEditPressed() {
        EventHandler<WindowEvent> handler = event -> {
            initializeMap(map.getCenter(), map.getZoom());
            homeInit();
            menuInit();
        };
        createModalWindow("/fxml/editUser.fxml", "Edit Account", handler);
    }

    @FXML
    private void onExitPressed() {
        stage.close();
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
        EventHandler<WindowEvent> handler = event -> {
            initializeMap(map.getCenter(), map.getZoom());
            viewReportInit();
        };
        SourceReportController controller = (SourceReportController) createModalWindow("/fxml/sourceReport.fxml", "Add Source Report", handler);
        controller.setReportLocation(new Location(map.getCenter().getLatitude(), map.getCenter().getLongitude()));
    }

    @FXML
    private void onAddQualityReportPressed() {
        if(Model.getInstance().getCurrentUser().getAccountType().isAuthorized(AccountType.Worker)) {
            EventHandler<WindowEvent> handler = event -> {
                initializeMap(map.getCenter(), map.getZoom());
                viewQReportInit();
            };
            QualityReportController controller = (QualityReportController) createModalWindow("/fxml/qualityReport.fxml", "Add Quality Report", handler);
            controller.setReportLocation(new Location(map.getCenter().getLatitude(), map.getCenter().getLongitude()));
        }
    }

    @FXML
    private void onResetPressed() { initializeMap(); }

    private Controller createModalWindow(String path, String title) {
        return createModalWindow(path, title, event -> {});
    }

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