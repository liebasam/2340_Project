package model;

import org.junit.Assert;
import org.junit.Test;
import java.util.Set;

public class HideSourceReportsTest
{
    private final Location somewhere = new Location(33.7756178, -84.38392538);
    private final Location gatech = new Location(33.7756178, -84.396285);
    
    Model model;
    
    @SuppressWarnings("unchecked")
    @Test
    public void testHideNearbySourceReports() {
        model = Model.getTestInstance();
        String eric_user = "bird";
        String eric_pass = "up";
        model.createAccount(eric_user, eric_pass, AccountType.Manager);
        model.login(eric_user, eric_pass);
        model.createSourceReport(gatech.getLatitude(), gatech.getLongitude(),
                WaterSourceReport.SourceType.BOTTLED, WaterSourceReport.QualityType.POTABLE);
        model.createSourceReport(somewhere.getLatitude(), somewhere.getLongitude(), WaterSourceReport.SourceType
                .BOTTLED, WaterSourceReport.QualityType.WASTE);
        model.hideSourceReportsNear(somewhere);
        Set<WaterSourceReport> rpt_set = model.getSourceReportsNear(somewhere);
        for (WaterSourceReport e : rpt_set) {
            Assert.assertTrue(e.isHidden());
        }
    }
}
