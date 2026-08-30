package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * LoginPage — Page Object Class for Application Authentication.
 * Encapsulates UI elements and user interactions on the login page.
 */
public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Login URL constant
    public static final String LOGIN_URL = "https://test.fieldforceconnect.com/auth/login";

    // Dashboard URL fragment after successful login
    public static final String DASHBOARD_URL_FRAGMENT = "/dashboard";

    // Application error message for invalid login attempts
    public static final String EXPECTED_ERROR_TEXT = "Invalid Email Id / Mobile No or Password";

    // Locators
    private static final By EMAIL_FIELD = By.xpath(
        "//input[contains(@placeholder,'Email') or contains(@placeholder,'email') or " +
        "contains(@placeholder,'Mobile') or contains(@placeholder,'mobile') or " +
        "@name='email' or @name='username' or @type='email']"
    );

    private static final By PASSWORD_FIELD = By.xpath("//input[@type='password']");

    private static final By LOGIN_BUTTON = By.xpath(
        "//button[@type='submit'] | " +
        "//button[normalize-space()='Login'] | " +
        "//button[normalize-space()='Sign In'] | " +
        "//button[contains(normalize-space(),'Log In')]"
    );

    private static final By ERROR_MESSAGE = By.xpath(
        "//*[@role='alert'] | " +
        "//*[@data-rht-toaster] | " +
        "//*[contains(@class,'Toastify__toast')] | " +
        "//*[contains(@class,'error')] | " +
        "//*[contains(@class,'alert')] | " +
        "//*[contains(normalize-space(),'" + EXPECTED_ERROR_TEXT + "')]"
    );

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    /**
     * Navigates directly to the login page.
     */
    public void navigateToLogin() {
        driver.get(LOGIN_URL);
        try {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "window.localStorage.clear(); window.sessionStorage.clear();"
            );
            driver.manage().deleteAllCookies();
            driver.get(LOGIN_URL);
        } catch (Exception ignored) {}
    }

    /**
     * Enters user email or phone number in the credential input field.
     */
    public void enterEmail(String email) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_FIELD));
        field.clear();
        field.sendKeys(email);
    }

    /**
     * Enters user password into password input field.
     */
    public void enterPassword(String password) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD_FIELD));
        field.clear();
        field.sendKeys(password);
    }

    /**
     * Clicks the login submit button.
     */
    public void clickLoginButton() {
        wait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON)).click();
    }

    /**
     * Helper method to perform full login sequence.
     */
    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLoginButton();
    }

    /**
     * Checks if error message is displayed on login failure.
     */
    public boolean isErrorMessageDisplayed() {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE));
            return el.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns error message text.
     */
    public String getErrorMessageText() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Verifies dashboard redirect on valid login.
     */
    public boolean isDashboardReached() {
        try {
            wait.until(ExpectedConditions.urlContains(DASHBOARD_URL_FRAGMENT));
            return driver.getCurrentUrl().contains(DASHBOARD_URL_FRAGMENT);
        } catch (Exception e) {
            return false;
        }
    }
}
