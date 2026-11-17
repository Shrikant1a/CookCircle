# Software Requirements Specification (SRS)
## CookCircle: Housing Society Cooking Service Management Platform

---

### 1. Introduction

#### 1.1 Purpose
This document provides a comprehensive Software Requirements Specification (SRS) for **CookCircle** (Society Cooking, Simplified). It defines the high-level architecture, user personas, functional and non-functional requirements, data boundaries, and acceptance criteria required to build a multi-tenant, hyperlocal residential cooking management and pooling system.

#### 1.2 Scope
CookCircle centralizes and digitizes cooking services across gated housing societies and high-rise apartment towers. The platform manages:
- **Societies, Towers & Flats:** Spatial taxonomy, resident-apartment mappings, and tenant isolation.
- **Cook Onboarding & Scheduling:** Primary cook allocations, shift management, dish proficiencies, and verification badges (Aadhaar & Police Verification).
- **Hyperlocal Cook Pooling & Booking:** Consecutive 45-minute slots (Morning: 7:00 AM – 10:00 AM; Evening: 6:30 PM – 10:15 PM) allowing 4–5 flats per tower to pool a single cook, saving ~70% in monthly costs.
- **Leave Management & Automated Standby Cook Dispatch:** Cook leave submission, admin approval, automated conflict identification, and 1-click standby cook assignment ("Simulate Primary Cook Leave" with Zero Meal Disruption).
- **Attendance & OTP Verification:** 4-digit cook check-in OTP and real-time cooking countdown timer.
- **Monthly Invoicing & Payments:** Razorpay Test Mode with backend signature verification, wallet credits for skipped meals, and downloadable payment receipts.
- **Vernacular Cook Interface:** English and Hindi (हिंदी) language toggling for domestic cooks with visual icon-driven navigation.

#### 1.3 Target Audience & Stakeholders
1. **Super Administrators:** Platform owners overseeing all registered housing societies, platform metrics, and administrative governance.
2. **Society Administrators / Facility Managers:** On-site management committee members handling tower pools, cook assignments, leave approvals, backup dispatches, and billing.
3. **Residents:** Flat owners and tenants who book cooking slots, track daily meal preparations, skip meals for wallet credits, and pay monthly subscriptions.
4. **Domestic Cooks (Primary & Standby/Float):** Culinary partners executing daily meal sessions across flats in a single tower, logging attendance, and requesting leaves.

---

### 2. Overall Description

#### 2.1 Product Perspective
CookCircle functions as a multi-tenant Web Application. The frontend is built with React.js (Vite, Tailwind CSS, Lucide React), consuming RESTful APIs exposed by a Spring Boot 3 backend backed by a relational MySQL 8 database.

```
+----------------------------------------------------------------------------+
|                         CookCircle Web Application                         |
+---------------------+-------------------+------------------+---------------+
|     Super Admin     |   Society Admin   |     Resident     |     Cook      |
|      Dashboard      |     Dashboard     |    Experience    |  Partner App  |
+---------------------+-------------------+------------------+---------------+
                                       |
                           HTTPS REST APIs (JSON / JWT)
                                       v
+----------------------------------------------------------------------------+
|                     Spring Boot 3.x Backend Service                        |
|  - Security & JWT Filter           - Cook Pooling & Slot Engine            |
|  - Multi-Tenant Society Isolation  - Standby Backup Dispatch Simulator     |
|  - Monthly Invoicing & Billing     - Razorpay Verification Service         |
+----------------------------------------------------------------------------+
                                       |
                               Spring Data JPA
                                       v
+----------------------------------------------------------------------------+
|                             MySQL 8 Database                               |
|   (18 Relational Tables, Foreign Key Constraints, Financial Precision)     |
+----------------------------------------------------------------------------+
```

#### 2.2 User Characteristics & Role Matrix Overview

| Persona | Technical Fluency | Primary Device | Key Tasks |
|---|---|---|---|
| **Super Admin** | High | Desktop | Provision societies, assign society admins, monitor platform health. |
| **Society Admin** | Medium | Desktop / Tablet | Manage towers & flats, approve leaves, dispatch backup cooks, view MRR. |
| **Resident** | Medium-High | Mobile / Desktop | Join tower pool, select meal preferences, get arrival OTP, skip meals. |
| **Cook Partner** | Low-Medium | Mobile (Vernacular) | View tower route, enter 4-digit OTP, track daily earnings & bonuses. |

---

### 3. Functional Requirements (FR)

#### Module 1: Authentication & Access Control
- **FR-01.1:** The system shall support secure authentication using email/phone and BCrypt-hashed passwords.
- **FR-01.2:** The system shall issue signed JWT access tokens containing user ID, role, and assigned society scope.
- **FR-01.3:** Role-Based Access Control (RBAC) must be strictly enforced at the API layer using Spring Security annotations (`@PreAuthorize`).
- **FR-01.4:** Super Admins can register Society Admins; public registration shall only allow Resident and Cook onboarding upon society verification.

#### Module 2: Society, Tower & Apartment Management
- **FR-02.1:** Super Admins can create societies with name, registration code, address, city, state, postal code, and billing rules.
- **FR-02.2:** Society Admins can configure towers/wings (e.g., Tower A, Tower B, Wing C) and create apartments with floor, flat number, and occupancy status.
- **FR-02.3:** The system must enforce multi-tenant isolation ensuring queries scope to `society_id`.

#### Module 3: Cook Profiles & Background Verification
- **FR-03.1:** Maintain comprehensive cook records: full name, phone number, primary specialty (North Indian, South Indian, Jain, Continental), years of experience, and status (`ACTIVE`, `ON_LEAVE`, `INACTIVE`).
- **FR-03.2:** Maintain verification badges: Aadhaar Verified, Police Verification cleared, and Vaccination Status.
- **FR-03.3:** Categorize cooks into `PRIMARY_COOK` (assigned to specific tower pools) and `BACKUP_FLOAT_COOK` (standby pool for emergency leave replacements).

#### Module 4: Tower Cook Pooling & 45-Minute Slot Engine
- **FR-04.1:** Morning Shift slots: 7:00 AM, 7:45 AM, 8:30 AM, 9:15 AM (45 min each).
- **FR-04.2:** Evening Shift slots: 6:30 PM, 7:15 PM, 8:00 PM, 8:45 PM, 9:30 PM (45 min each).
- **FR-04.3:** Live Tower Pool Visualizer: Show all 5 consecutive slots for a tower wing, indicating occupied vs open slots.
- **FR-04.4:** Pricing Tier Logic: Calculate dynamic per-flat monthly subscription (e.g., ₹4,200/mo when 4–5 flats pool together vs. ₹15,000 for private solo hiring).
- **FR-04.5:** Prevent duplicate or concurrent bookings using database-level unique constraints and Spring transaction isolation (`SERIALIZABLE` or pessimistic locking).

#### Module 5: Resident Meal Management & Checklists
- **FR-05.1:** Allow residents to specify household size (1–2 members vs 3–4 members) and meal preference (Pure Veg, Non-Veg, Jain).
- **FR-05.2:** 60-Minute Pre-Cook Prep Checklist: Show interactive prep reminders (e.g., "Soak dal", "Keep chopped veggies ready", "Atta ready").
- **FR-05.3:** 1-Click "Skip Meal / Pause": Allow resident to pause a day's slot at least 3 hours prior; automatically credit ₹140 back to the resident's wallet.
- **FR-05.4:** 4-Digit Arrival OTP: Generate a secure 4-digit code that resident shares with the cook to initiate the cooking window timer.

#### Module 6: Vernacular Cook Partner App
- **FR-06.1:** Bilingual toggle (English and Hindi) translating navigation, action buttons, and status labels.
- **FR-06.2:** Daily Tower Route Card: Vertical chronological timeline showing flat numbers in sequence within the same tower (zero transit time).
- **FR-06.3:** Check-in Action: Prompt for 4-digit resident OTP to start cooking; display a live 45-minute countdown clock.
- **FR-06.4:** Earnings & Bonus Tracker: Display monthly accumulated earnings (e.g., ₹19,500), completed flats, and zero-leave performance bonus bar (+₹1,000).

#### Module 7: Leave Management & Automated Backup Dispatch
- **FR-07.1:** Cook can submit a leave request specifying start date, end date, and reason.
- **FR-07.2:** When leave is approved by Society Admin, the system identifies all affected tower slots across the date range.
- **FR-07.3:** Backup Matcher: System filters active `BACKUP_FLOAT_COOK` partners without overlapping schedules.
- **FR-07.4:** "Simulate Primary Cook Leave" Action: In the admin dashboard, simulate instant leave and dispatch standby cook (e.g., Ramesh Kumar - Standby Pool).
- **FR-07.5:** Zero Meal Disruption Badge: Instantly update resident and admin views with notification and replacement status.

#### Module 8: Monthly Invoicing, Razorpay & Billing
- **FR-08.1:** Generate monthly itemized invoices for each flat on the 1st of every month (base pooling fee minus wallet skip-meal credits plus any add-ons).
- **FR-08.2:** Support Razorpay Test Mode checkout for card, netbanking, and UPI.
- **FR-08.3:** Server-side signature validation: Compute HMAC-SHA256 (`order_id + "|" + payment_id`) using Razorpay Secret key; never mark invoices paid based on client callbacks alone.
- **FR-08.4:** Allow Society Admins to record authorized offline payments (Cash/Cheque) with receipt number and audit log.

#### Module 9: Notifications, Complaints & Analytics
- **FR-09.1:** In-app real-time notification bell for slot confirmations, arrival OTPs, leave alerts, backup assignments, and invoices.
- **FR-09.2:** Resident Helpdesk / Complaints: File tickets under categories (Taste/Quality, Punctuality, Hygiene, Billing), admin resolution notes, and status updates.
- **FR-09.3:** Operations & Financial Reports: Tower pool occupancy %, Monthly Recurring Revenue (MRR), cook utilization rate, and SLA on-time arrival percentage.

---

### 4. Non-Functional Requirements (NFR)

- **NFR-01 (Performance):** Average API response time under 200ms; database queries indexed on `society_id`, `apartment_id`, `cook_id`, and `date`.
- **NFR-02 (Security):** Passwords hashed using BCrypt (cost factor 10); JWT stored in secure storage; Razorpay secret kept server-side; strict CORS policy.
- **NFR-03 (Data Isolation):** Multi-tenant society boundary enforced on all repository queries; cross-tenant access returns HTTP 403 Forbidden.
- **NFR-04 (Usability & Design):** Premium SaaS dark theme (`#0A0A0A` background, `#151515` cards, `#10B981` / `#EA580C` accents), fully responsive across desktop, tablet, and mobile.
- **NFR-05 (Auditability):** Every administrative action (backup cook dispatch, leave approval, offline payment entry) logged in `audit_logs`.

