package ui;

import javafx.scene.input.KeyCode;
import model.AccountType;
import org.junit.Assert;
import org.junit.Test;
import static org.loadui.testfx.Assertions.assertNodeExists;
import static org.loadui.testfx.Assertions.verifyThat;
import static org.loadui.testfx.controls.Commons.hasText;
import org.loadui.testfx.exceptions.NoNodesFoundException;
import org.loadui.testfx.exceptions.NoNodesVisibleException;

public class MainAppTest extends WaterReportingGUITest
{
    @Test
    public void testInvalidAddSourceReport() {
        login("admin");
    
        click("#mapMenu");
        move("#addMenu");
        click("#addSourceMenu");
        
        //both choice boxes empty
        click("#submit");
        assertNodeExists("Submit Report Error");
        click("OK");
        
        //one choice box empty
        click("#sourceTypeChoiceBox");
        click("BOTTLED");
        click("#submit");
        assertNodeExists("Submit Report Error");
        click("OK");
        
        click("#cancel");
        logout();
    }
    
    @Test
    public void testAddSourceReport() {
        login("admin");
        int startSourceReports = model.getWaterSourceReports().size();

        sleep(2000);
    
        rightClick("#mapView");
        click("Source Report");
        click("#cancel");
        Assert.assertEquals(model.getWaterSourceReports().size(), startSourceReports);
        
        rightClick("#mapView");
        click("Source Report");
        addSourceReport("BOTTLED", "POTABLE", true);
        Assert.assertEquals(model.getWaterSourceReports().size(), startSourceReports + 1);
    
        click("#mapMenu");
        move("#addMenu");
        click("#addSourceMenu");
        addSourceReport("LAKE", "TREATABLE_MUDDY", false);
        Assert.assertEquals(model.getWaterSourceReports().size(), startSourceReports + 2);
        
        resetReports();
        logout();
    }
    
    private void addSourceReport(String sourceType, String qualityType, boolean mouse) {
        click("#sourceTypeChoiceBox");
        click(sourceType);
        click("#qualityTypeChoiceBox");
        click(qualityType);
        if(mouse) {
            click("#submit");
        } else {
            press(KeyCode.ENTER);
        }
        click("OK");
    }
    
    @Test
    public void testInvalidAddQualityReport() {
        login("admin");
    
        click("#mapMenu");
        move("#addMenu");
        click("#addQualityMenu");
        
        //all fields + combo box empty
        click("#submit");
        assertNodeExists("Submit Report Error");
        click("OK");
        
        //all fields empty
        click("#conditionTypeChoiceBox");
        click("SAFE");
        click("#submit");
        assertNodeExists("Submit Report Error");
        click("OK");
        
        //one field empty
        click("#virusPpmField");
        type("12");
        click("#submit");
        assertNodeExists("Submit Report Error");
        click("OK");
        
        //invalid field
        click("#contaminantPpmField");
        type("abc");
        click("#submit");
        assertNodeExists("Submit Report Error");
        click("OK");
        
        click("#cancel");
        logout();
    }
    
    @Test
    public void testAddQualityReport() {
        login("admin");
        int startQualityReports = model.getQualityReports().size();
    
        sleep(2000);
    
        rightClick("#mapView");
        click("Quality Report");
        click("#cancel");
        Assert.assertEquals(model.getQualityReports().size(), startQualityReports);
    
        rightClick("#mapView");
        click("Quality Report");
        addQualityReport("SAFE", "110", "240.34", true);
        Assert.assertEquals(model.getQualityReports().size(), startQualityReports + 1);
    
        click("#mapMenu");
        move("#addMenu");
        click("#addQualityMenu");
        addQualityReport("UNSAFE", "17.11112", "44", false);
        Assert.assertEquals(model.getQualityReports().size(), startQualityReports + 2);
        
        resetReports();
        logout();
    }
    
    private void addQualityReport(String conditionType, String virusPPM, String contaminantPPM, boolean mouse) {
        click("#conditionTypeChoiceBox");
        click(conditionType);
        click("#virusPpmField");
        type(virusPPM);
        verifyThat("#virusPpmField", hasText(virusPPM));
        if(mouse) {
            click("#contaminantPpmField");
            type(contaminantPPM);
            verifyThat("#contaminantPpmField", hasText(contaminantPPM));
            click("#submit");
        } else {
            press(KeyCode.TAB);
            type(contaminantPPM);
            press(KeyCode.ENTER);
        }
        click("OK");
    }
    
    @Test
    public void testPermissions() {
        login("user");
        testPermissions(AccountType.User);
        
        login("worker");
        testPermissions(AccountType.Worker);
        
        login("manager");
        testPermissions(AccountType.Manager);
        
        login("admin");
        testPermissions(AccountType.Admin);
    }
    
    private void testPermissions(AccountType authorization) {
        boolean addQualityAuthorized = authorization.isAuthorized(AccountType.Worker);
        boolean qualityHistoryAuthorized = authorization.isAuthorized(AccountType.Manager);
        
        //add quality report via menu
        click("#mapMenu");
        move("#addMenu");
        Assert.assertTrue(addQualityAuthorized == nodeVisible("#addQualityMenu"));
        click("#mapMenu");
        
        //add quality report via right click
        rightClick("#mapView");
        Assert.assertTrue(addQualityAuthorized == nodeExists("Quality Report"));
        click("Cancel");
    
        //view quality report history
        click("#viewQualityTab");
        Assert.assertTrue(qualityHistoryAuthorized == nodeVisible("#QualityHistoryTable"));
        
        //view source report history
        click("#viewSourceTab");
        Assert.assertTrue(nodeVisible("#SourceHistoryTable"));
        
        logout();
    }
    
    private boolean nodeExists(String node) {
        try {
            find(node);
            return true;
        } catch(NoNodesFoundException e) {
            return false;
        }
    }
    
    private boolean nodeVisible(String node) {
        try {
            find(node);
            return true;
        } catch(NoNodesVisibleException e) {
            return false;
        }
    }
    
    private void resetReports() {
        model.getWaterSourceReports().clear();
        model.getQualityReports().clear();
    }
}
