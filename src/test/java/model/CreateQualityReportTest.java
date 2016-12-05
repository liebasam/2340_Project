package model;

import java.util.List;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CreateQualityReportTest
{
    private final String username = "YoungClaptoid";
    private final String username1 = "MisterProper";
    private final String password = "resurrection";

    private final Location loc = new Location(30.00, 50.00);
    private final QualityReport.WaterCondition WC = QualityReport.WaterCondition.SAFE;
    private final double virusPPM = 100.00;
    private final double contaminantPPM = 200.00;

    private Model model;

    @Before
    public void setup() {
        model = Model.getTestInstance();
        model.createAccount(username, password, AccountType.Manager);
        model.createAccount(username1, password, AccountType.User);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateQualityReportUser() {
        model.login(username1, password);
        model.createQualityReport(loc, WC, virusPPM, contaminantPPM);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateQualityReportNullUser() {
        model.logout();
        model.createQualityReport(loc, WC, virusPPM, contaminantPPM);
    }

    @Test
    public void testCreateQualityReport() {
        model.login(username, password);
        QualityReport QR = new QualityReport(model.getCurrentUser(),
                new Location(30.00, 50.00), WC, 100.00, 200.00);
        model.createQualityReport(loc, WC, virusPPM, contaminantPPM);
        List<QualityReport> reports = new ArrayList<>();
        reports.addAll(model.getQualityReports());
        assertEquals(QR.getLocation(), reports.get(0).getLocation());
        assertEquals(QR.getWaterCondition(), reports.get(0).getWaterCondition());
        assertEquals(QR.getVirusPpm(), reports.get(0).getVirusPpm());
        assertEquals(QR.getContaminantPpm(), reports.get(0).getContaminantPpm());
    }
}
