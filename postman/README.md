# Postman API Tests — FieldForceConnect

## Overview
This folder contains the Postman collection and environment for API testing of [https://test.fieldforceconnect.com/](https://test.fieldforceconnect.com/).

---

## Files

| File | Description |
|------|-------------|
| `FieldForceConnect.postman_collection.json` | API collection with verified live endpoints and test scripts |
| `FieldForceConnect.postman_environment.json` | Environment variables (`base_url`, `token`, `valid_email`, `valid_password`) |
| `README.md` | This file |

---

## Environment Variables

| Variable | Value | Set By |
|---|---|---|
| `base_url` | `https://test.fieldforceconnect.com` | Pre-configured |
| `valid_email` | `YOUR_EMAIL_HERE` | Pre-configured |
| `valid_password` | `YOUR_PASSWORD_HERE` | User configuration |
| `token` | _(empty initially)_ | Set automatically by Login test script |

---

## Requests & Verified Endpoints

### 1. POST Login — Valid Credentials
**Folder:** Auth  
**Endpoint:** `POST {{base_url}}/api/account/authenticate`  
**Body:**
```json
{
  "username": "{{valid_email}}",
  "password": "{{valid_password}}"
}
```
**Test scripts:**
- Assert HTTP `200 OK`
- Assert response is JSON
- Capture token → `pm.environment.set("token", ...)`

---

### 2. POST Login — Invalid Credentials
**Folder:** Auth  
**Endpoint:** `POST {{base_url}}/api/account/authenticate`  
**Body:**
```json
{
  "username": "invalid_user@test.com",
  "password": "WrongPassword123"
}
```
**Test scripts:**
- Assert `success: false` or HTTP 4xx status
- Assert no token returned

---

### 3. GET Dashboard / Profile (Authenticated)
**Folder:** Dashboard / Profile  
**Endpoint:** `GET {{base_url}}/api/V1/UserBasicInfo`  
**Header:** `Authorization: Bearer {{token}}`  

---

### 4. POST Add Customer (Authenticated)
**Folder:** Customers  
**Endpoint:** `POST {{base_url}}/api/CRM/Lead`  
**Header:** `Authorization: Bearer {{token}}`  
**Body:**
```json
{
  "leadName": "API Test Customer",
  "mobile": "9876543210",
  "email": "api.customer@test.com",
  "address": "123 Tech Park",
  "city": "Mumbai"
}
```

---

## Collection Pre-Request Script

The collection includes a pre-request script that automatically checks if `{{token}}` is populated before running authenticated requests. If missing, it fails fast with a clear notification instructing the runner to execute **POST Login — Valid Credentials** first.

---

## How to Run

### Option A: Postman UI (Recommended)
1. Import `FieldForceConnect.postman_environment.json` and set `valid_email` / `valid_password`.
2. Import `FieldForceConnect.postman_collection.json`.
3. Select `FieldForceConnect` environment.
4. Run **POST Login — Valid Credentials** first to capture token, then run remaining requests.

### Option B: Newman CLI
```bash
newman run postman/FieldForceConnect.postman_collection.json \
  -e postman/FieldForceConnect.postman_environment.json
```
