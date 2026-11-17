# UI/UX Design System, Wireframes & Navigation Specification
## CookCircle - Web Design Blueprint

---

### 1. Design System & Theme Foundations

CookCircle delivers a top-tier, commercial B2B/B2C SaaS aesthetic blending a sleek dark theme with an Indian consumer-tech sensibility (inspired by the trust, clarity, and speed of Swiggy, Urban Company, and Zepto).

#### 1.1 Color Palette & Token Definitions
| Token Name | Hex Code | Purpose |
|---|---|---|
| **Background Deep** | `#0A0A0A` | Global application backdrop |
| **Surface Card** | `#151515` | Dashboard cards, modals, table headers |
| **Surface Raised** | `#1E1E1E` | Hover states, dropdowns, inputs |
| **Border Subtle** | `#27272A` | Clean card borders, table dividers |
| **Text Primary** | `#F9FAFB` | High-contrast headlines, labels |
| **Text Secondary** | `#9CA3AF` | Muted descriptions, timestamps |
| **Emerald Accent** | `#10B981` | Success states, active pools, verified badges |
| **Saffron / Terracotta** | `#EA580C` | Primary branding CTA, meal highlights, pool savings |
| **Amber Warning** | `#F59E0B` | Pending leaves, low prep checklists |
| **Rose Error** | `#EF4444` | Cancelled slots, payment failed |

#### 1.2 Typography & Spacing
- **Primary Font Family:** Inter, system-ui, sans-serif
- **Heading Styles:** `font-bold tracking-tight text-white`
- **Border Radii:** `rounded-xl` (12px) for cards, `rounded-lg` (8px) for buttons/inputs, `rounded-full` for status badges.
- **Micro-Interactions:** Subtle scale on button hover (`hover:scale-[1.02] active:scale-[0.98] transition-all duration-200`).

---

### 2. Multi-Role Navigation & Layout Hierarchy

```
+-------------------------------------------------------------------------------------------------------+
|  COOKCIRCLE [Logo]   |   [Demo View Switcher: Resident App | Cook Partner | Society Admin | Super Admin] |  [User Profile]  |
+-------------------------------------------------------------------------------------------------------+
|                                                                                                       |
|  [Sidebar Navigation]              |  [Main Dynamic Content Area]                                     |
|  - Overview                        |                                                                  |
|  - Live Tower Pools                |  - KPI Statistics Summary Cards                                  |
|  - Cooking Slots                   |  - Interactive Live Tower Pool Timeline (5 Slots)                |
|  - Attendance & Leaves             |  - Assigned Cook Profile & Verification Badges                   |
|  - Standby Backup Dispatch         |  - Meal Preparation Checklist (60-min prior)                     |
|  - Billing & Invoices              |  - 4-Digit Arrival OTP & Cooking Timer                           |
|  - Settings                        |                                                                  |
+------------------------------------+------------------------------------------------------------------+
```

---

### 3. Wireframe Specifications by Role View

#### 3.1 Public Website Wireframe
- **Header:** Sticky navbar with Logo, links (`About`, `Features`, `How It Works`, `Pricing`, `Contact`), and `Get Started` / `Login` buttons.
- **Hero Section:**
  - Headline: *"Your Society. Your Cook. Your Schedule."*
  - Subheadline: *"Ghar ka khana, shared with your society. Save 70% on daily home cooks with verified tower pools."*
  - CTAs: `Join Your Tower Pool` (Saffron button) & `Explore Society Features` (Border button).
- **Social Proof / Metrics Banner:** *85+ Societies Onboarded*, *4,200+ Meals Cooked Daily*, *99.4% On-Time SLA*, *Zero Disruption Backup Guarantee*.
- **Features Section:** 4-Card Grid detailing (1) Synchronized 45-Min Tower Slots, (2) Standby Cook Backup Dispatch, (3) 4-Digit Arrival OTP Check-in, (4) Automated Monthly Invoicing with Skip Credits.
- **Interactive Pricing Calculator:** Slider showing savings between hiring private cook (₹15,000/mo) vs CookCircle Tower Pool (₹4,200/mo).
- **FAQ Accordion & Clean Footer.**

---

#### 3.2 View 1: Resident App Wireframe
```
+---------------------------------------------------------------------------------------------------+
|  Active Society: [Hiranandani Gardens - Powai]  |  Tower: [Tower B - Wing 1]  |  Flat: [402]      |
+---------------------------------------------------------------------------------------------------+
|                                                                                                   |
|  [Assigned Cook Partner Card]                       |  [Today's Meal & Prep Checklist]            |
|  * Sunita Devi (4.9 ★ - 6 yrs exp)                  |  Dinner Slot: 6:30 PM - 7:15 PM (Slot 1)    |
|  * [Aadhaar Verified] [Police Verified] [Vaccinated]|  Selected: Dal Tadka + Aloo Gobhi + 4 Rotis |
|  * Specialty: North Indian, Jain                    |  Checklist:                                 |
|  * Arrival OTP: [ 4 8 2 9 ] (Share on arrival)      |  [x] Soak Toor Dal 30 mins prior            |
|  * Status: Cooking In Progress (28m remaining)      |  [x] Keep atta kneaded & chopped veggies    |
|  * [ 1-Click Skip Meal / +₹140 Wallet Credit ]      |  [ ] Keep spices & oil on kitchen counter   |
|                                                                                                   |
+---------------------------------------------------------------------------------------------------+
|  LIVE TOWER COOK POOL VISUALIZER (Evening Shift: 6:30 PM - 10:15 PM)                              |
|  Pool Rate: ₹4,200 / flat / month (72% Savings vs Solo Private Cook)                              |
|                                                                                                   |
|  +--------------------+ +--------------------+ +--------------------+ +--------------------+ ... |
|  | Slot 1: 6:30-7:15  | | Slot 2: 7:15-8:00  | | Slot 3: 8:00-8:45  | | Slot 4: 8:45-9:30  |     |
|  | Flat 402 (You)     | | Flat 701           | | [ OPEN SLOT ]      | | [ OPEN SLOT ]      |     |
|  | Status: CONFIRMED  | | Status: CONFIRMED  | | [ Join Pool ]      | | [ Join Pool ]      |     |
|  +--------------------+ +--------------------+ +--------------------+ +--------------------+     |
+---------------------------------------------------------------------------------------------------+
```

---

#### 3.3 View 2: Cook Partner App (Vernacular-Ready) Wireframe
- **Top Bar:** Bilingual toggle `[ English | हिंदी ]` + Cook avatar with current status (`डायलॉग: ऑन ड्यूटी / On Duty`).
- **Earnings & Attendance Snapshot:**
  - This Month Earnings: `₹19,500`
  - Completed Flats Today: `3 of 5`
  - Zero-Leave Bonus Progress: `80% [=====>  ] (+₹1,000 on perfect attendance)`
- **Daily Tower Route Timeline (Tower B):**
  - **Flat 402 (6:30 PM):** `Dal Tadka, Aloo Gobi, Phulkas` | **[ OTP Entered - Finished ]**
  - **Flat 701 (7:15 PM):** `Paneer Bhurji, Jeera Rice, Phulkas` | **[ Start Cooking - Enter OTP ]** (Big Green Action Button)
  - **Flat 904 (8:00 PM):** `Khichdi, Kadhi, Gujarati Subzi` | **[ Upcoming ]**
  - **Flat 1102 (8:45 PM):** `Bhindi Masala, Dal Fry, Phulkas` | **[ Upcoming ]**
- **Action Buttons:**
  - `[ Request Leave / छुट्टी का आवेदन ]`
  - `[ Emergency Support / आपातकालीन सहायता ]`

---

#### 3.4 View 3: Society Admin & Operations Dashboard Wireframe
- **KPI Summary Grid (4 Metric Cards):**
  - Active Tower Pools: `12 Pools`
  - Monthly Recurring Revenue (MRR): `₹2,52,000`
  - Cook Utilization Rate: `96.4%`
  - SLA On-Time Arrival: `99.1%`
- **Automated Standby Cook Dispatch Simulator Panel:**
  - Banner: *"Demonstrate Instant Emergency Coverage"*
  - Primary Cook on Shift: `Sunita Devi (Tower B)`
  - Action Button: `[ Simulate Primary Cook Leave ]`
  - Triggered Modal / Real-Time Workflow:
    - 1. System detects 5 affected evening slots.
    - 2. System queries standby float cooks. Matches `Ramesh Kumar (Standby Pool - 4.85★)`.
    - 3. Reassigns slots and dispatches notifications.
    - 4. Displays verified badge: `Backup Cook Assigned — Zero Meal Disruption`.
- **Tower Directory & Apartment Booking Table:** Filter by tower, floor, meal preference, payment status.
- **Invoicing & Collection Module:** Total billed, received online via Razorpay, pending amounts, offline receipt logging.

---

#### 3.5 View 4: Super Admin Governance Wireframe
- Platform Society Directory: Provision new societies, assign society admin credentials.
- System Audit Logs: Real-time event stream (`BACKUP_DISPATCHED`, `LEAVE_APPROVED`, `PAYMENT_SETTLED`).
- Global revenue analytics and cook pool utilization index.

