package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * AttendancePage — Page Object for https://test.fieldforceconnect.com/attendance
 * ================================================================================
 * Handles all interactions on the Attendance page where Punch-In happens.
 *
 * Page layout observed from live site:
 * ┌─────────────────────────────────────────────────────────────────┐
 * │  Left Panel              │  Right Panel (My Claims tab)         │
 * │  ─────────────────       │  ─────────────────────────────────   │
 * │  [Mitesh]                │  [Filter]          [Add New] ←button │
 * │  Waiting for punch in    │                                      │
 * │  [⚙][📍][☰] (icons)    │  Date│Punch In│Punch Out│Reason│...   │
 * └─────────────────────────────────────────────────────────────────┘
 *
 * Punch-In trigger: The "Add New" button (top-right of the table) OR
 * the action icons in the employee row. We try "Add New" first as it
 * is the most visually prominent element.
 *
 * ⚠️  LOCATOR NOTES:
 *  - If "Add New" is not the punch-in trigger, inspect the icon next to
 *    "Waiting for punch in" text and update PUNCH_IN_TRIGGER below.
 *  - The toast message class depends on the React toast library used.
 *    Check the DOM after clicking punch-in and update TOAST_MESSAGE.
 */
public class AttendancePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** Full URL of the Attendance page (observed from live site screenshot) */
    public static final String ATTENDANCE_URL = "https://test.fieldforceconnect.com/attendance";

    /**
     * Expected toast text after a successful punch-in.
     * ⚠️  UPDATE THIS after running the test once and observing the actual toast.
     *     Common values: "Punched In Successfully", "Attendance marked", etc.
     */
    public static final String EXPECTED_PUNCH_IN_TOAST = "Punched In Successfully";

    // -------------------------------------------------------------------------
    // Locators
    // -------------------------------------------------------------------------

    /**
     * "Attendance" link in the left sidebar navigation.
     * Observed text: "Attendance" (from the dashboard screenshot).
     */
    private static final By ATTENDANCE_SIDEBAR_LINK = By.xpath(
        "//*[normalize-space()='Attendance' and (self::a or self::span or self::li or self::div)]"
    );

    /**
     * Employee row showing "Waiting for punch in" status in the left panel.
     * This is the row containing the employee who needs to punch in.
     */
    private static final By WAITING_FOR_PUNCH_IN_ROW = By.xpath(
        "//*[contains(normalize-space(),'Waiting for punch in')]"
    );

    /**
     * Primary punch-in trigger: "Add New" button (top-right of the attendance table).
     * Observed from the live site screenshot.
     */
    private static final By ADD_NEW_BUTTON = By.xpath(
        "//button[normalize-space()='Add New'] | " +
        "//button[contains(normalize-space(),'Add New')] | " +
        "//*[contains(@class,'add-new') or contains(@class,'addNew')]"
    );

    /**
     * Alternative punch-in trigger: action icon inside the employee row.
     * The left panel shows icons (⚙, 📍, ☰) next to the employee name.
     * This XPath finds the first clickable icon inside the punch-in row.
     *
     * If the above ADD_NEW_BUTTON is not the punch-in mechanism, comment it out
     * and uncomment this approach in clickPunchIn().
     */
    private static final By PUNCH_IN_ICON_IN_ROW = By.xpath(
        "//*[contains(normalize-space(),'Waiting for punch in')]" +
        "/ancestor::div[1]//button | " +
        "//*[contains(normalize-space(),'Waiting for punch in')]" +
        "/following-sibling::*//button[1]"
    );

    /**
     * Toast / confirmation message element.
     * After clicking Punch-In, a toast notification appears.
     *
     * React toast library class patterns:
     *  - react-toastify:   .Toastify__toast-body
     *  - react-hot-toast:  [id^='toast-']
     *  - Chakra UI:        .chakra-toast
     *  - Material UI:      .MuiSnackbarContent-message
     *  - role="alert":     accessible toast pattern (works across libraries)
     *
     * We use role="alert" as the most universal selector, with class fallbacks.
     */
    public static final By TOAST_MESSAGE = By.xpath(
        "//*[@role='alert'] | " +
        "//*[contains(@class,'Toastify__toast-body')] | " +
        "//*[contains(@class,'Toastify__toast')] | " +
        "//*[contains(@class,'toast')] | " +
        "//*[contains(@class,'snackbar')] | " +
        "//*[contains(@class,'notification')]"
    );

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public AttendancePage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    /**
     * Navigate directly to the Attendance page by URL.
     * Faster and more reliable than clicking the sidebar link.
     */
    public void navigateToAttendance() {
        driver.get(ATTENDANCE_URL);
        System.out.println("[AttendancePage] Navigated to: " + ATTENDANCE_URL);
        // Wait until the page content is visible before proceeding
        wait.until(ExpectedConditions.visibilityOfElementLocated(ADD_NEW_BUTTON));
    }

    /**
     * Click the "Attendance" link in the sidebar.
     * Use this if you prefer navigating via the sidebar (as a real user would).
     */
    public void clickAttendanceSidebarLink() {
        wait.until(ExpectedConditions.elementToBeClickable(ATTENDANCE_SIDEBAR_LINK)).click();
        System.out.println("[AttendancePage] Clicked Attendance in sidebar.");
        // Wait for the attendance page to load
        wait.until(ExpectedConditions.urlContains("attendance"));
    }

    // -------------------------------------------------------------------------
    // Punch-In Actions
    // -------------------------------------------------------------------------

    /**
     * Click the Punch-In trigger button.
     *
     * Primary approach: click "Add New" button (observed in screenshot).
     * If that does not open the punch-in flow, try the icon in the employee row.
     *
     * ⚠️  After running once, confirm which button actually triggers punch-in
     *     and remove the fallback if not needed.
     */
    public void clickPunchIn() {
        try {
            // Primary: "Add New" button in the top-right of the attendance table
            WebElement addNew = wait.until(ExpectedConditions.elementToBeClickable(ADD_NEW_BUTTON));
            System.out.println("[AttendancePage] Clicking 'Add New' button for punch-in.");
            addNew.click();
        } catch (Exception e) {
            // Fallback: action icon inside the "Waiting for punch in" employee row
            System.out.println("[AttendancePage] 'Add New' not found, trying punch-in icon in row.");
            wait.until(ExpectedConditions.elementToBeClickable(PUNCH_IN_ICON_IN_ROW)).click();
        }
    }

    // -------------------------------------------------------------------------
    // Toast / Confirmation Assertions
    // -------------------------------------------------------------------------

    /**
     * Wait for the toast / notification message to appear after punch-in.
     * Uses ExpectedConditions.visibilityOfElementLocated as required by the assignment.
     *
     * @return The WebElement of the toast message
     */
    public WebElement waitForToast() {
        System.out.println("[AttendancePage] Waiting for toast notification...");
        return wait.until(ExpectedConditions.visibilityOfElementLocated(TOAST_MESSAGE));
    }

    /**
     * Return the text content of the toast message.
     * Called after waitForToast() confirms visibility.
     */
    public String getToastText() {
        String text = waitForToast().getText().trim();
        System.out.println("[AttendancePage] Toast text: '" + text + "'");
        return text;
    }

    /**
     * Verify the page loaded successfully (employee row is visible).
     * Used as a quick sanity check after navigation.
     */
    public boolean isAttendancePageLoaded() {
        try {
            return wait.until(
                ExpectedConditions.visibilityOfElementLocated(WAITING_FOR_PUNCH_IN_ROW)
            ).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
