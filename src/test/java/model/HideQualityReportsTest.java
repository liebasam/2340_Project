package model;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class HideQualityReportsTest
{
    private final Location somewhere = new Location(33.7756178, -84.38392538);
    private final Location gatech = new Location(33.7756178, -84.396285);
    
    Model model;
    
    @SuppressWarnings("unchecked")
    @Test
    public void testHideNearbyQualityReports() {
        model = Model.getTestInstance();
        String eric_user = "bird";
        String eric_pass = "up";
        model.createAccount(eric_user, eric_pass, AccountType.Manager);
        model.login(eric_user, eric_pass);
        model.createQualityReport(gatech.getLatitude(), gatech.getLongitude(), QualityReport.WaterCondition.TREATABLE, 45.8, 145.0);
        model.createQualityReport(somewhere.getLatitude(), somewhere.getLongitude(), QualityReport.WaterCondition.SAFE, 122.0, 349.2);
        model.hideSourceReportsNear(somewhere);
        Set<WaterSourceReport> rpt_set = model.getSourceReportsNear(somewhere);
        for (WaterSourceReport e : rpt_set) {
            Assert.assertTrue(e.isHidden());
        }
    }
}
