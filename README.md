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

---

### How to Run Automation Tests

#### Method 1: Java IDE (Recommended — Works without global Maven PATH setup)
1. Open the `/automation` folder in **IntelliJ IDEA**, **Eclipse**, or **VS Code**.
2. Allow your IDE to load dependencies from `pom.xml`.
3. Open `testng.xml` -> **Right-click `testng.xml` -> Run 'testng.xml'** (or **Run As -> TestNG Suite**).

#### Method 2: Maven Command Line (Requires Maven installed on System PATH)
1. Open terminal and navigate to the automation directory:
   ```powershell
   cd "e:\QA task\automation"
   ```
2. Configure credentials in `src/test/resources/config.properties`.
3. Execute tests:
   ```powershell
   mvn test
   ```
4. View generated report at `automation/target/surefire-reports/index.html`.

> ⚠️ **If PowerShell says `mvn : The term 'mvn' is not recognized`:**  
> This means Apache Maven is not added to your Windows system `PATH` variable.  
> - **Quickest Solution:** Run via Method 1 (IDE `testng.xml` execution).  
> - **To enable `mvn` in terminal:** Download Apache Maven from [maven.apache.org](https://maven.apache.org/download.cgi), extract to `C:\apache-maven`, and add `C:\apache-maven\bin` to Windows Environment Variables -> System Variables -> `Path`.

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
| **Login API** | `/api/account/authenticate` | `POST` | `{"username": "{{valid_username}}", "password": "{{valid_password}}"}` |
| **User Profile API** | `/api/V1/UserBasicInfo` | `GET` | `Authorization: Bearer {{token}}` |
| **Add Customer API** | `/api/CRM/Lead` | `POST` | `{"leadName": "...", "mobile": "...", "email": "..."}` |
| **Check-In API** | `/api/CheckIn` | `POST` | `Authorization: Bearer {{token}}` |

### Pre-Request Script & Safeguards
The Postman collection includes a collection-level pre-request script:
- Automatically verifies whether `{{token}}` is populated before executing authenticated calls (`Profile`, `Add Customer`).
- Fails fast with a clear diagnostic message if the token is missing, ensuring requests do not fail with confusing `401` responses.

### How to Run Postman Collection

1. Open Postman and import `postman/FieldForceConnect.postman_environment.json`.
2. Set your test credentials (`valid_username` and `valid_password`) in the environment.
3. Import `postman/FieldForceConnect.postman_collection.json`.
4. Select the `FieldForceConnect` environment in the top-right dropdown.
5. Execute **POST Login — Valid Credentials** first to populate `{{token}}`, then run remaining requests.
6. Alternatively, execute via Newman CLI:
   ```bash
   newman run postman/FieldForceConnect.postman_collection.json -e postman/FieldForceConnect.postman_environment.json
   ```

### Auth mechanism — observed behavior

The login response's `token` field is returned empty (`"token": ""`); the only populated
JWT-like value is `referralToken`, which by naming appears scoped to referral features rather
than general API auth. In testing, the Profile and Add Customer endpoints returned valid `200`
responses regardless of whether a real bearer token was supplied. This suggests these particular
endpoints do not enforce bearer-token auth on this environment — worth calling out to an
evaluator as an observation about the API's current behavior, rather than assuming the
`Authorization: Bearer {{token}}` header shown above is doing meaningful work.

---

## Credentials

Test account credentials (email + password) are provided to the evaluator separately and are
**not committed to this repository**. `config.properties`, `login-data.csv`, and the Postman
environment file are gitignored; each has a matching `*.example` file in the same folder showing
the expected format with placeholder values. To run this project locally:

```bash
cp automation/src/test/resources/config.properties.example automation/src/test/resources/config.properties
cp automation/src/test/resources/testdata/login-data.csv.example automation/src/test/resources/testdata/login-data.csv
cp postman/FieldForceConnect.postman_environment.example.json postman/FieldForceConnect.postman_environment.json
# then fill in the real email/password in each of the three copied files
```

---

## Verification Status (honest summary)

| Item | Status |
|---|---|
| Login flow (UI automation) | ✅ Locators verified against live site |
| Login flow (API, Postman/Newman) | ✅ Confirmed working — `success: true`, 0 assertion failures |
| Add Customer (API) | ✅ Endpoint confirmed (`/api/CRM/Lead`), returns 200 |
| Add Customer (UI automation) | 🟡 Built, not yet run end-to-end against a fresh customer record |
| Punch-In toast (UI automation) | 🟡 Test asserts a toast appears and is non-empty; the *exact expected text* (`EXPECTED_PUNCH_IN_TOAST`) is an unverified placeholder — the test account was already punched in during exploration, so the real toast was never captured. The test logs a notice rather than failing on a text mismatch. |
| Bearer token auth mechanism | 🟡 Observed that `token` is empty in the login response and authenticated endpoints return 200 without a valid token — see note above |
| Manual test cases | ✅ Complete for all 4 modules |
