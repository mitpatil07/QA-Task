package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * LoginPage — Page Object for https://test.fieldforceconnect.com/auth/login
 * ============================================================================
 * Encapsulates ALL locators and interactions on the login page.
 * Test classes never interact with the DOM directly — they call methods here.
 *
 * Site observations (from live site inspection):
 *  - URL:   /auth/login
 *  - Field: "Email ID / Mobile No *"  (supports both email & phone number)
 *  - Field: Password
 *  - Error: "Invalid Email Id / Mobile No or Password" (thrown from Login JS)
 *  - The site is a React SPA (Vite), so there is no server-rendered HTML;
 *    all elements are created dynamically and may not have stable ID attributes.
 *    We use XPath with placeholder text and element type for resilience.
 *
 * ⚠️  LOCATOR NOTES:
 *  If any locator fails, right-click the element in Chrome → Inspect,
 *  copy the XPath or note the placeholder/name attribute, and update here.
 */
public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** Full URL of the login page */
    public static final String LOGIN_URL = "https://test.fieldforceconnect.com/auth/login";

    /** URL fragment present in the address bar after a successful login */
    public static final String DASHBOARD_URL_FRAGMENT = "/dashboard";

    /**
     * The exact error message text the application throws for bad credentials.
     * Captured from Chrome DevTools console:
     *   "Invalid Email Id / Mobile No or Password"
     * Used in LoginTest to assert the failure case.
     */
    public static final String EXPECTED_ERROR_TEXT = "Invalid Email Id / Mobile No or Password";

    // -------------------------------------------------------------------------
    // Locators — declared as static final By so they're initialised once
    // -------------------------------------------------------------------------

    /**
     * Email / Mobile input field.
     * Strategy: match by placeholder text — stable across React re-renders.
     * The field label says "Email ID / Mobile No *", so the placeholder likely
     * contains "Email" or "Mobile".
     */
    private static final By EMAIL_FIELD = By.xpath(
        "//input[contains(@placeholder,'Email') or " +
        "contains(@placeholder,'email') or " +
        "contains(@placeholder,'Mobile') or " +
        "contains(@placeholder,'mobile') or " +
        "@name='email' or @name='username' or @type='email']"
    );

    /**
     * Password input field.
     * Strategy: type="password" is the most reliable attribute — it's a
     * browser standard and never changes regardless of CSS framework.
     */
    private static final By PASSWORD_FIELD = By.xpath("//input[@type='password']");

    /**
     * Login submit button.
     * Strategy: try type="submit" first (most specific), then button text.
     * The '|' operator in XPath tries each alternative in order.
     */
    private static final By LOGIN_BUTTON = By.xpath(
        "//button[@type='submit'] | " +
        "//button[normalize-space()='Login'] | " +
        "//button[normalize-space()='Sign In'] | " +
        "//button[contains(normalize-space(),'Log In')]"
    );

    /**
     * Error message element.
     * Strategy: match common patterns for React alert/toast/error components.
     * Also matches the exact error string as a text fallback.
     *
     * Common React toast library classes:
     *  - Toastify__toast        (react-toastify)
     *  - react-hot-toast        (react-hot-toast)
     *  - chakra-alert           (Chakra UI)
     *  - MuiAlert-message       (Material UI)
     */
    private static final By ERROR_MESSAGE = By.xpath(
        "//*[contains(@class,'Toastify__toast')] | " +
        "//*[contains(@class,'error')] | " +
        "//*[contains(@class,'alert')] | " +
        "//*[contains(@class,'snackbar')] | " +
        "//*[contains(normalize-space(),'" + EXPECTED_ERROR_TEXT + "')]"
    );

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor — receives driver and wait from the test class (BasePage).
     * Page Objects do NOT create their own driver; they receive it via injection.
     */
    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    // -------------------------------------------------------------------------
    // Page Actions — one method per user action
    // -------------------------------------------------------------------------

    /** Navigate the browser to the login page URL */
    public void navigateToLogin() {
        driver.get(LOGIN_URL);
        System.out.println("[LoginPage] Navigated to: " + LOGIN_URL);
    }

    /**
     * Type an email or phone number into the identifier field.
     * Clears any pre-filled value first.
     */
    public void enterEmail(String email) {
        // visibilityOfElementLocated waits until the element is present AND visible
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_FIELD));
        field.clear();
        field.sendKeys(email);
        System.out.println("[LoginPage] Entered email/phone: " + email);
    }

    /** Type the password (characters are masked on screen by the browser). */
    public void enterPassword(String password) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD_FIELD));
        field.clear();
        field.sendKeys(password);
        System.out.println("[LoginPage] Entered password: [HIDDEN]");
    }

    /**
     * Click the Login/Submit button.
     * elementToBeClickable waits until the button is both visible AND enabled.
     */
    public void clickLoginButton() {
        wait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON)).click();
        System.out.println("[LoginPage] Clicked login button.");
    }

    /**
     * Convenience method — performs the full login sequence in one call.
     * Used by tests that just need to be logged in before testing something else.
     *
     * @param email    Email address or mobile number
     * @param password Account password
     */
    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLoginButton();
    }

    // -------------------------------------------------------------------------
    // Assertions / State Queries — called by test classes to verify outcomes
    // -------------------------------------------------------------------------

    /**
     * Check whether an error message is currently visible.
     * Returns false (not true exception) if the element never appears,
     * so tests can safely assert on the boolean result.
     *
     * @return true if an error message is displayed, false otherwise
     */
    public boolean isErrorMessageDisplayed() {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE));
            return el.isDisplayed();
        } catch (Exception e) {
            // Element not found within the wait timeout → no error shown
            System.out.println("[LoginPage] No error message detected.");
            return false;
        }
    }

    /**
     * Return the text content of the error message element.
     * Call this after isErrorMessageDisplayed() returns true.
     */
    public String getErrorMessageText() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Confirm that login was successful by checking the current URL.
     * After a valid login the app redirects to /dashboard.
     * WebDriverWait keeps polling the URL until it contains "/dashboard"
     * or the timeout expires.
     *
     * @return true if the browser is now on the dashboard, false if still on login
     */
    public boolean isDashboardReached() {
        try {
            // urlContains is an ExpectedCondition that polls driver.getCurrentUrl()
            wait.until(ExpectedConditions.urlContains(DASHBOARD_URL_FRAGMENT));
            String currentUrl = driver.getCurrentUrl();
            System.out.println("[LoginPage] Dashboard reached. URL: " + currentUrl);
            return currentUrl.contains(DASHBOARD_URL_FRAGMENT);
        } catch (Exception e) {
            System.out.println("[LoginPage] Dashboard NOT reached. Current URL: " + driver.getCurrentUrl());
            return false;
        }
    }
}
