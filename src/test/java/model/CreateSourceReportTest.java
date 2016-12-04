package model;

import org.junit.Assert;
import org.junit.Test;
/*
 * @author Soo Hyung Park
 */

import java.util.Set;
/**
 * @author Soo Hyung Park
 */
public class CreateSourceReportTest
{
    private final Location gatech = new Location(33.7756178, -84.396285);
    private final Location somewhere = new Location(33.7756178, -84.38392538);
    
    private Model model;

    @Test
    public void submitSourceReportTest() {
        model = Model.getTestInstance();
        String eric_user = "bird";
        String eric_pass = "up";
        User userObj1 = model.createAccount(eric_user, eric_pass, AccountType.Manager);
        model.login(eric_user, eric_pass);
        model.createSourceReport(gatech.getLatitude(), gatech.getLongitude(),
                WaterSourceReport.SourceType.BOTTLED, WaterSourceReport.QualityType.POTABLE);
        WaterSourceReport waterbottle = new WaterSourceReport(userObj1, gatech, WaterSourceReport.SourceType.BOTTLED,
                WaterSourceReport.QualityType.POTABLE);

        Assert.assertTrue(containsIgnoreDateIDNumber(model.getWaterSourceReports(), waterbottle));

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