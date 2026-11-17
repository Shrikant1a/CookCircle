# Role Matrix & Security Access Control Specification
## CookCircle - Authentication, Authorization & Multi-Tenant Isolation

---

### 1. Role Definitions

1. `ROLE_SUPER_ADMIN`: CookCircle platform owner with omniscient rights across all housing societies.
2. `ROLE_SOCIETY_ADMIN`: Facility and operations committee managers managing a single housing society's towers, flats, cooks, leaves, backup dispatches, and billing.
3. `ROLE_RESIDENT`: Apartment occupants managing their own cooking slot bookings, meal prep checklists, wallet balances, payments, and complaints.
4. `ROLE_COOK`: Domestic culinary partners who view their daily tower routes, enter arrival OTPs, submit leave requests, and track earnings.
5. `GUEST / PUBLIC`: Unauthenticated visitors accessing the landing page, features, pricing calculator, and login/registration flows.

---

### 2. Comprehensive Permission Matrix

| Feature / Resource | SUPER_ADMIN | SOCIETY_ADMIN | RESIDENT | COOK | GUEST |
|---|:---:|:---:|:---:|:---:|:---:|
| **Public Landing & Pricing** | ✅ | ✅ | ✅ | ✅ | ✅ |
| **User Login & Registration** | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Manage Societies (Create/Edit)** | ✅ | ❌ | ❌ | ❌ | ❌ |
| **Create Society Admins** | ✅ | ❌ | ❌ | ❌ | ❌ |
| **Platform-Wide Audit Logs** | ✅ | ❌ | ❌ | ❌ | ❌ |
| **Tower & Apartment CRUD** | ✅ | ✅ | ❌ | ❌ | ❌ |
| **Cook Onboarding & Verification** | ✅ | ✅ | ❌ | ❌ | ❌ |
| **View Live Tower Pool Timeline** | ✅ | ✅ | ✅ | ✅ | ❌ |
| **Book / Join 45-Min Cooking Slot** | ❌ | ❌ | ✅ (Own Flat) | ❌ | ❌ |
| **1-Click Skip Meal (+Wallet Credit)**| ❌ | ❌ | ✅ (Own Slot) | ❌ | ❌ |
| **View Arrival OTP** | ❌ | ❌ | ✅ (Own Slot) | ❌ | ❌ |
| **Verify Arrival OTP (Start Cook)** | ❌ | ❌ | ❌ | ✅ (Assigned) | ❌ |
| **Complete Cooking Session** | ❌ | ❌ | ❌ | ✅ (Assigned) | ❌ |
| **Submit Leave Request** | ❌ | ❌ | ❌ | ✅ (Own) | ❌ |
| **Approve Leave & Dispatch Backup** | ✅ | ✅ | ❌ | ❌ | ❌ |
| **Trigger Backup Simulator** | ✅ | ✅ | ❌ | ❌ | ❌ |
| **Generate Monthly Invoices** | ✅ | ✅ | ❌ | ❌ | ❌ |
| **Initiate Razorpay Payment** | ❌ | ❌ | ✅ (Own Bill) | ❌ | ❌ |
| **Record Offline Payment** | ✅ | ✅ | ❌ | ❌ | ❌ |
| **File Complaint / Feedback** | ❌ | ❌ | ✅ (Own) | ❌ | ❌ |
| **Resolve Complaint** | ✅ | ✅ | ❌ | ❌ | ❌ |
| **Operational & Financial KPIs** | ✅ | ✅ | ❌ | ❌ | ❌ |

---

### 3. Multi-Tenant Data Isolation Enforcement

- Every protected entity (`towers`, `apartments`, `residents`, `cooks`, `cooking_slots`, `bookings`, `invoices`) maintains a foreign key to `society_id`.
- For `ROLE_SOCIETY_ADMIN`, `ROLE_RESIDENT`, and `ROLE_COOK`, the `society_id` claim in their validated JWT is injected into the database query context.
- Cross-society data access attempts are rejected immediately with HTTP 403 Forbidden.
- The `SUPER_ADMIN` can switch society scope via an explicit request header (`X-Society-Scope: <ID>`).

