-- =============================================================================
-- CookCircle Database Initialization DDL
-- Multi-Tenant Housing Society Cooking Service Management Platform
-- Database: MySQL 8.0+
-- =============================================================================

-- 1. Roles Table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Users Table (Authentication Identity)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(120) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Societies Table (Multi-Tenant Boundaries)
CREATE TABLE IF NOT EXISTS societies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    address_line VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    base_pooling_fee DECIMAL(10,2) NOT NULL DEFAULT 4200.00,
    skip_meal_credit DECIMAL(10,2) NOT NULL DEFAULT 140.00,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Towers Table (Wings / Buildings)
CREATE TABLE IF NOT EXISTS towers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    society_id BIGINT NOT NULL,
    name VARCHAR(80) NOT NULL,
    total_floors INT NOT NULL DEFAULT 20,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_towers_society FOREIGN KEY (society_id) REFERENCES societies(id) ON DELETE CASCADE,
    UNIQUE KEY uk_society_tower (society_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Apartments Table (Flats)
CREATE TABLE IF NOT EXISTS apartments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tower_id BIGINT NOT NULL,
    flat_number VARCHAR(30) NOT NULL,
    floor_number INT NOT NULL,
    configuration VARCHAR(30) DEFAULT '2BHK',
    status VARCHAR(30) NOT NULL DEFAULT 'OCCUPIED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_apartments_tower FOREIGN KEY (tower_id) REFERENCES towers(id) ON DELETE CASCADE,
    UNIQUE KEY uk_tower_flat (tower_id, flat_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Residents Table
CREATE TABLE IF NOT EXISTS residents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    society_id BIGINT NOT NULL,
    apartment_id BIGINT NOT NULL,
    household_size VARCHAR(30) DEFAULT '3-4 members',
    dietary_pref VARCHAR(40) DEFAULT 'Vegetarian',
    wallet_balance DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_residents_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_residents_society FOREIGN KEY (society_id) REFERENCES societies(id) ON DELETE CASCADE,
    CONSTRAINT fk_residents_apartment FOREIGN KEY (apartment_id) REFERENCES apartments(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Cooks Table
CREATE TABLE IF NOT EXISTS cooks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    society_id BIGINT NOT NULL,
    type VARCHAR(30) NOT NULL, -- PRIMARY_COOK or BACKUP_FLOAT_COOK
    rating DECIMAL(3,2) DEFAULT 4.90,
    specialties VARCHAR(255) NOT NULL,
    experience_years INT NOT NULL DEFAULT 5,
    aadhaar_verified BOOLEAN DEFAULT TRUE,
    police_verified BOOLEAN DEFAULT TRUE,
    vaccination_status VARCHAR(60) DEFAULT 'Double Vaccinated',
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, ON_LEAVE, INACTIVE
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cooks_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_cooks_society FOREIGN KEY (society_id) REFERENCES societies(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Cook Assignments Table
CREATE TABLE IF NOT EXISTS cook_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cook_id BIGINT NOT NULL,
    tower_id BIGINT NOT NULL,
    shift VARCHAR(30) NOT NULL, -- MORNING, EVENING, BOTH
    start_date DATE NOT NULL,
    end_date DATE NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_assignments_cook FOREIGN KEY (cook_id) REFERENCES cooks(id) ON DELETE CASCADE,
    CONSTRAINT fk_assignments_tower FOREIGN KEY (tower_id) REFERENCES towers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. Cooking Slots Table
CREATE TABLE IF NOT EXISTS cooking_slots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tower_id BIGINT NOT NULL,
    shift VARCHAR(30) NOT NULL, -- MORNING or EVENING
    slot_order INT NOT NULL, -- 1 to 5
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_slots_tower FOREIGN KEY (tower_id) REFERENCES towers(id) ON DELETE CASCADE,
    UNIQUE KEY uk_tower_slot (tower_id, shift, slot_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. Bookings Table (Slot Reservations)
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    society_id BIGINT NOT NULL,
    slot_id BIGINT NOT NULL,
    apartment_id BIGINT NOT NULL,
    resident_id BIGINT NOT NULL,
    primary_cook_id BIGINT NOT NULL,
    effective_cook_id BIGINT NOT NULL,
    booking_date DATE NOT NULL,
    dishes_planned TEXT NULL,
    arrival_otp VARCHAR(6) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'CONFIRMED', -- CONFIRMED, COOKING, COMPLETED, SKIPPED, CANCELLED
    skip_refund_amount DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_society FOREIGN KEY (society_id) REFERENCES societies(id) ON DELETE CASCADE,
    CONSTRAINT fk_bookings_slot FOREIGN KEY (slot_id) REFERENCES cooking_slots(id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_apartment FOREIGN KEY (apartment_id) REFERENCES apartments(id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_resident FOREIGN KEY (resident_id) REFERENCES residents(id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_primary_cook FOREIGN KEY (primary_cook_id) REFERENCES cooks(id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_effective_cook FOREIGN KEY (effective_cook_id) REFERENCES cooks(id) ON DELETE RESTRICT,
    UNIQUE KEY uk_slot_date_status (slot_id, booking_date, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. Attendance Table
CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cook_id BIGINT NOT NULL,
    booking_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    check_in_time DATETIME NULL,
    check_out_time DATETIME NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ON_TIME', -- ON_TIME, DELAYED, ABSENT
    remarks VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attendance_cook FOREIGN KEY (cook_id) REFERENCES cooks(id) ON DELETE CASCADE,
    CONSTRAINT fk_attendance_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. Leave Requests Table
CREATE TABLE IF NOT EXISTS leave_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cook_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason VARCHAR(255) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    action_by BIGINT NULL,
    action_notes VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_leaves_cook FOREIGN KEY (cook_id) REFERENCES cooks(id) ON DELETE CASCADE,
    CONSTRAINT fk_leaves_admin FOREIGN KEY (action_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. Backup Assignments Table
CREATE TABLE IF NOT EXISTS backup_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    leave_request_id BIGINT NOT NULL,
    primary_cook_id BIGINT NOT NULL,
    backup_cook_id BIGINT NOT NULL,
    tower_id BIGINT NOT NULL,
    assignment_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    assigned_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_backup_leave FOREIGN KEY (leave_request_id) REFERENCES leave_requests(id) ON DELETE CASCADE,
    CONSTRAINT fk_backup_primary_cook FOREIGN KEY (primary_cook_id) REFERENCES cooks(id) ON DELETE RESTRICT,
    CONSTRAINT fk_backup_backup_cook FOREIGN KEY (backup_cook_id) REFERENCES cooks(id) ON DELETE RESTRICT,
    CONSTRAINT fk_backup_tower FOREIGN KEY (tower_id) REFERENCES towers(id) ON DELETE CASCADE,
    CONSTRAINT fk_backup_admin FOREIGN KEY (assigned_by) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 14. Invoices Table
CREATE TABLE IF NOT EXISTS invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(60) NOT NULL UNIQUE,
    society_id BIGINT NOT NULL,
    apartment_id BIGINT NOT NULL,
    resident_id BIGINT NOT NULL,
    billing_month VARCHAR(10) NOT NULL, -- e.g. '2026-11'
    base_amount DECIMAL(10,2) NOT NULL,
    discount_wallet DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    net_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'UNPAID', -- UNPAID, PAID, OVERDUE, CANCELLED
    due_date DATE NOT NULL,
    paid_at DATETIME NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_invoices_society FOREIGN KEY (society_id) REFERENCES societies(id) ON DELETE CASCADE,
    CONSTRAINT fk_invoices_apartment FOREIGN KEY (apartment_id) REFERENCES apartments(id) ON DELETE RESTRICT,
    CONSTRAINT fk_invoices_resident FOREIGN KEY (resident_id) REFERENCES residents(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 15. Payments Table (Razorpay & Offline Records)
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    razorpay_order_id VARCHAR(100) NOT NULL UNIQUE,
    razorpay_payment_id VARCHAR(100) NULL,
    razorpay_signature VARCHAR(255) NULL,
    amount DECIMAL(10,2) NOT NULL,
    method VARCHAR(50) DEFAULT 'RAZORPAY_TEST', -- UPI, CARD, NETBANKING, CASH, CHEQUE
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, SUCCESS, FAILED
    verified_at DATETIME NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 16. Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(40) NOT NULL, -- BOOKING, BACKUP, CREDIT, PREP
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 17. Complaints Table
CREATE TABLE IF NOT EXISTS complaints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    resident_id BIGINT NOT NULL,
    category VARCHAR(60) NOT NULL, -- Punctuality, Taste, Hygiene, Billing
    subject VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN', -- OPEN, IN_PROGRESS, RESOLVED
    resolution_notes TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_complaints_resident FOREIGN KEY (resident_id) REFERENCES residents(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 18. Audit Logs Table
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    actor_user_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(60) NOT NULL,
    entity_id BIGINT NOT NULL,
    details TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_user FOREIGN KEY (actor_user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indexes on high-frequency search fields
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_apartments_tower ON apartments(tower_id);
CREATE INDEX idx_residents_society ON residents(society_id);
CREATE INDEX idx_cooks_society ON cooks(society_id);
CREATE INDEX idx_bookings_date ON bookings(booking_date);
CREATE INDEX idx_bookings_resident ON bookings(resident_id);
CREATE INDEX idx_invoices_month ON invoices(billing_month);
CREATE INDEX idx_notifications_user ON notifications(user_id);
