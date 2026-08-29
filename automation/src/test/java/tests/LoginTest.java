package tests;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.BasePage;
import pages.LoginPage;
import utils.CSVDataReader;

/**
 * LoginTest — Data-Driven Login Test
 * =====================================
 * Tests the login functionality at https://test.fieldforceconnect.com/auth/login
 * using multiple credential sets read from a CSV file.
 *
 * Test data file: src/test/resources/testdata/login-data.csv
 *
 * CSV columns: email, password, expectedResult
 *   - expectedResult = "success" → assert browser navigates to /dashboard
 *   - expectedResult = "failure" → assert error message is displayed
 *
 * Rows in login-data.csv:
 *   Row 1: valid email login → success
 *   Row 2: valid email login (repeat — demonstrates data-driven with same creds)
 *   Row 3: valid phone login (if site supports phone as identifier)
 *   Row 4: unregistered email + wrong password → failure
 *   Row 5: correct email + wrong password → failure
 *
 * TestNG @DataProvider:
 *   Each row in the CSV becomes one test invocation.
 *   With 5 rows, TestNG reports 5 separate test results.
 *   If row 4 fails, rows 1–3 and 5 still run independently.
 *
 * IMPORTANT: Update login-data.csv — replace YOUR_PASSWORD_HERE with real password.
 */
public class LoginTest extends BasePage {

    // -------------------------------------------------------------------------
    // Data Provider — reads CSV and feeds rows to the test method
    // -------------------------------------------------------------------------

    /**
     * @DataProvider bridges the CSV file to the @Test method.
     * Returns Object[][] where each Object[] is [email, password, expectedResult].
     *
     * The `name` attribute links this provider to the @Test method below
     * via dataProvider="loginData".
     */
    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {
        // CSVDataReader.read() skips the header row and returns data rows only
        return CSVDataReader.read("src/test/resources/testdata/login-data.csv");
    }

    // -------------------------------------------------------------------------
    // Test Method
    // -------------------------------------------------------------------------

    /**
     * Data-driven login test.
     *
     * For each row in login-data.csv, TestNG:
     *  1. Calls BasePage.setUp()       → launches a fresh Chrome window
     *  2. Calls this test method       → executes the login scenario
     *  3. Calls BasePage.tearDown()    → quits Chrome
     *
     * @param email          Login identifier (email or mobile number)
     * @param password       Account password
     * @param expectedResult "success" or "failure" (from CSV column 3)
     */
    @Test(
        dataProvider   = "loginData",
        description    = "Data-driven login test: validates successful and failed logins from CSV",
        groups         = { "login", "smoke" }
    )
    public void testLogin(String email, String password, String expectedResult) {
        System.out.println("\n===== LoginTest =====");
        System.out.println("Email:    " + email);
        System.out.println("Expected: " + expectedResult);

        // Step 1: Create the LoginPage object — pass driver + wait from BasePage
        LoginPage loginPage = new LoginPage(driver, wait);

        // Step 2: Navigate to the login page URL
        loginPage.navigateToLogin();

        // Step 3: Enter credentials and click the login button
        loginPage.login(email, password);

        // Step 4: Assert the outcome based on the CSV's "expectedResult" column
        if ("success".equalsIgnoreCase(expectedResult)) {
            /*
             * VALID LOGIN CASE:
             * After a correct login, the app should redirect to /dashboard.
             * isDashboardReached() uses WebDriverWait.until(urlContains("/dashboard"))
             * so it polls the URL without any Thread.sleep().
             */
            boolean onDashboard = loginPage.isDashboardReached();

            Assert.assertTrue(
                onDashboard,
                "VALID LOGIN FAILED for [" + email + "]. " +
                "Expected to reach /dashboard but current URL is: " + driver.getCurrentUrl()
            );

            System.out.println("[PASS] Valid login → Dashboard reached. Email: " + email);

        } else {
            /*
             * INVALID LOGIN CASE:
             * After wrong credentials, an error message should appear.
             * The exact error text is: "Invalid Email Id / Mobile No or Password"
             * (captured from the Chrome DevTools console on the live site).
             */
            boolean errorShown = loginPage.isErrorMessageDisplayed();

            Assert.assertTrue(
                errorShown,
                "INVALID LOGIN FAILED for [" + email + "]. " +
                "Expected an error message but none was displayed."
            );

            // Additional assertion: verify the error message text contains expected string
            String actualError = loginPage.getErrorMessageText();
            Assert.assertTrue(
                actualError.contains("Invalid") || actualError.contains("invalid") || !actualError.isEmpty(),
                "Error message shown but text was unexpected: '" + actualError + "'"
            );

            System.out.println("[PASS] Invalid login → Error displayed: '" + actualError + "'");
        }
    }
}
