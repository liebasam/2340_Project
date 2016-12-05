package model;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class NearbyQualityReportsTest
{
    private Model model;
    
    @SuppressWarnings("unchecked")
    @Test
    public void testNearbyQualityReports() {
        model = Model.getTestInstance();
        final String eric_user = "bird";
        final String eric_pass = "up";
        User userObj1 = model.createAccount(eric_user, eric_pass, AccountType.Manager);
        final Location gatech = new Location(33.7756178, -84.396285);
        final Location somewhere = new Location(33.77562, -84.38392538);
        
        model.login(eric_user, eric_pass);
        model.createQualityReport(gatech, QualityReport.WaterCondition.TREATABLE, 100.4, 56.4);
        
        QualityReport reportSameLoc = new QualityReport(userObj1, gatech, QualityReport.WaterCondition.SAFE, 23.44, 56.8);
        model.createQualityReport(reportSameLoc.getLocation(), reportSameLoc.getWaterCondition(), reportSameLoc.getVirusPpm(), reportSameLoc.getContaminantPpm());
        Assert.assertTrue(containsIgnoreDateIDNumber(model.getQualityReportsNear(somewhere), reportSameLoc));
        
        QualityReport reportNearLoc = new QualityReport(userObj1, somewhere, QualityReport.WaterCondition.UNSAFE, 238.94, 820.3);
        model.createQualityReport(reportNearLoc.getLocation(), reportNearLoc.getWaterCondition(), reportNearLoc.getVirusPpm(), reportNearLoc.getContaminantPpm());
        Assert.assertTrue(containsIgnoreDateIDNumber(model.getQualityReportsNear(somewhere), reportNearLoc));
    }
    
    private boolean containsIgnoreDateIDNumber(Set<QualityReport> set, QualityReport find) {
        for (QualityReport e : set) {
            if ((e.getLocation().equals(find.getLocation())) && (e.getContaminantPpm().equals(find.getContaminantPpm()))
                    && e.getWaterCondition().equals(find.getWaterCondition()) && e.getSubmitter().equals(find.getSubmitter())) {
                return true;
            }
        }
        return false;
    }
}
