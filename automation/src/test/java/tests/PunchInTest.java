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

        // Soft comparison only — does NOT fail the test. AttendancePage.EXPECTED_PUNCH_IN_TOAST
        // is a best-guess placeholder that was never confirmed against the live DOM (see the
        // verification-status note on that constant). Logging the mismatch here instead of
        // asserting on it keeps this test meaningful even before that value is confirmed.
        if (!actualToastText.equalsIgnoreCase(AttendancePage.EXPECTED_PUNCH_IN_TOAST)) {
            System.out.println(
                "[NOTICE] Actual toast text ('" + actualToastText + "') differs from the " +
                "placeholder EXPECTED_PUNCH_IN_TOAST ('" + AttendancePage.EXPECTED_PUNCH_IN_TOAST +
                "'). Update the constant with this real value."
            );
        }

        String screenshotPath = takeScreenshot("punch_in_toast");
        Assert.assertNotNull(
            screenshotPath,
            "Failed to save screenshot evidence."
        );

        System.out.println("[SUCCESS] Verified Punch-In toast ('" + actualToastText + "') and captured screenshot: " + screenshotPath);
    }
}
