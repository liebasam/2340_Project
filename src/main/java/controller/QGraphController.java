package controller;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import model.QualityReport;

import java.util.Collection;

public class QGraphController extends Controller
{
    @FXML
    private LineChart<String, Double> qualityGraph;
    
    @FXML
    public void QualityGraphInit(Collection<QualityReport> qualityReports)
    {
        LineChart.Series<String, Double> virusPpmSeries = new LineChart.Series<>();
        virusPpmSeries.setName("Virus PPM");
        LineChart.Series<String, Double> contaminantPpmSeries = new LineChart.Series<>();
        contaminantPpmSeries.setName("Contaminant PPM");
        for (QualityReport report : qualityReports) {
            String date = report.getSubmissionDate().toString().substring(4, 16)
                    + " " + report.getSubmissionDate().toString().substring(24, 28);
            Double virusPpm = report.getVirusPpm();
            Double contaminantPpm = report.getContaminantPpm();
            
            virusPpmSeries.getData().add(new LineChart.Data<>(date, virusPpm));
            contaminantPpmSeries.getData().add(new LineChart.Data<>(date, contaminantPpm));
        }
        
        qualityGraph.getData().addAll(virusPpmSeries, contaminantPpmSeries);
    }
}