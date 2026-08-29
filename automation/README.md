# FieldForceConnect Automation Project

## Overview
Automated Test Suite for [https://test.fieldforceconnect.com/](https://test.fieldforceconnect.com/) built using **Java 17**, **Selenium WebDriver**, **TestNG**, and **Maven**.

The project strictly follows the **Page Object Model (POM)** architectural design pattern to separate element locators and page interactions from test logic.

---

## Project Structure

```
automation/
├── pom.xml                              # Maven build config & dependencies
├── testng.xml                           # TestNG suite runner
├── README.md                            # Automation documentation
└── src/
    └── test/
        ├── java/
        │   ├── pages/                   # Page Object classes (POM)
        │   │   ├── BasePage.java        # Driver initialization, teardown & screenshot utility
        │   │   ├── LoginPage.java       # Login locators & page actions
        │   │   ├── AttendancePage.java  # Punch-In locators & toast assertions
        │   │   └── CustomerPage.java    # Add Customer form locators & actions
        │   ├── tests/                   # TestNG test suites
        │   │   ├── LoginTest.java       # Parameterized data-driven login test
        │   │   ├── PunchInTest.java     # Punch-In toast verification & screenshot
        │   │   └── AddCustomerTest.java # Parameterized data-driven Add Customer test
        │   └── utils/                   # Shared utilities
        │       ├── CSVDataReader.java   # CSV parser for @DataProvider
        │       └── Config.java          # Configuration file loader
        └── resources/
            ├── config.properties        # Config settings
            ├── testdata/                # CSV test datasets (login-data.csv, customer-data.csv)
            └── screenshots/             # Output directory for screenshot evidence
```

---

## How to Run Tests

### Method 1: Java IDE (Recommended)
1. Open the `/automation` folder in **IntelliJ IDEA**, **Eclipse**, or **VS Code**.
2. Ensure your IDE imports `pom.xml` dependencies.
3. Open `testng.xml` -> **Right-click `testng.xml` -> Run 'testng.xml'** (or **Run As -> TestNG Suite**).

### Method 2: Maven Command Line
```powershell
cd "e:\QA task\automation"
mvn test
```

> **Troubleshooting: `mvn : The term 'mvn' is not recognized`**  
> If your terminal displays this message, Maven is not added to your Windows environment `PATH`.  
> - Use **Method 1 (IDE execution)**, which uses your IDE's built-in Maven bundler.  
> - Or download Apache Maven from [maven.apache.org](https://maven.apache.org/download.cgi) and add `C:\apache-maven\bin` to Windows System Environment Variables -> `Path`.

---

## Test Implementation Summary

### 1. LoginTest (`tests.LoginTest`)
- **Pattern:** Parameterized Data-Driven Test.
- **Input Data:** `src/test/resources/testdata/login-data.csv` (5 dataset rows).
- **Assertions:** Validates dashboard redirect on valid login and error feedback on invalid attempts.

### 2. PunchInTest (`tests.PunchInTest`)
- **Pattern:** End-to-End Functional Test.
- **Execution:** Performs authentication, navigates to Attendance, clicks Punch-In trigger, waits explicitly for toast message (`ExpectedConditions.visibilityOfElementLocated`), and captures screenshot evidence.

### 3. AddCustomerTest (`tests.AddCustomerTest`)
- **Pattern:** Parameterized Data-Driven Test.
- **Input Data:** `src/test/resources/testdata/customer-data.csv` (3 customer records).
- **Assertions:** Fills customer details, submits form, and validates creation feedback.
