package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.AttendancePage;
import pages.BasePage;
import pages.LoginPage;
import utils.Config;

/**
 * PunchInTest — Punch-In Toast & Confirmation Verification.
 * Automates Punch-In action and captures toast evidence screenshot.
 */
public class PunchInTest extends BasePage {

    @BeforeMethod
    public void loginFirst() {
        String email    = Config.getValidEmail();
        String password = Config.getValidPassword();

        LoginPage loginPage = new LoginPage(driver, wait);
        loginPage.navigateToLogin();
        loginPage.login(email, password);

        boolean loggedIn = loginPage.isDashboardReached();
        if (!loggedIn) {
            throw new RuntimeException("Pre-condition failed: Authentication unsuccessful for email: " + email);
        }
    }

    @Test(
        description = "Verifies Punch-In trigger displays toast confirmation and saves screenshot evidence",
        groups      = { "attendance", "smoke" }
    )
    public void testPunchInToast() {
        AttendancePage attendancePage = new AttendancePage(driver, wait);
        attendancePage.navigateToAttendance();
        attendancePage.clickPunchIn();

        String actualToastText = attendancePage.getToastText();
        Assert.assertFalse(
            actualToastText.isEmpty(),
            "Punch-In notification toast was empty or not displayed."
        );

        String screenshotPath = takeScreenshot("punch_in_toast");
        Assert.assertNotNull(
            screenshotPath,
            "Failed to save screenshot evidence."
        );

        System.out.println("[SUCCESS] Verified Punch-In toast ('" + actualToastText + "') and captured screenshot: " + screenshotPath);
    }
}
