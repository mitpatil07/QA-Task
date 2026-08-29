# Postman API Tests — FieldForceConnect

## Overview
This folder contains the Postman collection and environment for API testing of
[https://test.fieldforceconnect.com/](https://test.fieldforceconnect.com/).

---

## Files

| File | Description |
|------|-------------|
| `FieldForceConnect.postman_collection.json` | All API requests with embedded test scripts |
| `FieldForceConnect.postman_environment.json` | Environment variables (base_url, token, credentials) |
| `README.md` | This file |

---

## Environment Variables

| Variable | Value | Set By |
|---|---|---|
| `base_url` | `https://test.fieldforceconnect.com` | Pre-configured |
| `valid_email` | `mitesh8767@gmail.com` | Pre-configured — update if needed |
| `valid_password` | `YOUR_PASSWORD_HERE` | **Set this to real password before running** |
| `token` | _(empty initially)_ | Set automatically by the valid login test script |

---

## Requests

### 1. POST Login — Valid Credentials
**Folder:** Auth  
**Method:** `POST {{base_url}}/api/auth/login`  
**Body:**
```json
{
  "email": "{{valid_email}}",
  "password": "{{valid_password}}"
}
```
**Test scripts:**
- Assert status `200`
- Assert response is JSON
- Extract auth token → `pm.environment.set("token", ...)`

> ⚠️ **Run this request FIRST** — it populates `{{token}}` for all subsequent requests.

---

### 2. POST Login — Invalid Credentials
**Folder:** Auth  
**Method:** `POST {{base_url}}/api/auth/login`  
**Body:** `{ "email": "wronguser@test.com", "password": "WrongPassword123" }`  
**Test scripts:**
- Assert status is `4xx` (400, 401, 403, or 422)
- Assert response contains an error message
- Assert no token is returned

---

### 3. GET Dashboard / Profile (Authenticated)
**Folder:** Dashboard / Profile  
**Method:** `GET {{base_url}}/api/user/profile`  
**Authorization:** `Bearer {{token}}`  
**Test scripts:**
- Assert status `200`
- Assert response is JSON
- Assert response has expected user fields (id, email, or name)

---

### 4. POST Add Customer (Authenticated)
**Folder:** Customers  
**Method:** `POST {{base_url}}/api/customers`  
**Authorization:** `Bearer {{token}}`  
**Body:**
```json
{
  "name": "API Test Customer",
  "phone": "9000000001",
  "email": "api.customer@test.com",
  "address": "123 API Street",
  "city": "Mumbai"
}
```
**Test scripts:**
- Assert status `200` or `201`
- Assert response contains submitted customer name
- Assert not unauthorized (`401`)

---

## ⚠️ Important: Update API Endpoints

The endpoint paths in this collection (`/api/auth/login`, `/api/user/profile`, `/api/customers`) are
**educated guesses** based on common REST API conventions.

**To find the real endpoints:**
1. Open Chrome → go to [https://test.fieldforceconnect.com/auth/login](https://test.fieldforceconnect.com/auth/login)
2. Open DevTools (`F12`) → **Network** tab → filter by **Fetch/XHR**
3. Perform a login action
4. Click the XHR request → note the **Request URL** and **Request Payload**
5. Update the `url.raw` field in each request in the collection JSON

Also update the **test script token path** in request 1 to match your actual response:
```javascript
// In the test script, update this line:
var token = jsonData.token        // if response is { token: "abc" }
         || jsonData.data.token   // if response is { data: { token: "abc" } }
         || jsonData.access_token // if response is { access_token: "abc" }
```

---

## How to Run

### Option A: Postman UI (Recommended)

1. Open Postman
2. **Import Environment:**
   - Click Environments → Import → select `FieldForceConnect.postman_environment.json`
   - Update `valid_password` with your actual password
3. **Import Collection:**
   - Click Collections → Import → select `FieldForceConnect.postman_collection.json`
4. **Select Environment:** Choose `FieldForceConnect` from the environment dropdown (top-right)
5. **Run in order:**
   - First run: **POST Login — Valid Credentials** (captures token)
   - Then run: **GET Dashboard / Profile** (uses token)
   - Then run: **POST Add Customer** (uses token)
   - Run **POST Login — Invalid Credentials** independently (no token needed)

### Option B: Postman Collection Runner

1. Open the collection → click **Run collection**
2. Select environment: `FieldForceConnect`
3. Ensure requests are ordered: Valid Login → GET Profile → Add Customer → Invalid Login
4. Click **Run FieldForceConnect API Tests**

### Option C: Newman (CLI)

```bash
# Install Newman globally
npm install -g newman

# Run the full collection
newman run FieldForceConnect.postman_collection.json \
  --environment FieldForceConnect.postman_environment.json \
  --reporters cli,html \
  --reporter-html-export newman-report.html
```

---

## Notes
- The `token` variable is automatically saved by the test script — no manual copy-paste needed
- If a request returns `401 Unauthorized`, the token has expired; re-run the valid login request
- All test assertions are in the **Tests** tab of each request in Postman
