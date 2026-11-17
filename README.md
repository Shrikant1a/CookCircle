# CookCircle - Society Cooking, Simplified

[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-brightgreen.svg)]()
[![React](https://img.shields.io/badge/React-18.3.1-blue.svg)]()
[![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

> **CookCircle** is a production-grade, multi-tenant SaaS platform that modernizes and digitizes domestic cooking services across residential apartment towers and gated communities. It eliminates scheduling conflicts, automates pooled 45-minute meal slots, coordinates verified standby backup cooks during leave, and streamlines monthly billing with Razorpay.

---

## 🌟 Key Highlights

* **🏢 Multi-Tenant Housing Society Isolation:** Complete tenant isolation across housing societies, towers, wings, and apartments with composite unique constraints (`uk_society_tower`, `uk_tower_flat`).
* **⏱️ Hyperlocal 45-Minute Slot Pooling:** Synchronized 45-minute cooking slots (Morning 7:00 AM – 10:45 AM & Evening 6:30 PM – 10:15 PM) reducing resident costs by ~72% (₹4,200/mo vs ₹15,000 solo domestic cook).
* **🛡️ Zero Meal Disruption Guarantee (Automated Standby Dispatch):** When a primary cook takes leave, the automated dispatch engine reassigns affected tower flats to standby float cooks (Ramesh Kumar), retaining full audit trails.
* **📱 Bilingual Vernacular Interface for Cook Partners:** Live English & Hindi vernacular toggle with daily route cards, 4-digit resident arrival OTP verification, 45-minute live cooking timer, and 1-tap leave applications.
* **💳 Integrated Billing & Razorpay Test Gateway:** Automated monthly invoicing (₹4,200 base fee minus ₹140/skip wallet credits), Razorpay order creation, cryptographic HMAC-SHA256 signature verification, and offline cash/cheque recording.
* **🔒 Enterprise Security:** Spring Security 6 with stateless JWT authentication, BCrypt password hashing, role-based method authorization (`@PreAuthorize`), and preflight CORS support.

---

## 🛠️ Technology Stack

| Layer | Technologies |
|---|---|
| **Frontend** | React 18, Vite, Tailwind CSS, Lucide React Icons, Context API |
| **Backend** | Java 17+, Spring Boot 3.3.0, Spring Security 6, Spring Data JPA, Hibernate, Maven 3.9 |
| **Database** | MySQL 8.0+ (18 normalized tables, Flyway migrations `V1__init_schema.sql` & `V2__seed_data.sql`) |
| **Security** | Stateless JWT (HS512), BCrypt password encryption, CORS preflight handler |
| **Payment Gateway** | Razorpay Test Mode with server-side HMAC-SHA256 signature verification |
| **DevOps & Containers** | Docker, Docker Compose, Multi-stage Dockerfiles, Nginx Alpine reverse proxy |

---

## 👥 Demo Credentials Matrix

The platform is pre-seeded with verified test accounts across all 4 system roles:

| Role | Email | Password | Persona & Default Assignment |
|---|---|---|---|
| **`RESIDENT`** | `resident@society.com` | `password123` | **Aarav Sharma** (Flat 402, Tower B, Hiranandani Gardens) |
| **`COOK`** | `sunita.devi@cookcircle.com` | `password123` | **Sunita Devi** (Primary Cook, Tower B, 5 Evening Slots) |
| **`SOCIETY_ADMIN`** | `admin@hiranandani.com` | `password123` | **Vikram Malhotra** (Estate Facility Manager, Hiranandani Gardens) |
| **`SUPER_ADMIN`** | `superadmin@cookcircle.com` | `password123` | **Ananya Roy** (Chief Operations Officer, CookCircle Corporate HQ) |

> **Note:** The UI also features a **1-Click Demo Switcher** bar at the top for instant one-click switching between all 4 dashboards without re-typing credentials.

---

## 🚦 12-Phase Development Roadmap

| Phase | Milestone Name | Status | Key Deliverables |
|:---:|---|:---:|---|
| **1** | Requirements & UI Wireframes | **COMPLETED** | SRS, Architecture, ER models, 18 DB tables, REST API contracts in `documentation/` |
| **2** | Frontend Development | **COMPLETED** | 29 responsive pages, 5 live demo views, Tailwind CSS, vernacular toggle, 1,621 modules |
| **3** | MySQL Schema & Spring Boot | **COMPLETED** | Flyway `V1__init_schema.sql` (18 tables), `V2__seed_data.sql`, JPA entities, GlobalExceptionHandler |
| **4** | Authentication & RBAC | **COMPLETED** | Spring Security 6 stateless JWT architecture, login, registration, `/api/v1/auth/*` |
| **5** | Society & Resident Management | **COMPLETED** | Multi-tenant society, tower, apartment boundaries, preferences, unique constraints |
| **6** | Cook Partner Management | **COMPLETED** | Cook onboarding, Aadhaar & police clearance trust badges, tower assignments |
| **7** | Cooking Schedules & Booking | **COMPLETED** | 45-min pooling engine, double-booking prevention, 4-digit arrival OTP, 1-click skip (+₹140) |
| **8** | Attendance, Leave & Backup Dispatch | **COMPLETED** | Punctuality tracking, leave lifecycle, Standby Float Cook auto-dispatch engine |
| **9** | Monthly Invoices & Payments | **COMPLETED** | Invoicing engine, Razorpay test checkout, server-side HMAC-SHA256 signature verification |
| **10** | Notifications & Operational Reports | **COMPLETED** | In-app notifications, complaint resolution, KPI dashboard summary, CSV report exports |
| **11** | Frontend & Backend Integration | **COMPLETED** | Native API client, service modules, state context integration, CORS configuration |
| **12** | Testing, Bug Fixing & Deployment | **COMPLETED** | Multi-stage Dockerfiles, Docker Compose stack, startup scripts, 47/47 passing tests |

---

## 🚀 Quick Start Guide

### Option A: One-Click Docker Deployment (Recommended)

Ensure Docker Desktop is running, then run:

```bash
# Clone repository and launch stack
docker-compose up -d --build
```

Or use the provided one-click launcher scripts:
* **Windows PowerShell:** `.\start.ps1`
* **Linux / macOS:** `./start.sh`

**Services Launched:**
* **Frontend Web App:** [http://localhost:5173](http://localhost:5173) (or [http://localhost](http://localhost))
* **Spring Boot REST API:** [http://localhost:8080/api/v1/societies](http://localhost:8080/api/v1/societies)
* **MySQL 8.0 Database:** `localhost:3306` (Database: `cookcircle_db`, User: `cookcircle_user`)

---

### Option B: Local Standalone Development

#### 1. Prerequisites
* **Java JDK 17 or 18+** (`$env:JAVA_HOME` configured)
* **Node.js 18+ & npm**
* **MySQL 8.0+** running locally on port 3306

#### 2. Start the Backend (Spring Boot 3.3.0)
```powershell
# Using the pre-installed Maven 3.9 in tools:
s:\CookCircle\tools\apache-maven-3.9.6\bin\mvn.cmd spring-boot:run -f backend/pom.xml
```
*Backend will start on `http://localhost:8080`.*

#### 3. Start the Frontend (React + Vite)
```powershell
cd frontend
npm install
npm run dev
```
*Frontend will launch on `http://localhost:5173` with dev-server proxy `/api` $\rightarrow$ `http://localhost:8080`.*

---

## 🧪 Automated Test Execution & Verification

### Run Backend Unit & Integration Tests (47 Tests)
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-18.0.1.1"
s:\CookCircle\tools\apache-maven-3.9.6\bin\mvn.cmd test -f backend/pom.xml
```
```text
Results:
Tests run: 47, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Run Frontend Production Build (1,630 Modules)
```powershell
npm run build --prefix frontend
```
```text
✓ 1630 modules transformed.
dist/index.html                   1.19 kB
dist/assets/index-BzrrBh2e.css   38.11 kB
dist/assets/index-CoGsb3xQ.js   410.86 kB
✓ built in 4.01s
```

### Run Automated End-to-End QA Script
```powershell
powershell -ExecutionPolicy Bypass -File scripts/verify-e2e.ps1
```
```text
Total Verification Checks: 11
Passed:                   11
Failed:                   0
>>> ALL 12 PHASES VERIFIED: PLATFORM PRODUCTION READY <<<
```

---

## 📡 REST API Quick Reference

All REST endpoints follow standard `/api/v1/...` routing:

| Domain | Method & Path | Description | Access |
|---|---|---|---|
| **Auth** | `POST /api/v1/auth/login` | Email/password login returning JWT | Public |
| **Auth** | `POST /api/v1/auth/register-resident` | Register new resident profile | Public |
| **Auth** | `POST /api/v1/auth/register-cook` | Register new cook partner profile | Public |
| **Auth** | `GET /api/v1/auth/me` | Fetch currently authenticated user | Authenticated |
| **Societies** | `GET /api/v1/societies` | List all housing societies | Public |
| **Societies** | `GET /api/v1/societies/{id}/towers` | List towers in a society | Authenticated |
| **Cooks** | `GET /api/v1/cooks` | Directory of verified cooks | Authenticated |
| **Cooks** | `POST /api/v1/societies/{id}/cooks` | Onboard cook partner with trust badges | `SOCIETY_ADMIN` |
| **Slots** | `GET /api/v1/towers/{id}/slots/availability` | 45-min slots availability grid | Authenticated |
| **Bookings** | `POST /api/v1/bookings` | Book slot (generates 4-digit arrival OTP) | `RESIDENT` |
| **Bookings** | `POST /api/v1/bookings/{id}/start-cooking` | Cook OTP check-in & starts 45-min timer | `COOK` |
| **Bookings** | `POST /api/v1/bookings/{id}/complete` | Mark cooking session complete | `COOK` |
| **Bookings** | `POST /api/v1/bookings/{id}/skip-meal` | 1-click skip meal (+₹140 wallet credit) | `RESIDENT` |
| **Leaves** | `POST /api/v1/leaves` | Apply for scheduled leave | `COOK` |
| **Leaves** | `POST /api/v1/leaves/{id}/approve` | Approve leave & auto-dispatch standby cook | `SOCIETY_ADMIN` |
| **Invoices** | `POST /api/v1/invoices/generate` | Generate monthly flat invoices (₹4,200 base) | `SOCIETY_ADMIN` |
| **Payments** | `POST /api/v1/payments/razorpay/create-order` | Create Razorpay payment order in paise | `RESIDENT` |
| **Payments** | `POST /api/v1/payments/razorpay/verify` | HMAC-SHA256 signature verification | `RESIDENT` |
| **Payments** | `POST /api/v1/payments/offline` | Record authorized cash/cheque receipt | `SOCIETY_ADMIN` |
| **Reports** | `GET /api/v1/reports/dashboard-kpis` | Operational KPIs (MRR, Utilization, SLA) | `SOCIETY_ADMIN` |
| **Reports** | `GET /api/v1/reports/invoices/csv` | Download monthly financial CSV ledger | `SOCIETY_ADMIN` |
| **Reports** | `GET /api/v1/reports/attendance/csv` | Download cook attendance & SLA CSV roster | `SOCIETY_ADMIN` |

---

## 📁 Repository Directory Structure

```text
CookCircle/
├── backend/
│   ├── src/main/java/com/cookcircle/
│   │   ├── config/            # SecurityConfig (CORS & stateless JWT)
│   │   ├── controller/        # 15 REST API Controllers
│   │   ├── dto/               # Type-safe Request and Response DTOs
│   │   ├── entity/            # 18 JPA MySQL Entities
│   │   ├── exception/         # GlobalExceptionHandler & Custom Exceptions
│   │   ├── repository/        # 16 Spring Data JPA Repositories
│   │   ├── security/          # JwtTokenProvider, JwtAuthenticationFilter
│   │   └── service/           # High-performance business logic services
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── db/migration/      # Flyway V1 schema & V2 seed data
│   ├── Dockerfile             # Multi-stage Alpine JRE container
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/        # Navbar, Footer, Sidebar, DemoSwitcher
│   │   ├── context/           # AuthContext, AppContext, ToastContext, LanguageContext
│   │   ├── layouts/           # PublicLayout, DashboardLayout
│   │   ├── pages/
│   │   │   ├── public/        # LandingPage, About, Features, HowItWorks, Login, Register
│   │   │   ├── resident/      # ResidentDashboard, LiveTowerPool, MyBookings, Invoices
│   │   │   ├── cook/          # CookPartnerDashboard (Bilingual), Route, Attendance, Leave
│   │   │   ├── society_admin/ # SocietyAdminDashboard, InvoicesAdmin, Reports, Cooks
│   │   │   └── super_admin/   # SuperAdminDashboard (Platform Governance)
│   │   └── services/          # Central API client & 7 modular service modules
│   ├── Dockerfile             # Multi-stage Nginx Alpine container
│   ├── nginx.conf             # SPA routing & reverse proxy
│   ├── package.json
│   └── vite.config.js         # Port 5173 & dev server proxy
├── documentation/             # Specs: SRS, Architecture, DB, API, Roles, Testing, Deployment
├── scripts/
│   └── verify-e2e.ps1         # 11-point Automated QA verification script
├── docker-compose.yml         # MySQL + Spring Boot + Frontend container stack
├── start.ps1                  # One-click Windows PowerShell launcher
├── start.sh                   # One-click Linux/macOS Bash launcher
└── README.md                  # Comprehensive Documentation & Run Guide
```

---

## 📄 License
This project is licensed under the **MIT License**.
