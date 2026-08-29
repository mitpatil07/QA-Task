package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.BasePage;
import pages.CustomerPage;
import pages.LoginPage;
import utils.CSVDataReader;
import utils.Config;

/**
 * AddCustomerTest — Data-Driven Customer Creation Test Suite.
 * Automates Add Customer flow using records loaded from CSV.
 */
public class AddCustomerTest extends BasePage {

    @BeforeMethod
    public void loginAndSetup() {
        String email    = Config.getValidEmail();
        String password = Config.getValidPassword();

        LoginPage loginPage = new LoginPage(driver, wait);
        loginPage.navigateToLogin();
        loginPage.login(email, password);

        boolean loggedIn = loginPage.isDashboardReached();
        if (!loggedIn) {
            throw new RuntimeException("Pre-condition failed: Login unsuccessful for email: " + email);
        }
    }

    @DataProvider(name = "customerData")
    public Object[][] getCustomerData() {
        Object[][] rawData = CSVDataReader.read("src/test/resources/testdata/customer-data.csv");
        Object[][] wrapped = new Object[rawData.length][1];
        for (int i = 0; i < rawData.length; i++) {
            wrapped[i][0] = rawData[i];
        }
        return wrapped;
    }

    @Test(
        dataProvider = "customerData",
        description  = "Data-driven test: adds customer records from CSV and validates creation",
        groups       = { "customer", "regression" }
    )
    public void testAddCustomer(String[] data) {
        System.out.println("Executing AddCustomerTest for record: " + data[0]);

        CustomerPage customerPage = new CustomerPage(driver, wait);
        customerPage.navigateToCustomers();
        customerPage.clickAddCustomer();
        customerPage.fillCustomerForm(data);
        customerPage.submitForm();

        boolean successMessageShown = customerPage.isSuccessMessageDisplayed();
        boolean customerInList = customerPage.isCustomerInList(data[0]);

        Assert.assertTrue(
            successMessageShown || customerInList,
            "Add Customer assertion failed for [" + data[0] + "]."
        );

        System.out.println("[SUCCESS] Add Customer verified for record: " + data[0]);
    }
}
