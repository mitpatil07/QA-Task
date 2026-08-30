package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;

/**
 * CustomerPage — Page Object for Customer Management & Creation.
 * Encapsulates UI elements, form interactions, and post-submission checks.
 */
public class CustomerPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Customer URL constant
    public static final String CUSTOMERS_URL = "https://test.fieldforceconnect.com/my-customers";

    // Locators — Navigation
    private static final By MY_CUSTOMERS_MENU = By.xpath(
        "//*[normalize-space()='My Customers' and (self::span or self::a or self::li or self::div)]"
    );

    private static final By ADD_CUSTOMER_BUTTON = By.xpath(
        "//button[contains(normalize-space(),'Add Customer')] | " +
        "//button[contains(normalize-space(),'Add New')] | " +
        "//button[contains(normalize-space(),'Create New Customer')] | " +
        "//button[contains(normalize-space(),'New Customer')] | " +
        "//button[normalize-space()='Add'] | " +
        "//button[contains(normalize-space(),'Add')] | " +
        "//a[contains(normalize-space(),'Add Customer')] | " +
        "//a[contains(normalize-space(),'Add')] | " +
        "//*[contains(@class,'add') and contains(normalize-space(),'Customer')] | " +
        "//button[.*[contains(@class,'add') or contains(@class,'Add') or contains(@data-testid,'Add')]]"
    );

    // Form Field Locators
    private static final By FIELD_NAME = By.xpath(
        "//input[contains(@placeholder,'Name') or contains(@placeholder,'name') or " +
        "@name='name' or @name='customerName' or @name='fullName']"
    );

    private static final By FIELD_PHONE = By.xpath(
        "//input[@type='tel' or contains(@placeholder,'Phone') or contains(@placeholder,'Mobile') or " +
        "@name='phone' or @name='mobile' or @name='contact']"
    );

    private static final By FIELD_EMAIL = By.xpath(
        "//input[@type='email' or contains(@placeholder,'Email') or @name='email']"
    );

    private static final By FIELD_ADDRESS = By.xpath(
        "//input[contains(@placeholder,'Address')] | " +
        "//textarea[contains(@placeholder,'Address')] | " +
        "//input[@name='address'] | //textarea[@name='address']"
    );

    private static final By FIELD_CITY = By.xpath(
        "//input[contains(@placeholder,'City') or @name='city']"
    );

    private static final By SUBMIT_BUTTON = By.xpath(
        "//button[@type='submit'] | //button[normalize-space()='Save'] | " +
        "//button[normalize-space()='Submit'] | //button[normalize-space()='Add']"
    );

    // Post-Submit Alert / Toast Locators
    private static final By SUCCESS_MESSAGE = By.xpath(
        "//*[@role='alert'] | " +
        "//*[@data-rht-toaster] | " +
        "//*[contains(@class,'Toastify__toast-body')] | " +
        "//*[contains(@class,'toast')] | " +
        "//*[contains(@class,'success')]"
    );

    public CustomerPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    /**
     * Navigates to Customer section.
     */
    public void navigateToCustomers() {
        try {
            driver.get(CUSTOMERS_URL);
            wait.until(ExpectedConditions.urlContains("customer"));
        } catch (Exception e) {
            try {
                driver.get("https://test.fieldforceconnect.com/customers");
                wait.until(ExpectedConditions.urlContains("customer"));
            } catch (Exception ex) {
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(MY_CUSTOMERS_MENU)).click();
                } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Clicks button to launch Add Customer form.
     */
    public void clickAddCustomer() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(ADD_CUSTOMER_BUTTON)).click();
        } catch (Exception e) {
            // Fallback: click any element with 'Add' text if exact button match isn't clickable immediately
            try {
                driver.findElement(By.xpath("//*[contains(text(),'Add') or contains(text(),'New Customer')]")).click();
            } catch (Exception ignored) {}
        }
    }

    /**
     * Fills form fields using data array.
     */
    public void fillCustomerForm(String[] data) {
        fillFieldIfPresent(FIELD_NAME, data[0]);
        if (data.length > 1) fillFieldIfPresent(FIELD_PHONE, data[1]);
        if (data.length > 2) fillFieldIfPresent(FIELD_EMAIL, data[2]);
        if (data.length > 3) fillFieldIfPresent(FIELD_ADDRESS, data[3]);
        if (data.length > 4) fillFieldIfPresent(FIELD_CITY, data[4]);
    }

    private void fillFieldIfPresent(By locator, String value) {
        try {
            WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            field.clear();
            field.sendKeys(value);
        } catch (Exception ignored) {
        }
    }

    /**
     * Submits the customer form.
     */
    public void submitForm() {
        wait.until(ExpectedConditions.elementToBeClickable(SUBMIT_BUTTON)).click();
    }

    /**
     * Asserts visibility of success feedback element.
     */
    public boolean isSuccessMessageDisplayed() {
        try {
            return wait.until(
                ExpectedConditions.visibilityOfElementLocated(SUCCESS_MESSAGE)
            ).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifies whether customer name exists on page after submission.
     */
    public boolean isCustomerInList(String customerName) {
        try {
            By nameInPage = By.xpath("//*[contains(normalize-space(),'" + customerName + "')]");
            List<WebElement> matches = driver.findElements(nameInPage);
            return !matches.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns success notification text.
     */
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
