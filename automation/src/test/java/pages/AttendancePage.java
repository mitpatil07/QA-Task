package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * AttendancePage — Page Object Class for Attendance and Punch-In Management.
 * Encapsulates UI elements and user interactions on the Attendance page.
 */
public class AttendancePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Attendance URL constant
    public static final String ATTENDANCE_URL = "https://test.fieldforceconnect.com/attendance";

    // Expected toast message pattern for successful punch-in
    public static final String EXPECTED_PUNCH_IN_TOAST = "Punched In Successfully";

    // Locators
    private static final By ATTENDANCE_SIDEBAR_LINK = By.xpath(
        "//*[normalize-space()='Attendance' and (self::a or self::span or self::li or self::div)]"
    );

    private static final By WAITING_FOR_PUNCH_IN_ROW = By.xpath(
        "//*[contains(normalize-space(),'Waiting for punch in')]"
    );

    private static final By ADD_NEW_BUTTON = By.xpath(
        "//button[contains(normalize-space(),'Add New')] | " +
        "//*[contains(@class,'add-new') or contains(@class,'addNew')]"
    );

    private static final By PUNCH_IN_ICON_IN_ROW = By.xpath(
        "//*[contains(normalize-space(),'Waiting for punch in')]" +
        "/ancestor::div[1]//button | " +
        "//*[contains(normalize-space(),'Waiting for punch in')]" +
        "/following-sibling::*//button[1]"
    );

    // Toast notification locators covering react-hot-toast and standard alert elements
    public static final By TOAST_MESSAGE = By.xpath(
        "//*[@role='alert'] | " +
        "//*[@data-rht-toaster] | " +
        "//*[contains(@class,'Toastify__toast-body')] | " +
        "//*[contains(@class,'toast')] | " +
        "//*[contains(@class,'notification')]"
    );

    public AttendancePage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    /**
     * Navigates directly to the Attendance section.
     */
    public void navigateToAttendance() {
        driver.get(ATTENDANCE_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(ADD_NEW_BUTTON));
    }

    /**
     * Clicks the Attendance menu item in sidebar navigation.
     */
    public void clickAttendanceSidebarLink() {
        wait.until(ExpectedConditions.elementToBeClickable(ATTENDANCE_SIDEBAR_LINK)).click();
        wait.until(ExpectedConditions.urlContains("attendance"));
    }

    /**
     * Executes punch-in action by clicking the designated trigger element.
     */
    public void clickPunchIn() {
        try {
            WebElement addNew = wait.until(ExpectedConditions.elementToBeClickable(ADD_NEW_BUTTON));
            addNew.click();
        } catch (Exception e) {
            wait.until(ExpectedConditions.elementToBeClickable(PUNCH_IN_ICON_IN_ROW)).click();
        }
    }

    /**
     * Explicitly waits for the toast message element to become visible.
     */
    public WebElement waitForToast() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(TOAST_MESSAGE));
    }

    /**
     * Extracts text content from the displayed toast message.
     */
    public String getToastText() {
        return waitForToast().getText().trim();
    }

    /**
     * Checks whether Attendance page content has rendered.
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
