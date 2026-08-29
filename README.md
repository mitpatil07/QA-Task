# FieldForceConnect QA Assignment

> **Target site:** [https://test.fieldforceconnect.com/](https://test.fieldforceconnect.com/)  
> **Scope:** Automation · Manual Testing · API Testing

---

## Repository Structure

```
QA task/
├── automation/              ← Part 1: Java + Maven + Selenium + TestNG project
│   ├── pom.xml
│   ├── testng.xml
│   ├── README.md            ← Automation setup & execution guide
│   └── src/
│       └── test/
│           ├── java/
│           │   ├── pages/   ← Page Object Model classes (BasePage, LoginPage, AttendancePage, CustomerPage)
│           │   ├── tests/   ← TestNG test classes (LoginTest, PunchInTest, AddCustomerTest)
│           │   └── utils/   ← CSVDataReader, Config
│           └── resources/
│               ├── config.properties
│               ├── testdata/
│               │   ├── login-data.csv    ← Data-driven login records
│               │   └── customer-data.csv ← Customer records
│               └── screenshots/          ← Execution evidence
│
├── manual-testing/          ← Part 2: Manual test suite & bug reports
│   └── manual-test-cases.xlsx
│       ├── Sheet: Sign Up           (12 test cases)
│       ├── Sheet: Forgot Password   (11 test cases)
│       ├── Sheet: Sign in with OTP  (10 test cases)
│       ├── Sheet: Login             (14 test cases)
│       └── Sheet: Bug Report        (Logged defects)
│
├── postman/                 ← Part 3: API collection + environment
│   ├── FieldForceConnect.postman_collection.json
│   ├── FieldForceConnect.postman_environment.json
│   └── README.md            ← Postman run guide
│
└── README.md                ← This document
```

---

## Part 1 — Automation (Selenium + TestNG)

### Pre-requisites
- Java 17+ (`java -version`)
- Maven 3.8+ (`mvn -version`)
- Google Chrome (latest)

### Setup & Run
1. Configure credentials in `automation/src/test/resources/config.properties`
2. Execute tests:
```bash
cd automation
mvn test
```

### View Execution Report
```
automation/target/surefire-reports/index.html
```

### Test Suite Overview
| Test Class | Type | Description |
|---|---|---|
| `LoginTest` | Data-driven | Validates valid & invalid login paths |
| `PunchInTest` | E2E | Executes Punch-In action and captures toast evidence |
| `AddCustomerTest` | Data-driven | Verifies creation of customer records |

---

## Part 2 — Manual Test Cases

File: `manual-testing/manual-test-cases.xlsx`

| Sheet | Cases | Scope |
|---|---|---|
| Sign Up | 12 | Positive, negative, field-level validation, UI/UX |
| Forgot Password | 11 | OTP flow, expiry, cooldown, field validation |
| Sign in with OTP | 10 | Valid/invalid OTP, expiry, resend |
| Login | 14 | Valid, invalid, empty fields, session, UI/UX |
| Bug Report | 1 | Logged defects from live application |

---

## Part 3 — Postman API Tests

### Setup & Execution
1. Import `postman/FieldForceConnect.postman_environment.json` into Postman.
2. Import `postman/FieldForceConnect.postman_collection.json`.
3. Configure `valid_email` and `valid_password` variables in the environment.
4. Run collection using Postman Collection Runner or Newman CLI:
```bash
newman run postman/FieldForceConnect.postman_collection.json -e postman/FieldForceConnect.postman_environment.json
```

### Capturing & Verified Endpoints
- **Login API:** `POST /api/account/authenticate`
- **User Profile API:** `GET /api/V1/UserBasicInfo`
- **Add Customer API:** `POST /api/CRM/Lead`
- **Check-In API:** `POST /api/CheckIn`
