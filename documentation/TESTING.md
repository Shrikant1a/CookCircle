# Testing Strategy & Acceptance Criteria Catalog
## CookCircle - Quality Assurance Test Suite

---

### 1. Test Strategy Overview

CookCircle incorporates a multi-tiered testing strategy spanning unit tests, integration tests, API verification, and end-to-end user workflows.

```
+-------------------------------------------------------------+
|                End-to-End & Acceptance Tests                |
|      (Booking flow, Backup dispatch, Razorpay payment)      |
+-------------------------------------------------------------+
                              ^
+-------------------------------------------------------------+
|               Controller & API Integration Tests            |
|         (Spring MockMvc, Status Codes, JWT Auth)            |
+-------------------------------------------------------------+
                              ^
+-------------------------------------------------------------+
|                  Service Layer Unit Tests                   |
|        (Conflict checks, Leave matching, Pricing math)      |
+-------------------------------------------------------------+
                              ^
+-------------------------------------------------------------+
|           Database Repository & Constraint Tests            |
|        (Unique slot constraints, Foreign keys, Cascade)     |
+-------------------------------------------------------------+
```

---

### 2. Essential Test Cases Catalog

| Test Case ID | Module | Scenario Tested | Preconditions | Input Data | Expected Result |
|---|---|---|---|---|---|
| **TC-01** | Auth | Valid user login | User registered with BCrypt hash | Valid email & password | HTTP 200, JWT token returned with correct role claims, routes to target dashboard |
| **TC-02** | Auth | Invalid password login | User registered | Valid email, incorrect password | HTTP 401 Unauthorized, generic error message without leaking sensitive details |
| **TC-03** | Auth / RBAC | Resident calls Admin API | Resident authenticated | `GET /api/v1/societies/1/settings` with Resident JWT | HTTP 403 Forbidden, request denied at Spring Security filter |
| **TC-04** | Society | Create Apartment | Society Admin authenticated | Tower B, Flat 802, Floor 8 | Flat created successfully under Tower B and mapped to Society ID |
| **TC-05** | Pooling / Slot | Book available 45-min slot | Resident authenticated, Slot 3 open | `slotId: 203`, `date: 2026-11-16`, `flat: 904` | HTTP 201, booking created, slot marked `CONFIRMED`, 4-digit arrival OTP generated |
| **TC-06** | Concurrency | Duplicate / concurrent slot booking | Slot 3 open | Flat 904 and Flat 905 submit simultaneous booking requests | Exactly one request succeeds with HTTP 201; second request rejected with HTTP 409 Conflict |
| **TC-07** | Cook Operations | Overlapping cook assignment | Cook assigned to Tower B 6:30 PM | Attempt to assign same cook to Tower A at 6:30 PM | Assignment rejected with business validation error |
| **TC-08** | Leave Engine | Submit leave request | Cook authenticated | Start: `2026-11-17`, End: `2026-11-17`, Reason: Medical | Request saved in `leave_requests` with status `PENDING` |
| **TC-09** | Backup Dispatch | Approve leave & identify affected slots | Leave request pending for Sunita Devi | Admin clicks "Approve Leave" | System identifies all 5 evening slots in Tower B as affected; slots remain preserved |
| **TC-10** | Backup Matcher | Assign unavailable backup cook | Standby cook already assigned to Tower C | Attempt to assign to Tower B on same date | Assignment rejected with conflict warning |
| **TC-11** | Billing | Generate monthly invoice | Billing cycle 1st of month | Base fee: ₹4,200, Wallet credit: ₹280 | Invoice created with net amount: ₹3,920, status: `UNPAID` |
| **TC-12** | Payment | Verify invalid Razorpay signature | Invoice `UNPAID`, order generated | Invalid forged HMAC signature | HTTP 400 Bad Request, invoice status remains `UNPAID` |
| **TC-13** | Multi-Tenancy | Cross-society data request | User belongs to Society 1 | Query for Tower in Society 2 | HTTP 403 Forbidden or empty dataset; strict isolation maintained |
| **TC-14** | Notifications | Mark notification as read | Unread notification exists | `PATCH /api/v1/notifications/10/read` | Status updated to `is_read = true`, unread count decremented |
| **TC-15** | Meal Management | 1-Click Skip Meal & Wallet Credit | Booking confirmed for today | Click "Skip Meal" 4 hours prior | Slot marked `SKIPPED`, ₹140 wallet credit awarded, cook notified |
| **TC-16** | OTP Check-in | Start cooking session with OTP | Booking exists, cook on site | Resident provides OTP `4829`, cook submits | OTP matches, cooking countdown starts (45 mins), attendance logged |

