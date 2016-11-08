package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import model.Location;
import model.Model;
import model.WaterSourceReport;

class SourceReportController extends Controller
{
    private Location reportLocation = null;
    
    @FXML
    private ChoiceBox<WaterSourceReport.SourceType> sourceTypeChoiceBox;
    @FXML
    private ChoiceBox<WaterSourceReport.QualityType> qualityTypeChoiceBox;
    
    public void setReportLocation(Location location) { reportLocation = location; }
    
    @FXML
    private void initialize() {
        sourceTypeChoiceBox.getItems().setAll(WaterSourceReport.SourceType.values());
        qualityTypeChoiceBox.getItems().setAll(WaterSourceReport.QualityType.values());
    }
    
    @FXML
    private void onKeyPressed(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
            onSubmitPressed();
        }
    }
    
    @FXML
    private void onSubmitPressed() {
        Model model = Model.getInstance();
        WaterSourceReport.SourceType source = sourceTypeChoiceBox.getValue();
        WaterSourceReport.QualityType quality = qualityTypeChoiceBox.getValue();
        
        if(source == null) {
            ControllerUtils.createErrorMessage(stage, "Submit Report Error", "Please select a source type");
        } else if(quality == null) {
            ControllerUtils.createErrorMessage(stage, "Submit Report Error", "Please select a quality type");
        } else {
            model.hideSourceReportsNear(reportLocation);
            model.createSourceReport(reportLocation, source, quality);
            Alert message = ControllerUtils.createMessage(stage, "Submit Report", "Success",
                    "Your water source report has been added", Alert.AlertType.INFORMATION);
            message.setOnCloseRequest(event -> stage.close());
        }
    }
    
    @FXML
    private void onCancelPressed() {
        stage.close();
    }
}
