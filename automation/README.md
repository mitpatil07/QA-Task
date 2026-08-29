# FieldForce Connect Automation — README

## Overview
Selenium + TestNG automation suite for [https://test.fieldforceconnect.com/](https://test.fieldforceconnect.com/)

**Test framework:** TestNG · **Language:** Java 17 · **Build:** Maven  
**Browser:** Chrome (auto-managed via WebDriverManager — no manual driver download)  
**Pattern:** Page Object Model (POM)

---

## Project Structure

```
automation/
├── pom.xml                              ← Maven build config + all dependencies
├── testng.xml                           ← Test suite definition (controls run order)
├── README.md                            ← This file
└── src/
    └── test/
        ├── java/
        │   ├── pages/                   ← Page Object classes (POM layer)
        │   │   ├── BasePage.java        ← WebDriver setup/teardown, screenshot util
        │   │   ├── LoginPage.java       ← Login form locators + actions
        │   │   ├── AttendancePage.java  ← Attendance / Punch-In locators + actions
        │   │   └── CustomerPage.java    ← Add Customer form locators + actions
        │   ├── tests/                   ← TestNG test classes
        │   │   ├── LoginTest.java       ← Data-driven login (5 credential sets)
        │   │   ├── PunchInTest.java     ← Toast verification + screenshot
        │   │   └── AddCustomerTest.java ← Data-driven Add Customer (3 records)
        │   └── utils/                   ← Reusable utilities
        │       ├── CSVDataReader.java   ← Reads CSV → Object[][] for @DataProvider
        │       └── Config.java          ← Reads config.properties
        └── resources/
            ├── config.properties        ← ⚠️ UPDATE password here before running
            ├── testdata/
            │   ├── login-data.csv       ← 3 valid + 2 invalid credential sets
            │   └── customer-data.csv    ← 3 customer records for Add Customer test
            └── screenshots/             ← Punch-In toast screenshots saved here
```

---

## Quick Start

### Step 1 — Prerequisites
| Requirement | Version | Check |
|---|---|---|
| Java JDK | 17+ | `java -version` |
| Maven | 3.8+ | `mvn -version` |
| Google Chrome | Latest | — |

> **No ChromeDriver download needed.** WebDriverManager handles it automatically.

### Step 2 — Update Config
Open `src/test/resources/config.properties` and set:
```properties
valid.password=YOUR_ACTUAL_PASSWORD_HERE
```
Also update `src/test/resources/testdata/login-data.csv` — replace `YOUR_PASSWORD_HERE` in the valid rows.

### Step 3 — Run All Tests
```bash
cd automation
mvn test
```

### Step 4 — View Report
After the run, open:
```
target/surefire-reports/index.html
```
in any browser for the TestNG HTML report.

---

## Running Individual Tests

```bash
# Login test only
mvn test -Dtest=LoginTest

# Punch-In test only
mvn test -Dtest=PunchInTest

# Add Customer test only
mvn test -Dtest=AddCustomerTest
```

---

## Test Descriptions

### 1. LoginTest (data-driven)
Reads `login-data.csv` and runs one test invocation per row (5 total):
- **Rows 1-3** (valid): Assert browser reaches `/dashboard`
- **Rows 4-5** (invalid): Assert error message `"Invalid Email Id / Mobile No or Password"` is displayed

Uses `@DataProvider` + `WebDriverWait.until(urlContains(...))` — zero `Thread.sleep()` calls.

### 2. PunchInTest
1. Logs in via `@BeforeMethod`
2. Navigates to `/attendance`
3. Clicks "Add New" (the punch-in trigger)
4. Waits for toast with `ExpectedConditions.visibilityOfElementLocated`
5. Asserts toast text is non-empty
6. Saves PNG screenshot to `src/test/resources/screenshots/`

### 3. AddCustomerTest (data-driven)
Reads `customer-data.csv` and runs once per row (3 total):
1. Logs in via `@BeforeMethod`
2. Navigates to My Customers
3. Clicks Add Customer
4. Fills form (name, phone, email, address, city)
5. Submits and asserts either success toast OR customer name visible in list

---

## Locator Update Guide

Since the site is a **React SPA**, all locators use XPath with placeholder text
(not brittle CSS class names). If a locator fails:

1. Open `https://test.fieldforceconnect.com/auth/login` in Chrome
2. Right-click the failing element → **Inspect**
3. Note the `placeholder`, `name`, or `id` attribute
4. Open the corresponding page class and update the `By.xpath(...)` value

Common files to update:
- **Login fails**: `LoginPage.java` → `EMAIL_FIELD` or `LOGIN_BUTTON`
- **Punch-In fails**: `AttendancePage.java` → `ADD_NEW_BUTTON` or `PUNCH_IN_ICON_IN_ROW`
- **Add Customer fails**: `CustomerPage.java` → `FIELD_*` locators

---

## Design Decisions (Interview Notes)

| Decision | Reason |
|---|---|
| **Page Object Model** | Separates locators from test logic. One locator change = one file edit |
| **WebDriverManager** | Eliminates manual ChromeDriver version management |
| **WebDriverWait only** | `Thread.sleep()` wastes time on fast machines; explicit wait adapts to actual app speed |
| **Config.properties** | Credentials never hard-coded in Java — change env without touching source |
| **CSVDataReader** | OpenCSV handles quoted commas (e.g., "123 Main St, Apt 4") correctly |
| **`@BeforeMethod` inheritance** | TestNG runs parent's `@BeforeMethod` (driver setup) before child's (login), giving each test a clean browser + logged-in state |
