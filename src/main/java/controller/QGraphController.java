package controller;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import model.QualityReport;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Soo Hyung Park on 11/2/16.
 */



public class QGraphController extends Controller {

    @FXML
    private LineChart<Integer, Double> qualityGraph;

    @FXML
    private void initialize() {

    }

    @FXML
    public void QualityGraphInit(List<QualityReport> list) {
//        Location myLocation = new Location(map.getCenter().getLatitude(), map.getCenter().getLongitude());
//
//        clearQualityGraph();
        LineChart.Series<Integer, Double> virusPpmSeries = new LineChart.Series<Integer, Double>();
        virusPpmSeries.setName("Virus PPM");
        LineChart.Series<Integer, Double> contaminantPpmSeries = new LineChart.Series<Integer, Double>();
        contaminantPpmSeries.setName("Contaminant PPM");

//        for (QualityReport report : Model.getInstance().getQualityReports()) {
//            if (myLocation.equals(report.getLocation())) {
//                String date = report.getSubmissionDate().toString().substring(4, 16)
//                        + " " + report.getSubmissionDate().toString().substring(24, 28);
//                Double virusPpm = report.getVirusPpm();
//                Double contaminantPpm = report.getContaminantPpm();
//
//                virusPpmSeries.getData().add(new LineChart.Data<>(date, virusPpm));
//                contaminantPpmSeries.getData().add(new LineChart.Data<>(date, contaminantPpm));
//            }
//        }
        for (QualityReport q : list) {
            Double virusPpm = q.getVirusPpm();
            Double contaminantPpm = q.getContaminantPpm();
            int date = q.getSubmissionDate().getDate();
            System.out.print(virusPpm + ", " + contaminantPpm + ", " + date);

            virusPpmSeries.getData().add(new LineChart.Data<>(date, virusPpm));
            contaminantPpmSeries.getData().add(new LineChart.Data<>(date, contaminantPpm));
        }
        qualityGraph.getData().addAll(virusPpmSeries, contaminantPpmSeries);
    }

}
