# QA Assignment Submission — FieldForceConnect

**Target Application Under Test:** [https://test.fieldforceconnect.com/](https://test.fieldforceconnect.com/)  
**Submission Scope:** UI Automation (Selenium + TestNG) | Manual Test Cases & Bug Reports | Postman API Testing Collection

---

## Executive Summary for Evaluators

This repository contains a complete QA solution structured across three distinct modules:

1. **Automation (`/automation`)**: A Java 17 + Selenium WebDriver + TestNG Maven project built on the Page Object Model (POM) design pattern. Implements data-driven testing for Login and Add Customer flows using CSV data providers, explicit waits (`WebDriverWait`), and automated screenshot capture for Punch-In toast verification.
2. **Manual Testing (`/manual-testing`)**: An Excel workbook (`manual-test-cases.xlsx`) detailing test cases across Sign Up, Forgot Password, Sign in with OTP, and Login modules, complete with field validation rules and live site bug reports.
3. **API Testing (`/postman`)**: A Postman collection and environment with identified endpoints (`/api/account/authenticate`, `/api/CRM/Lead`, `/api/V1/UserBasicInfo`), payload structures, authentication token management, and collection-level pre-request validation scripts.

---

## Repository Layout

```
QA task/
├── automation/                      # UI Automation Project (Selenium + TestNG)
│   ├── pom.xml                      # Maven dependencies & build setup
│   ├── testng.xml                   # TestNG suite runner
│   ├── README.md                    # Detailed automation guide
│   └── src/
│       └── test/
│           ├── java/
│           │   ├── pages/           # Page Object Model (BasePage, LoginPage, AttendancePage, CustomerPage)
│           │   ├── tests/           # Test Suites (LoginTest, PunchInTest, AddCustomerTest)
│           │   └── utils/           # Data utilities (CSVDataReader, Config)
│           └── resources/
│               ├── config.properties# Test configuration settings
│               ├── testdata/        # CSV data files (login-data.csv, customer-data.csv)
│               └── screenshots/     # Punch-In toast screenshot evidence
│
├── manual-testing/                  # Manual Test Cases & Defect Reports
│   └── manual-test-cases.xlsx       # Multi-tab test suite & bug log
│
├── postman/                         # API Automation & Verification
│   ├── FieldForceConnect.postman_collection.json   # Requests, scripts & fail-fast checks
│   ├── FieldForceConnect.postman_environment.json  # Environment variables template
│   └── README.md                    # Postman setup & Newman execution guide
│
└── README.md                        # Master evaluation guide
```

---

## 1. Automation Suite (Selenium WebDriver + TestNG)

### Framework Highlights
- **Architecture:** Page Object Model (POM) separating page element locators from test logic.
- **Data-Driven:** Parameterized tests reading inputs dynamically from CSV files via OpenCSV parser.
- **Synchronization:** Zero `Thread.sleep()` calls; uses `WebDriverWait` and `ExpectedConditions`.
- **Driver Management:** Automatic Chrome binary management via `WebDriverManager`.

### Prerequisites
- Java JDK 17 or higher (`java -version`)
- Apache Maven 3.8+ (`mvn -version`)
- Google Chrome Browser

### How to Run Tests

#### Option A: Command Line (Maven CLI)
1. Navigate to the automation directory:
   ```bash
   cd automation
   ```
2. Set your credentials in `src/test/resources/config.properties`.
3. Execute tests:
   ```bash
   mvn test
   ```
4. View the TestNG report at `automation/target/surefire-reports/index.html`.

> **Note on `mvn : The term 'mvn' is not recognized`:**  
> If your terminal displays this message, Maven binary is not added to your system `PATH` variable. You can either:  
> 1. Add Apache Maven `bin` directory to environment PATH (e.g. `C:\apache-maven\bin`), OR  
> 2. Run the suite directly from your IDE as shown in Option B below.

#### Option B: Java IDE (IntelliJ IDEA / Eclipse / VS Code)
1. Open the `/automation` directory in your IDE as a Maven project.
2. Allow Maven to import dependencies automatically from `pom.xml`.
3. Right-click `testng.xml` -> click **Run 'testng.xml'** (or **Run As -> TestNG Suite**).

---

## 2. Manual Test Cases & Defect Reporting

**File Path:** `manual-testing/manual-test-cases.xlsx`

The manual test suite covers end-to-end scenarios, boundary values, negative inputs, and UI/UX checks across four primary modules:

- **Sheet 1: Sign Up** — 12 test cases covering form fields, validation, and submission.
- **Sheet 2: Forgot Password** — 11 test cases validating OTP request, resend timer, and password reset.
- **Sheet 3: Sign in with OTP** — 10 test cases covering OTP generation, verification, and expiration.
- **Sheet 4: Login** — 14 test cases covering valid credentials, bad inputs, empty states, and session persistence.
- **Sheet 5: Bug Report** — Defects identified during live site exploration formatted with steps to reproduce, expected vs actual results, and severity.

---

## 3. Postman API Testing Suite

### Identified & Verified Endpoints

| Function | Endpoint | Method | Key Body / Auth Details |
|---|---|---|---|
| **Login API** | `/api/account/authenticate` | `POST` | `{"username": "{{valid_email}}", "password": "{{valid_password}}"}` |
| **User Profile API** | `/api/V1/UserBasicInfo` | `GET` | `Authorization: Bearer {{token}}` |
| **Add Customer API** | `/api/CRM/Lead` | `POST` | `{"leadName": "...", "mobile": "...", "email": "..."}` |
| **Check-In API** | `/api/CheckIn` | `POST` | `Authorization: Bearer {{token}}` |

### Pre-Request Script & Safeguards
The Postman collection includes a collection-level pre-request script:
- Automatically verifies whether `{{token}}` is populated before executing authenticated calls (`Profile`, `Add Customer`).
- Fails fast with a clear diagnostic message if the token is missing, ensuring requests do not fail with confusing `401` responses.

### How to Run Postman Collection

1. Open Postman and import `postman/FieldForceConnect.postman_environment.json`.
2. Set your test credentials (`valid_email` and `valid_password`) in the environment.
3. Import `postman/FieldForceConnect.postman_collection.json`.
4. Select the `FieldForceConnect` environment in the top-right dropdown.
5. Execute **POST Login — Valid Credentials** first to populate `{{token}}`, then run remaining requests.
6. Alternatively, execute via Newman CLI:
   ```bash
   newman run postman/FieldForceConnect.postman_collection.json -e postman/FieldForceConnect.postman_environment.json
   ```
