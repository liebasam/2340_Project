package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import model.Location;


public class QualityReportController extends Controller
{
    private Location reportLocation = null;
    
    public void setReportLocation(Location location) { reportLocation = location; }
}
