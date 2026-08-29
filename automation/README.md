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

## Quick Start & Setup

### Prerequisites
- Java JDK 17+
- Apache Maven 3.8+
- Google Chrome Browser (`WebDriverManager` automatically provisions the matching ChromeDriver)

### Setup Credentials
Configure your target credentials in `src/test/resources/config.properties`:
```properties
valid.email=your_registered_email@domain.com
valid.password=your_password
```

---

## How to Run Tests

### Option A: Command Line (Maven CLI)
```bash
cd automation
mvn test
```

> **Troubleshooting: `mvn : The term 'mvn' is not recognized`**  
> This error occurs when Apache Maven is not added to your Windows system `PATH` environment variable.  
> **Fixes:**  
> 1. Add Apache Maven's `bin` folder path (e.g. `C:\apache-maven\bin`) to system Environment Variables -> System Variables -> `Path`. Restart terminal.  
> 2. Or execute the test suite directly from your IDE (Option B below).

### Option B: Java IDE (IntelliJ IDEA / Eclipse / VS Code)
1. Open the `/automation` folder in your IDE.
2. Ensure the IDE imports `pom.xml` dependencies.
3. Open `testng.xml` -> Right-click -> Select **Run 'testng.xml'** (or **Run As -> TestNG Suite**).

### Run Individual Test Suites via Maven
```bash
# Execute Login Tests
mvn test -Dtest=LoginTest

# Execute Punch-In Test
mvn test -Dtest=PunchInTest

# Execute Add Customer Tests
mvn test -Dtest=AddCustomerTest
```

### Viewing Execution Report
After execution, open the TestNG report in any browser:
`automation/target/surefire-reports/index.html`

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

---

## Design Choices

| Architectural Choice | Rationale |
|---|---|
| **Page Object Model (POM)** | Decouples UI locators from test logic for maintainability. |
| **WebDriverManager** | Eliminates manual ChromeDriver executable downloads and version mismatch issues. |
| **Explicit Synchronization** | Replaces hardcoded sleeps with `WebDriverWait` for dynamic page loading. |
| **CSV Data Providers** | Enables scalable data-driven testing without hardcoding test datasets in Java. |
