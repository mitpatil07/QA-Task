package pages;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

/**
 * BasePage — Foundation class for the Page Object Model (POM)
 * ==============================================================
 * All page classes inherit from this class.
 * Responsibilities:
 *   1. Setup: launch Chrome browser before each test method
 *   2. Teardown: quit the browser after each test method
 *   3. Provide a shared WebDriver + WebDriverWait instance
 *   4. Utility: takeScreenshot() for evidence capture
 *
 * WHY POM?
 *   Page Object Model separates the HOW (locators, interactions on a page)
 *   from the WHAT (test assertions). If a locator changes, you fix it in
 *   one page class — not scattered across 20 test methods.
 *
 * WHY WebDriverManager?
 *   Normally you'd download ChromeDriver manually and keep it in sync
 *   with your Chrome version. WebDriverManager auto-detects your Chrome
 *   version and downloads the matching driver at runtime. Zero manual setup.
 */
public class BasePage {

    // -------------------------------------------------------------------------
    // Shared fields — accessible to all subclasses (page classes + test classes)
    // -------------------------------------------------------------------------

    /** The browser instance. One WebDriver = one browser window. */
    protected WebDriver driver;

    /**
     * Explicit wait helper. Used everywhere instead of Thread.sleep().
     * Polls the DOM up to `timeout` seconds until a condition is true.
     */
    protected WebDriverWait wait;

    /** Default wait timeout in seconds for all WebDriverWait usages. */
    private static final int WAIT_TIMEOUT_SECONDS = 15;

    /** Directory where screenshot evidence is saved. Relative to project root. */
    public static final String SCREENSHOT_DIR = "src/test/resources/screenshots/";

    // -------------------------------------------------------------------------
    // TestNG lifecycle hooks — run automatically before/after each @Test method
    // -------------------------------------------------------------------------

    /**
     * @BeforeMethod — runs BEFORE each @Test method.
     * In TestNG, if the parent class has @BeforeMethod and the child also has one,
     * the PARENT's @BeforeMethod runs FIRST. So child tests can rely on `driver`
     * already being initialised when their @BeforeMethod executes.
     */
    @BeforeMethod
    public void setUp() {
        // Step 1: WebDriverManager downloads the correct ChromeDriver version
        //         matching the Chrome browser installed on this machine.
        WebDriverManager.chromedriver().setup();

        // Step 2: Configure Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");          // open full screen
        options.addArguments("--disable-notifications");    // block browser popups
        options.addArguments("--disable-popup-blocking");
        // Uncomment the line below to run headless (no visible browser window):
        // options.addArguments("--headless=new");

        // Step 3: Launch Chrome
        driver = new ChromeDriver(options);

        // Step 4: Explicit wait — used with ExpectedConditions throughout all tests.
        //         NEVER use Thread.sleep(); explicit wait is smarter and faster.
        wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT_SECONDS));

        // Step 5: Implicit wait — fallback timeout for driver.findElement() calls
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        System.out.println("[SETUP] Browser launched. Session: " + driver.hashCode());
    }

    /**
     * @AfterMethod — runs AFTER each @Test method (even if the test fails).
     * Always quits the browser to free up resources and prevent ghost processes.
     */
    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("[TEARDOWN] Browser closed.");
        }
    }

    // -------------------------------------------------------------------------
    // Utility methods — available to all subclasses
    // -------------------------------------------------------------------------

    /**
     * Captures a screenshot of the current browser state.
     * Used in PunchInTest to capture evidence of the toast confirmation message.
     *
     * @param fileName Descriptive name for the screenshot (no extension needed)
     * @return Absolute path of the saved PNG file, or null if capture failed
     */
    public String takeScreenshot(String fileName) {
        try {
            // Cast driver to TakesScreenshot interface and get raw PNG bytes
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            // Ensure the screenshots directory exists (creates parent dirs too)
            Path destDir = Paths.get(SCREENSHOT_DIR);
            Files.createDirectories(destDir);

            // Append timestamp to prevent overwrites across test runs
            String destPath = SCREENSHOT_DIR + fileName + "_" + System.currentTimeMillis() + ".png";
            Files.copy(srcFile.toPath(), Paths.get(destPath));

            System.out.println("[SCREENSHOT] Saved to: " + destPath);
            return destPath;

        } catch (IOException e) {
            System.err.println("[SCREENSHOT ERROR] Could not save screenshot: " + e.getMessage());
            return null;
        }
    }
}
