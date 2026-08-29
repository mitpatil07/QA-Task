package tests;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.BasePage;
import pages.LoginPage;
import utils.CSVDataReader;

/**
 * LoginTest — Data-Driven Authentication Test Suite.
 * Executes login test scenarios using records loaded from CSV.
 */
public class LoginTest extends BasePage {

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {
        return CSVDataReader.read("src/test/resources/testdata/login-data.csv");
    }

    @Test(
        dataProvider = "loginData",
        description  = "Data-driven login test: validates valid and invalid credential paths",
        groups       = { "login", "smoke" }
    )
    public void testLogin(String email, String password, String expectedResult) {
        System.out.println("Executing LoginTest for identifier: " + email);

        LoginPage loginPage = new LoginPage(driver, wait);
        loginPage.navigateToLogin();
        loginPage.login(email, password);

        if ("success".equalsIgnoreCase(expectedResult)) {
            boolean onDashboard = loginPage.isDashboardReached();
            Assert.assertTrue(
                onDashboard,
                "Valid login assertion failed for [" + email + "]. Current URL: " + driver.getCurrentUrl()
            );
            System.out.println("[SUCCESS] Dashboard reached for user: " + email);
        } else {
            boolean errorShown = loginPage.isErrorMessageDisplayed();
            Assert.assertTrue(
                errorShown,
                "Invalid login assertion failed for [" + email + "]. No error message displayed."
            );

            String actualError = loginPage.getErrorMessageText();
            Assert.assertTrue(
                actualError.contains("Invalid") || actualError.contains("invalid") || !actualError.isEmpty(),
                "Unexpected error message text: '" + actualError + "'"
            );
            System.out.println("[SUCCESS] Error feedback confirmed: '" + actualError + "'");
        }
    }
}
