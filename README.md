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
│   ├── README.md            ← Automation-specific setup and run guide
│   └── src/
│       └── test/
│           ├── java/
│           │   ├── pages/   ← Page Object Model classes
│           │   ├── tests/   ← TestNG test classes (3 tests)
│           │   └── utils/   ← CSVDataReader, Config
│           └── resources/
│               ├── config.properties     ← ⚠️ SET PASSWORD HERE
│               ├── testdata/
│               │   ├── login-data.csv    ← 5 login credential rows
│               │   └── customer-data.csv ← 3 customer records
│               └── screenshots/          ← Toast screenshot evidence
│
├── manual-testing/          ← Part 2: Manual test cases
│   └── manual-test-cases.xlsx
│       ├── Sheet: Sign Up           (12 test cases)
│       ├── Sheet: Forgot Password   (11 test cases)
│       ├── Sheet: Sign in with OTP  (10 test cases)
│       ├── Sheet: Login             (14 test cases)
│       └── Sheet: Bug Report        (1 bug logged from live site)
│
├── postman/                 ← Part 3: API collection + environment
│   ├── FieldForceConnect.postman_collection.json
│   ├── FieldForceConnect.postman_environment.json
│   └── README.md            ← Postman-specific run guide
│
└── README.md                ← This file
```

---

## Part 1 — Automation (Selenium + TestNG)

### Pre-requisites
- Java 17+ (`java -version`)
- Maven 3.8+ (`mvn -version`)
- Google Chrome (latest)

### Setup
1. Open `automation/src/test/resources/config.properties`
2. Replace `YOUR_PASSWORD_HERE` with the real password for `mitesh8767@gmail.com`
3. Do the same in `automation/src/test/resources/testdata/login-data.csv` (rows 1–3)

### Run
```bash
cd automation
mvn test
```

### View Report
```
automation/target/surefire-reports/index.html
```

### Tests
| Test Class | Type | Assertions |
|---|---|---|
| `LoginTest` | Data-driven (5 rows) | Dashboard reached (valid) or error shown (invalid) |
| `PunchInTest` | Single | Toast text non-empty + screenshot saved |
| `AddCustomerTest` | Data-driven (3 rows) | Success message or customer in list |

See [`automation/README.md`](automation/README.md) for full details.

---

## Part 2 — Manual Test Cases

File: `manual-testing/manual-test-cases.xlsx`

| Sheet | Cases | Coverage |
|---|---|---|
| Sign Up | 12 | Positive, negative, field-level validation, UI/UX |
| Forgot Password | 11 | OTP flow, expiry, cooldown, empty fields |
| Sign in with OTP | 10 | Valid/invalid OTP, expiry, resend, field limits |
| Login | 14 | Valid, invalid, empty fields, session, UI/UX |
| Bug Report | 1 | Real bug logged from live site exploration |

**Columns:** Test Case ID, Module, Test Scenario, Precondition, Test Steps, Test Data, Expected Result, Actual Result, Status, Priority

---

## Part 3 — Postman API Tests

### Setup
1. Open Postman
2. Import `postman/FieldForceConnect.postman_environment.json` (update `valid_password`)
3. Import `postman/FieldForceConnect.postman_collection.json`
4. Select the `FieldForceConnect` environment

### Requests (run in this order)
1. **POST Login — Valid** → captures `{{token}}` automatically
2. **GET Dashboard/Profile** → uses `{{token}}`
3. **POST Add Customer** → uses `{{token}}`
4. **POST Login — Invalid** → independent, asserts 4xx error

### ⚠️ Before Running
The API endpoint paths are best-guess estimates. Capture the real endpoints:
1. Open Chrome DevTools → Network → XHR/Fetch filter
2. Perform Login + Add Customer on the live site
3. Note the real Request URLs and update the collection

See [`postman/README.md`](postman/README.md) for Newman CLI instructions.

---

## ⚠️ One-Time Setup Checklist

Before evaluating this project, update these files:

| File | What to Update |
|---|---|
| `automation/src/test/resources/config.properties` | `valid.password=` |
| `automation/src/test/resources/testdata/login-data.csv` | Rows 1–3: `YOUR_PASSWORD_HERE` |
| `postman/FieldForceConnect.postman_environment.json` | `valid_password` value |
| Postman collection request URLs | Actual API endpoints from DevTools |
| `automation/src/test/java/pages/AttendancePage.java` | `EXPECTED_PUNCH_IN_TOAST` constant |


