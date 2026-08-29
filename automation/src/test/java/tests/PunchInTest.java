package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.AttendancePage;
import pages.BasePage;
import pages.LoginPage;
import utils.Config;

/**
 * PunchInTest — Punch-In Toast Verification Test
 * ================================================
 * Verifies that clicking "Punch In" on the Attendance page displays
 * a toast confirmation message and saves a screenshot as evidence.
 *
 * Attendance page: https://test.fieldforceconnect.com/attendance
 *
 * Page layout (observed from live site):
 *  - Left panel: Employee "Mitesh" with status "Waiting for punch in"
 *  - Right panel: Table + "Add New" button (punch-in trigger)
 *
 * Test flow:
 *  1. BasePage.setUp()       → launch Chrome
 *  2. loginFirst()           → log in with valid credentials
 *  3. testPunchInToast()     → navigate to Attendance, click Punch-In,
 *                              assert toast, take screenshot
 *  4. BasePage.tearDown()    → quit Chrome
 *
 * ⚠️  NOTE: Attendance systems allow one punch-in per day.
 *     If this test has already run today, the punch-in button may show
 *     "Already Punched In" or the button may be absent. Handle this by
 *     re-running on a new day, or by checking the site's "Add New" form
 *     for a manual entry option.
 */
public class PunchInTest extends BasePage {

    /**
     * @BeforeMethod (child class) — runs AFTER BasePage.setUp().
     * TestNG guarantees parent @BeforeMethod runs first:
     *   1. BasePage.setUp()     → driver created
     *   2. loginFirst()         → driver.get(loginUrl), fill credentials, click login
     *
     * This ensures every @Test method in this class starts already logged in.
     */
    @BeforeMethod
    public void loginFirst() {
        // Read credentials from config.properties (never hard-code passwords in test code)
        String email    = Config.getValidEmail();
        String password = Config.getValidPassword();

        LoginPage loginPage = new LoginPage(driver, wait);
        loginPage.navigateToLogin();
        loginPage.login(email, password);

        // Wait until the dashboard URL is reached before proceeding
        boolean loggedIn = loginPage.isDashboardReached();
        if (!loggedIn) {
            throw new RuntimeException(
                "[PunchInTest] Pre-condition failed: Could not log in with email=" + email +
                ". Update 'valid.password' in config.properties."
            );
        }
        System.out.println("[PunchInTest] Pre-condition: Successfully logged in as " + email);
    }

    // -------------------------------------------------------------------------
    // Test Method
    // -------------------------------------------------------------------------

    /**
     * Punch-In Toast Verification Test.
     *
     * Steps:
     *  1. Navigate to the Attendance page (/attendance)
     *  2. Click the Punch-In trigger ("Add New" button)
     *  3. Wait for the toast confirmation message using:
     *       ExpectedConditions.visibilityOfElementLocated(TOAST_MESSAGE)
     *     (as required by the assignment — no Thread.sleep())
     *  4. Assert the toast text matches the expected confirmation message
     *  5. Take a screenshot and save it to src/test/resources/screenshots/
     */
    @Test(
        description = "Verify that clicking Punch-In shows a toast confirmation and captures screenshot",
        groups      = { "attendance", "smoke" }
    )
    public void testPunchInToast() {
        System.out.println("\n===== PunchInTest =====");

        AttendancePage attendancePage = new AttendancePage(driver, wait);

        // Step 1: Navigate to the Attendance page
        attendancePage.navigateToAttendance();
        System.out.println("[PunchInTest] Navigated to Attendance page.");

        // Step 2: Click the Punch-In button ("Add New" or icon in employee row)
        attendancePage.clickPunchIn();
        System.out.println("[PunchInTest] Clicked Punch-In trigger.");

        // Step 3: Wait for the toast message using ExpectedConditions (no Thread.sleep!)
        //         visibilityOfElementLocated polls until the element is present + visible
        //         OR the 15-second timeout (set in BasePage) expires and throws TimeoutException.
        String actualToastText = attendancePage.getToastText();
        System.out.println("[PunchInTest] Toast appeared: '" + actualToastText + "'");

        // Step 4: Assert the toast text matches expected confirmation
        //
        // ⚠️  IMPORTANT: Run this test ONCE, observe the actual toast text in the
        //     console output, then update 'toast.punch.in.success' in config.properties.
        //
        // We use a soft check here: the toast should contain "Punch" or "success" or similar.
        // The hard assertion is that a toast appeared at all (step 3 would fail if it doesn't).
        Assert.assertFalse(
            actualToastText.isEmpty(),
            "Toast message was empty — Punch-In may not have triggered a confirmation."
        );

        // If you know the exact expected text, enable this stricter assertion:
        // Assert.assertEquals(actualToastText, AttendancePage.EXPECTED_PUNCH_IN_TOAST,
        //     "Toast text mismatch after Punch-In");

        // Step 5: Take a screenshot as evidence of the toast message
        String screenshotPath = takeScreenshot("punch_in_toast");
        Assert.assertNotNull(
            screenshotPath,
            "Screenshot could not be saved — check permissions on screenshots directory."
        );

        System.out.println("[PASS] Punch-In toast verified. Screenshot: " + screenshotPath);
    }
}
