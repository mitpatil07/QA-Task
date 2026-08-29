package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;

/**
 * CustomerPage — Page Object for the "My Customers" module
 * ==========================================================
 * Handles navigation to the customer list, Add Customer form,
 * form submission, and success/list verification.
 *
 * Site context:
 *  - "My Customers" is a collapsible sidebar menu item (has a ▶ arrow)
 *  - Likely expands to sub-items like "All Customers", "Add Customer", etc.
 *  - OR navigates to a page at /customers with an "Add Customer" button
 *
 * ⚠️  LOCATOR NOTES:
 *  The Add Customer form fields below are based on typical CRM applications.
 *  After running the test once, inspect the actual form in Chrome DevTools
 *  and update the placeholder values and field locators to match exactly.
 *
 *  Common adjustments needed:
 *   - If the form opens as a MODAL, no URL change occurs after clicking Add
 *   - If the form is on a separate PAGE, wait for URL change
 *   - Field names may differ (e.g., "Customer Name" vs "Full Name")
 */
public class CustomerPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** URL fragment for the customer list page */
    public static final String CUSTOMERS_URL = "https://test.fieldforceconnect.com/my-customers";

    // -------------------------------------------------------------------------
    // Locators — Navigation
    // -------------------------------------------------------------------------

    /**
     * "My Customers" sidebar menu item.
     * This item has a ▶ expand arrow (seen in screenshot), so clicking it
     * may either expand sub-items or navigate directly.
     */
    private static final By MY_CUSTOMERS_MENU = By.xpath(
        "//*[normalize-space()='My Customers' and " +
        "(self::span or self::a or self::li or self::div)]"
    );

    /**
     * "Add Customer" button on the customer list page.
     * May also appear as a sub-menu item after expanding "My Customers".
     */
    private static final By ADD_CUSTOMER_BUTTON = By.xpath(
        "//button[contains(normalize-space(),'Add Customer')] | " +
        "//button[contains(normalize-space(),'Add New')] | " +
        "//a[contains(normalize-space(),'Add Customer')] | " +
        "//*[contains(@class,'add') and contains(normalize-space(),'Customer')]"
    );

    // -------------------------------------------------------------------------
    // Locators — Add Customer Form Fields
    // -------------------------------------------------------------------------
    // ⚠️  These are based on common CRM field patterns.
    //     Update placeholder values to match the actual form labels.

    /** Customer full name field */
    private static final By FIELD_NAME = By.xpath(
        "//input[contains(@placeholder,'Name') or " +
        "contains(@placeholder,'name') or " +
        "@name='name' or @name='customerName' or @name='fullName']"
    );

    /** Phone / mobile number field */
    private static final By FIELD_PHONE = By.xpath(
        "//input[@type='tel' or " +
        "contains(@placeholder,'Phone') or " +
        "contains(@placeholder,'Mobile') or " +
        "@name='phone' or @name='mobile' or @name='contact']"
    );

    /** Email address field */
    private static final By FIELD_EMAIL = By.xpath(
        "//input[@type='email' or " +
        "contains(@placeholder,'Email') or " +
        "@name='email']"
    );

    /** Address field — could be input or textarea */
    private static final By FIELD_ADDRESS = By.xpath(
        "//input[contains(@placeholder,'Address')] | " +
        "//textarea[contains(@placeholder,'Address')] | " +
        "//input[@name='address'] | " +
        "//textarea[@name='address']"
    );

    /** City field */
    private static final By FIELD_CITY = By.xpath(
        "//input[contains(@placeholder,'City') or @name='city']"
    );

    /**
     * Form submit button — inside the Add Customer form.
     * Prefer type="submit" as it is browser-standard and stable.
     */
    private static final By SUBMIT_BUTTON = By.xpath(
        "//button[@type='submit'] | " +
        "//button[normalize-space()='Save'] | " +
        "//button[normalize-space()='Submit'] | " +
        "//button[normalize-space()='Add'] | " +
        "//button[contains(normalize-space(),'Save Customer')]"
    );

    // -------------------------------------------------------------------------
    // Locators — Post-Submit Verification
    // -------------------------------------------------------------------------

    /**
     * Success toast / confirmation message after customer is added.
     * Same toast selector pattern as AttendancePage — React toast libraries
     * use role="alert" for accessibility, making it a reliable selector.
     */
    private static final By SUCCESS_MESSAGE = By.xpath(
        "//*[@role='alert'] | " +
        "//*[contains(@class,'Toastify__toast-body')] | " +
        "//*[contains(@class,'success')] | " +
        "//*[contains(@class,'toast')] | " +
        "//*[contains(normalize-space(),'success')] | " +
        "//*[contains(normalize-space(),'Success')]"
    );

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public CustomerPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    /**
     * Navigate to My Customers via the sidebar menu.
     * Clicks "My Customers" which may expand a sub-menu or navigate directly.
     */
    public void navigateToCustomers() {
        try {
            // First try direct URL navigation for speed
            driver.get(CUSTOMERS_URL);
            System.out.println("[CustomerPage] Navigated to: " + CUSTOMERS_URL);
            // Give the page a moment to load
            wait.until(ExpectedConditions.urlContains("customer"));
        } catch (Exception e) {
            // Fallback: use sidebar navigation
            System.out.println("[CustomerPage] Direct nav failed, using sidebar.");
            wait.until(ExpectedConditions.elementToBeClickable(MY_CUSTOMERS_MENU)).click();
        }
    }

    /**
     * Click the "Add Customer" button to open the form.
     * The form may open as a modal or navigate to a new page.
     */
    public void clickAddCustomer() {
        wait.until(ExpectedConditions.elementToBeClickable(ADD_CUSTOMER_BUTTON)).click();
        System.out.println("[CustomerPage] Clicked Add Customer button.");
    }

    // -------------------------------------------------------------------------
    // Form Filling
    // -------------------------------------------------------------------------

    /**
     * Fill the customer form with data from a single CSV row.
     *
     * @param data Array of field values: [name, phone, email, address, city]
     *             Indices match the CSV column order in customer-data.csv.
     *             ⚠️  Update the column mapping if the form has different fields.
     */
    public void fillCustomerForm(String[] data) {
        // data[0] = name
        fillFieldIfPresent(FIELD_NAME, data[0], "Name");

        // data[1] = phone
        if (data.length > 1) fillFieldIfPresent(FIELD_PHONE, data[1], "Phone");

        // data[2] = email
        if (data.length > 2) fillFieldIfPresent(FIELD_EMAIL, data[2], "Email");

        // data[3] = address
        if (data.length > 3) fillFieldIfPresent(FIELD_ADDRESS, data[3], "Address");

        // data[4] = city
        if (data.length > 4) fillFieldIfPresent(FIELD_CITY, data[4], "City");
    }

    /**
     * Helper: fill a field if it exists on the page.
     * Gracefully skips if the field is not found (so partial forms still work).
     */
    private void fillFieldIfPresent(By locator, String value, String fieldName) {
        try {
            WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            field.clear();
            field.sendKeys(value);
            System.out.println("[CustomerPage] Filled " + fieldName + ": " + value);
        } catch (Exception e) {
            System.out.println("[CustomerPage] WARNING: Field '" + fieldName + "' not found. Skipping.");
        }
    }

    /** Click the submit button inside the Add Customer form. */
    public void submitForm() {
        wait.until(ExpectedConditions.elementToBeClickable(SUBMIT_BUTTON)).click();
        System.out.println("[CustomerPage] Submitted Add Customer form.");
    }

    // -------------------------------------------------------------------------
    // Assertions
    // -------------------------------------------------------------------------

    /**
     * Check whether a success message is displayed after form submission.
     * @return true if success toast/alert is visible, false otherwise
     */
    public boolean isSuccessMessageDisplayed() {
        try {
            return wait.until(
                ExpectedConditions.visibilityOfElementLocated(SUCCESS_MESSAGE)
            ).isDisplayed();
        } catch (Exception e) {
            System.out.println("[CustomerPage] No success message detected.");
            return false;
        }
    }

    /**
     * Check if a customer with the given name appears anywhere in the page.
     * Searches the entire page DOM for the customer name text.
     * This works whether the customer appears in a table row, card, or list.
     *
     * @param customerName The name value submitted in the form
     * @return true if the customer name is found on the page
     */
    public boolean isCustomerInList(String customerName) {
        try {
            // Search the entire page for the customer name text
            By nameInPage = By.xpath(
                "//*[contains(normalize-space(),'" + customerName + "')]"
            );
            List<WebElement> matches = driver.findElements(nameInPage);
            boolean found = !matches.isEmpty();
            System.out.println("[CustomerPage] Customer '" + customerName + "' in list: " + found);
            return found;
        } catch (Exception e) {
            return false;
        }
    }

    /** Return the text of the success message. */
    public String getSuccessMessageText() {
        try {
            return wait.until(
                ExpectedConditions.visibilityOfElementLocated(SUCCESS_MESSAGE)
            ).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }
}
