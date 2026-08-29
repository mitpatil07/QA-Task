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
 * AddCustomerTest — Data-Driven Add Customer Test
 * =================================================
 * Tests the Add Customer feature using multiple customer records from a CSV file.
 *
 * Test data file: src/test/resources/testdata/customer-data.csv
 * CSV columns: name, phone, email, address, city
 *
 * For each row, the test:
 *  1. Logs in (via @BeforeMethod)
 *  2. Navigates to My Customers
 *  3. Clicks Add Customer
 *  4. Fills the form with the row's data
 *  5. Submits the form
 *  6. Verifies the customer appears in the list OR a success message is shown
 *
 * ⚠️  LOCATOR NOTES:
 *  The Add Customer form fields in CustomerPage.java are based on common CRM patterns.
 *  If the form fields differ (wrong placeholders), update CustomerPage.java and rerun.
 *
 *  To find correct locators:
 *   1. Open https://test.fieldforceconnect.com/my-customers in Chrome
 *   2. Click "Add Customer"
 *   3. Right-click each input field → Inspect → note placeholder or name attribute
 *   4. Update the corresponding By locator in CustomerPage.java
 */
public class AddCustomerTest extends BasePage {

    // -------------------------------------------------------------------------
    // Pre-condition: Login before each test invocation
    // -------------------------------------------------------------------------

    /**
     * @BeforeMethod — runs AFTER BasePage.setUp() (parent's @BeforeMethod).
     * Logs in with valid credentials so the test starts on the dashboard.
     *
     * Because @DataProvider creates one test invocation per CSV row,
     * this method runs once per row — each invocation gets a fresh browser + fresh login.
     */
    @BeforeMethod
    public void loginAndSetup() {
        String email    = Config.getValidEmail();
        String password = Config.getValidPassword();

        LoginPage loginPage = new LoginPage(driver, wait);
        loginPage.navigateToLogin();
        loginPage.login(email, password);

        boolean loggedIn = loginPage.isDashboardReached();
        if (!loggedIn) {
            throw new RuntimeException(
                "[AddCustomerTest] Pre-condition failed: Login unsuccessful. " +
                "Update 'valid.password' in config.properties."
            );
        }
        System.out.println("[AddCustomerTest] Pre-condition: Logged in successfully.");
    }

    // -------------------------------------------------------------------------
    // Data Provider
    // -------------------------------------------------------------------------

    /**
     * Reads customer-data.csv and returns each row as a String[] parameter.
     * TestNG will call testAddCustomer() once for each row (3 times for 3 rows).
     *
     * Return type Object[][] where each element is Object[]{ String[] rowData }.
     * We wrap the String[] in an Object[] of length 1 so TestNG passes it as
     * a single parameter (the data array) to the test method.
     */
    @DataProvider(name = "customerData")
    public Object[][] getCustomerData() {
        // Read raw rows: each row is a String[]
        Object[][] rawData = CSVDataReader.read("src/test/resources/testdata/customer-data.csv");

        // Wrap each String[] row as Object[]{ row } so the test receives it as String[]
        Object[][] wrapped = new Object[rawData.length][1];
        for (int i = 0; i < rawData.length; i++) {
            wrapped[i][0] = rawData[i]; // the test param type is String[]
        }
        return wrapped;
    }

    // -------------------------------------------------------------------------
    // Test Method
    // -------------------------------------------------------------------------

    /**
     * Data-driven Add Customer test.
     *
     * @param data  One row from customer-data.csv as a String array.
     *              Index mapping:
     *                [0] = name
     *                [1] = phone
     *                [2] = email
     *                [3] = address
     *                [4] = city
     *
     * ⚠️  If the actual form has different fields, update CustomerPage.java
     *     AND the CSV columns, then keep the index mapping consistent here.
     */
    @Test(
        dataProvider = "customerData",
        description  = "Data-driven test: adds each customer from CSV and verifies success",
        groups       = { "customer", "regression" }
    )
    public void testAddCustomer(String[] data) {
        System.out.println("\n===== AddCustomerTest =====");
        System.out.println("Customer Name: " + data[0]);

        CustomerPage customerPage = new CustomerPage(driver, wait);

        // Step 1: Navigate to the My Customers section
        customerPage.navigateToCustomers();
        System.out.println("[AddCustomerTest] Navigated to My Customers.");

        // Step 2: Click the "Add Customer" button to open the form
        customerPage.clickAddCustomer();
        System.out.println("[AddCustomerTest] Opened Add Customer form.");

        // Step 3: Fill all form fields from the CSV row
        //         fillCustomerForm() handles each field gracefully —
        //         it skips fields that are not present in the actual form.
        customerPage.fillCustomerForm(data);
        System.out.println("[AddCustomerTest] Filled customer form.");

        // Step 4: Submit the form
        customerPage.submitForm();
        System.out.println("[AddCustomerTest] Submitted form.");

        // Step 5: Verify success — use EITHER approach based on what the site does:
        //
        //   APPROACH A: Check for a success toast/confirmation message
        boolean successMessageShown = customerPage.isSuccessMessageDisplayed();

        //   APPROACH B: Check that the customer's name now appears in the list
        boolean customerInList = customerPage.isCustomerInList(data[0]);

        // Pass if EITHER condition is satisfied
        Assert.assertTrue(
            successMessageShown || customerInList,
            "Add Customer FAILED for [" + data[0] + "]. " +
            "Neither a success message nor the customer name was found after submission.\n" +
            "Success message shown: " + successMessageShown + "\n" +
            "Customer in list: " + customerInList
        );

        if (successMessageShown) {
            System.out.println("[PASS] Success message shown: '" + customerPage.getSuccessMessageText() + "'");
        }
        if (customerInList) {
            System.out.println("[PASS] Customer '" + data[0] + "' found in customer list.");
        }
    }
}
