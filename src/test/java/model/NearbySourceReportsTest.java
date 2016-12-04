package model;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class NearbySourceReportsTest
{
    @SuppressWarnings("unchecked")
    @Test
    public void testNearbySourceReports() {
        Model model = Model.getTestInstance();
        final String eric_user = "bird";
        final String eric_pass = "up";
        final Location gatech = new Location(33.7756178, -84.396285);
        final Location somewhere = new Location(33.7756178, -84.38392538);
        
        User userObj1 = model.createAccount(eric_user, eric_pass, AccountType.Manager);
        model.login(eric_user, eric_pass);
        model.createSourceReport(gatech.getLatitude(), gatech.getLongitude(),
                WaterSourceReport.SourceType.BOTTLED, WaterSourceReport.QualityType.POTABLE);
        WaterSourceReport waterbottle = new WaterSourceReport(userObj1, gatech, WaterSourceReport.SourceType.BOTTLED,
                WaterSourceReport.QualityType.POTABLE);
        Assert.assertTrue(containsIgnoreDateIDNumber(model.getSourceReportsNear(somewhere), waterbottle));
    }
    
    private boolean containsIgnoreDateIDNumber(Set<WaterSourceReport> set, WaterSourceReport find) {
        for (WaterSourceReport e : set) {
            if ((e.getLocation().equals(find.getLocation())) && (e.getQuality().equals(find.getQuality())) && (e
                    .getSubmitter().equals(find.getSubmitter()))) {
                return true;
            }
        }
        return false;
    }
}
