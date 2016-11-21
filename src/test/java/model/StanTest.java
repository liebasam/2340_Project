package model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/*
 * @author Soo Hyung Park
 */

import java.util.Set;
/**
 * @author Soo Hyung Park
 */
public class StanTest {
    private User userObj1;
    private final Location gatech = new Location(33.7756178, -84.396285);
    private final Location somewhere = new Location(33.7756178, -84.38392538);



    private Model model;


    @Before
    public void setup() {
        model = Model.getTestInstance();
        String eric_user = "bird";
        String eric_pass = "up";
        userObj1 = model.createAccount(eric_user, eric_pass, AccountType.Manager);
        model.login(eric_user, eric_pass);
        model.createSourceReport(gatech.getLatitude(), gatech.getLongitude(),
                WaterSourceReport.SourceType.BOTTLED, WaterSourceReport.QualityType.POTABLE);
    }

    @Test
    public void submitSourceReportTest() {
        WaterSourceReport waterbottle = new WaterSourceReport(userObj1, gatech, WaterSourceReport.SourceType.BOTTLED,
                WaterSourceReport.QualityType.POTABLE);

        Assert.assertTrue(containsIgnoreDateIDNumber(model.getWaterSourceReports(), waterbottle));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNearbySourceReports() {
        WaterSourceReport waterbottle = new WaterSourceReport(userObj1, gatech, WaterSourceReport.SourceType.BOTTLED,
                WaterSourceReport.QualityType.POTABLE);
        Assert.assertTrue(containsIgnoreDateIDNumber(model.getSourceReportsNear(somewhere), waterbottle));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testHideNearbySourceReports() {
        model.createSourceReport(somewhere.getLatitude(), somewhere.getLongitude(), WaterSourceReport.SourceType
                .BOTTLED, WaterSourceReport.QualityType.WASTE);
        model.hideSourceReportsNear(somewhere);
        Set<WaterSourceReport> rpt_set = model.getSourceReportsNear(somewhere);
        for (WaterSourceReport e : rpt_set) {
            Assert.assertTrue(e.isHidden());
        }
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