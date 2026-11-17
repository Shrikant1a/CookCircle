# REST API Specification & Endpoint Documentation
## CookCircle - Backend API Contracts

---

### 1. Global API Standards

- **Base URL:** `/api/v1`
- **Content-Type:** `application/json`
- **Authentication:** Bearer Token in `Authorization` header (`Bearer <JWT_TOKEN>`)
- **Standard Success Response Format:**
  ```json
  {
    "success": true,
    "message": "Operation completed successfully",
    "data": { ... },
    "timestamp": "2026-11-16T14:50:00Z"
  }
  ```
- **Standard Error Response Format:**
  ```json
  {
    "success": false,
    "error": {
      "code": "SLOT_CONFLICT_ERROR",
      "message": "Selected slot is already reserved by another flat",
      "status": 409,
      "details": ["Slot 2 in Tower B has been locked by Flat 701"]
    },
    "timestamp": "2026-11-16T14:50:00Z"
  }
  ```

---

### 2. Authentication & Profile Endpoints (`/api/v1/auth`)

#### 2.1 Login
- **Endpoint:** `POST /api/v1/auth/login`
- **Access:** Public
- **Request Body:**
  ```json
  {
    "email": "sunita.admin@cookcircle.com",
    "password": "Password@123"
  }
  ```
- **Response (200 OK):**
  ```json
  {
    "success": true,
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "user": {
        "id": 1,
        "email": "sunita.admin@cookcircle.com",
        "fullName": "Sunita Patil",
        "role": "ROLE_SOCIETY_ADMIN",
        "societyId": 1,
        "societyName": "Hiranandani Gardens"
      }
    }
  }
  ```

#### 2.2 Register Resident
- **Endpoint:** `POST /api/v1/auth/register-resident`
- **Access:** Public
- **Request Body:**
  ```json
  {
    "fullName": "Aarav Sharma",
    "email": "aarav.sharma@example.com",
    "phone": "+919876543210",
    "password": "Password@123",
    "societyId": 1,
    "towerId": 1,
    "flatNumber": "402",
    "dietaryPref": "Vegetarian",
    "householdSize": "3-4 members"
  }
  ```

---

### 3. Society & Tower Management Endpoints (`/api/v1/societies`, `/api/v1/towers`)

#### 3.1 List Societies
- **Endpoint:** `GET /api/v1/societies`
- **Access:** Public / Super Admin
- **Response (200 OK):** Array of societies with active towers and pooling rates.

#### 3.2 Get Towers by Society
- **Endpoint:** `GET /api/v1/societies/{societyId}/towers`
- **Access:** Authenticated
- **Response (200 OK):** Tower list (`Tower B`, `Tower 4`, `Wing C`) with flat counts and active pools.

---

### 4. Cook & Verification Endpoints (`/api/v1/cooks`)

#### 4.1 List Cooks
- **Endpoint:** `GET /api/v1/cooks?societyId=1`
- **Access:** Society Admin / Resident
- **Response (200 OK):**
  ```json
  {
    "success": true,
    "data": [
      {
        "id": 101,
        "fullName": "Sunita Devi",
        "type": "PRIMARY_COOK",
        "rating": 4.9,
        "experienceYears": 6,
        "specialties": "North Indian, Jain, Gujarati",
        "assignedTower": "Tower B",
        "aadhaarVerified": true,
        "policeVerified": true,
        "vaccinationStatus": "Double Vaccinated",
        "status": "ACTIVE"
      },
      {
        "id": 102,
        "fullName": "Ramesh Kumar",
        "type": "BACKUP_FLOAT_COOK",
        "rating": 4.85,
        "experienceYears": 8,
        "specialties": "North Indian, South Indian, Maharashtrian",
        "assignedTower": "Standby Pool",
        "aadhaarVerified": true,
        "policeVerified": true,
        "vaccinationStatus": "Double Vaccinated",
        "status": "ACTIVE"
      }
    ]
  }
  ```

---

### 5. Cook Pooling & 45-Minute Slot Engine (`/api/v1/slots`, `/api/v1/pools`)

#### 5.1 Get Tower Live Pool Timeline
- **Endpoint:** `GET /api/v1/pools/tower/{towerId}?shift=EVENING&date=2026-11-16`
- **Access:** Authenticated
- **Response (200 OK):**
  ```json
  {
    "success": true,
    "data": {
      "towerId": 1,
      "towerName": "Tower B",
      "shift": "EVENING",
      "shiftTiming": "6:30 PM - 10:15 PM",
      "primaryCook": {
        "id": 101,
        "fullName": "Sunita Devi",
        "rating": 4.9,
        "badge": "Aadhaar & Police Verified"
      },
      "pricing": {
        "pooledMonthlyRate": 4200.00,
        "soloMarketRate": 15000.00,
        "savingsPercentage": 72
      },
      "slots": [
        {
          "slotId": 201,
          "slotOrder": 1,
          "startTime": "18:30:00",
          "endTime": "19:15:00",
          "displayTime": "6:30 PM - 7:15 PM",
          "isBooked": true,
          "bookedByFlat": "402",
          "status": "CONFIRMED"
        },
        {
          "slotId": 202,
          "slotOrder": 2,
          "startTime": "19:15:00",
          "endTime": "20:00:00",
          "displayTime": "7:15 PM - 8:00 PM",
          "isBooked": true,
          "bookedByFlat": "701",
          "status": "CONFIRMED"
        },
        {
          "slotId": 203,
          "slotOrder": 3,
          "startTime": "20:00:00",
          "endTime": "20:45:00",
          "displayTime": "8:00 PM - 8:45 PM",
          "isBooked": false,
          "bookedByFlat": null,
          "status": "AVAILABLE"
        },
        {
          "slotId": 204,
          "slotOrder": 4,
          "startTime": "20:45:00",
          "endTime": "21:30:00",
          "displayTime": "8:45 PM - 9:30 PM",
          "isBooked": false,
          "bookedByFlat": null,
          "status": "AVAILABLE"
        },
        {
          "slotId": 205,
          "slotOrder": 5,
          "startTime": "21:30:00",
          "endTime": "20:15:00",
          "displayTime": "9:30 PM - 10:15 PM",
          "isBooked": false,
          "bookedByFlat": null,
          "status": "AVAILABLE"
        }
      ]
    }
  }
  ```

#### 5.2 Join Pool / Reserve Slot
- **Endpoint:** `POST /api/v1/bookings`
- **Access:** Resident
- **Request Body:**
  ```json
  {
    "slotId": 203,
    "bookingDate": "2026-11-16",
    "apartmentId": 3,
    "flatNumber": "904",
    "householdSize": "1-2 members",
    "dietaryPref": "Pure Veg",
    "dishesPlanned": "Dal Tadka + Aloo Gobhi + 4 Phulkas"
  }
  ```

#### 5.3 1-Click "Skip Meal / Pause"
- **Endpoint:** `POST /api/v1/bookings/{bookingId}/skip`
- **Access:** Resident
- **Response (200 OK):**
  ```json
  {
    "success": true,
    "message": "Meal slot skipped successfully. ₹140.00 credited to your CookCircle wallet.",
    "data": {
      "bookingId": 501,
      "status": "SKIPPED",
      "creditedAmount": 140.00,
      "updatedWalletBalance": 280.00
    }
  }
  ```

---

### 6. Cook Partner Session & OTP Endpoints (`/api/v1/sessions`)

#### 6.1 Start Cooking Session (OTP Verification)
- **Endpoint:** `POST /api/v1/sessions/{bookingId}/start-otp`
- **Access:** Cook
- **Request Body:**
  ```json
  {
    "otp": "4829"
  }
  ```
- **Response (200 OK):**
  ```json
  {
    "success": true,
    "message": "OTP verified. 45-minute cooking session started.",
    "data": {
      "bookingId": 501,
      "status": "COOKING",
      "startTime": "2026-11-16T18:31:04Z",
      "remainingSeconds": 2700
    }
  }
  ```

#### 6.2 Complete Cooking Session
- **Endpoint:** `POST /api/v1/sessions/{bookingId}/complete`
- **Access:** Cook
- **Response (200 OK):** Session marked `COMPLETED`, attendance logged as `ON_TIME`.

---

### 7. Leave & Automated Backup Dispatch Simulator (`/api/v1/leaves`, `/api/v1/backup-dispatch`)

#### 7.1 Submit Leave Request
- **Endpoint:** `POST /api/v1/leaves`
- **Access:** Cook
- **Request Body:**
  ```json
  {
    "startDate": "2026-11-17",
    "endDate": "2026-11-17",
    "reason": "Family emergency"
  }
  ```

#### 7.2 Simulate Primary Cook Leave & Dispatch Standby Cook
- **Endpoint:** `POST /api/v1/backup-dispatch/simulate-leave`
- **Access:** Society Admin
- **Request Body:**
  ```json
  {
    "primaryCookId": 101,
    "towerId": 1,
    "date": "2026-11-16"
  }
  ```
- **Response (200 OK):**
  ```json
  {
    "success": true,
    "message": "Primary cook leave simulated. Standby cook Ramesh Kumar assigned to all 5 slots of Tower B.",
    "data": {
      "backupCook": {
        "id": 102,
        "fullName": "Ramesh Kumar",
        "rating": 4.85,
        "type": "BACKUP_FLOAT_COOK"
      },
      "affectedSlotsCount": 5,
      "disruptionStatus": "ZERO_MEAL_DISRUPTION",
      "badge": "Backup Cook Assigned — Zero Meal Disruption"
    }
  }
  ```

---

### 8. Monthly Invoices & Razorpay Test Payment (`/api/v1/invoices`, `/api/v1/payments`)

#### 8.1 Create Razorpay Order
- **Endpoint:** `POST /api/v1/payments/create-order`
- **Access:** Resident
- **Request Body:**
  ```json
  {
    "invoiceId": 901
  }
  ```
- **Response (200 OK):**
  ```json
  {
    "success": true,
    "data": {
      "orderId": "order_RzpTest987123",
      "amount": 392000,
      "currency": "INR",
      "keyId": "rzp_test_CookCircleKey"
    }
  }
  ```

#### 8.2 Verify Razorpay Signature
- **Endpoint:** `POST /api/v1/payments/verify-signature`
- **Access:** Resident
- **Request Body:**
  ```json
  {
    "invoiceId": 901,
    "razorpayOrderId": "order_RzpTest987123",
    "razorpayPaymentId": "pay_RzpPay456789",
    "razorpaySignature": "a3b5c7d8e9f0..."
  }
  ```
- **Response (200 OK):**
  ```json
  {
    "success": true,
    "message": "Payment verified and recorded successfully",
    "data": {
      "invoiceNumber": "INV-2026-11-HG-402",
      "status": "PAID",
      "amountPaid": 3920.00,
      "receiptUrl": "/api/v1/invoices/901/receipt.pdf"
    }
  }
  ```

---

### 9. Operations, Metrics & Reports (`/api/v1/reports`)

#### 9.1 Society Operations KPI
- **Endpoint:** `GET /api/v1/reports/kpis?societyId=1`
- **Access:** Society Admin
- **Response (200 OK):**
  ```json
  {
    "success": true,
    "data": {
      "activeTowerPools": 8,
      "monthlyRecurringRevenue": 168000.00,
      "cookUtilizationRate": 94.5,
      "slaOnTimePercentage": 98.2,
      "totalFlatsSubscribed": 40,
      "standbyCooksReady": 2
    }
  }
  ```

