package controller;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import model.QualityReport;
import java.util.List;

/**
 * Created by Soo Hyung Park on 11/2/16.
 */
public class QGraphController extends Controller
{
    @FXML
    private LineChart<String, Double> qualityGraph;
    
    @FXML
    public void QualityGraphInit(List<QualityReport> list)
    {
        LineChart.Series<String, Double> virusPpmSeries = new LineChart.Series<String, Double>();
        virusPpmSeries.setName("Virus PPM");
        LineChart.Series<String, Double> contaminantPpmSeries = new LineChart.Series<String, Double>();
        contaminantPpmSeries.setName("Contaminant PPM");
        
        for (QualityReport report : list) {
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