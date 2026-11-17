-- =============================================================================
-- CookCircle Seed Data
-- Initial Master Data for Roles, Societies, Towers, Flats, Cooks & Slots
-- =============================================================================

-- 1. Insert System Roles
INSERT INTO roles (id, name, description) VALUES
(1, 'ROLE_SUPER_ADMIN', 'Platform Administrator with global multi-society permissions'),
(2, 'ROLE_SOCIETY_ADMIN', 'Housing Society Administrator managing local towers and cooks'),
(3, 'ROLE_RESIDENT', 'Society resident booking cooking slots and paying subscriptions'),
(4, 'ROLE_COOK', 'Domestic culinary partner fulfilling flat cooking sessions')
ON DUPLICATE KEY UPDATE description=VALUES(description);

-- 2. Insert Initial Societies
INSERT INTO societies (id, code, name, address_line, city, state, postal_code, base_pooling_fee, skip_meal_credit, status) VALUES
(1, 'HG-POWAI', 'Hiranandani Gardens', 'Central Avenue, Hiranandani Estate, Powai', 'Mumbai', 'Maharashtra', '400076', 4200.00, 140.00, 'ACTIVE'),
(2, 'PSN-WFD', 'Prestige Shantiniketan', 'ITPB Main Road, Whitefield', 'Bengaluru', 'Karnataka', '560048', 4500.00, 150.00, 'ACTIVE'),
(3, 'SDA-PANATUR', 'Sobha Dream Acres', 'Balagere Road, Panathur', 'Bengaluru', 'Karnataka', '560087', 3999.00, 135.00, 'ACTIVE'),
(4, 'APT-HADAPSAR', 'Amanora Park Town', 'Amanora Park Town, Magarpatta Road, Hadapsar', 'Pune', 'Maharashtra', '411028', 4200.00, 140.00, 'ACTIVE'),
(5, 'BR-HINJEWADI', 'Blue Ridge', 'Blue Ridge Township, Hinjewadi Phase 1', 'Pune', 'Maharashtra', '411057', 4100.00, 135.00, 'ACTIVE')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 3. Insert Users (BCrypt hash for 'Password@123': $2a$10$eAccYoNO32OmAr8cb.OPOurshs0iEFlLUC6B37CWTCWbTmn664W3y)
INSERT INTO users (id, email, phone, password_hash, full_name, role_id, status) VALUES
(1, 'superadmin@cookcircle.com', '+919999900001', '$2a$10$eAccYoNO32OmAr8cb.OPOurshs0iEFlLUC6B37CWTCWbTmn664W3y', 'Ananya Roy', 1, 'ACTIVE'),
(2, 'admin.powai@cookcircle.com', '+919999900002', '$2a$10$eAccYoNO32OmAr8cb.OPOurshs0iEFlLUC6B37CWTCWbTmn664W3y', 'Vikram Malhotra', 2, 'ACTIVE'),
(3, 'aarav.sharma@example.com', '+919820148290', '$2a$10$eAccYoNO32OmAr8cb.OPOurshs0iEFlLUC6B37CWTCWbTmn664W3y', 'Aarav Sharma', 3, 'ACTIVE'),
(4, 'pooja.mehta@example.com', '+919820171340', '$2a$10$eAccYoNO32OmAr8cb.OPOurshs0iEFlLUC6B37CWTCWbTmn664W3y', 'Pooja Mehta', 3, 'ACTIVE'),
(5, 'sunita.devi@cookcircle.com', '+919820144521', '$2a$10$eAccYoNO32OmAr8cb.OPOurshs0iEFlLUC6B37CWTCWbTmn664W3y', 'Sunita Devi', 4, 'ACTIVE'),
(6, 'ramesh.kumar@cookcircle.com', '+919819288390', '$2a$10$eAccYoNO32OmAr8cb.OPOurshs0iEFlLUC6B37CWTCWbTmn664W3y', 'Ramesh Kumar', 4, 'ACTIVE'),
(7, 'meena.bai@cookcircle.com', '+919845011928', '$2a$10$eAccYoNO32OmAr8cb.OPOurshs0iEFlLUC6B37CWTCWbTmn664W3y', 'Meena Bai', 4, 'ACTIVE')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

-- 4. Insert Towers
INSERT INTO towers (id, society_id, name, total_floors) VALUES
(101, 1, 'Tower B - Wing 1', 22),
(102, 1, 'Tower B - Wing 2', 22),
(103, 1, 'Tower C - Heritage', 18),
(201, 2, 'Tower 4 - Crescent', 19),
(301, 3, 'Wing C - RainTree', 14),
(401, 4, 'Tower Aspen - Wing A', 26),
(402, 4, 'Tower Gateway - Wing 1', 30),
(501, 5, 'Tower 12 - Pine Crest', 24)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 5. Insert Apartments
INSERT INTO apartments (id, tower_id, flat_number, floor_number, configuration, status) VALUES
(1, 101, '402', 4, '2BHK', 'OCCUPIED'),
(2, 101, '701', 7, '3BHK', 'OCCUPIED'),
(3, 101, '904', 9, '2BHK', 'OCCUPIED'),
(4, 101, '1102', 11, '3BHK', 'OCCUPIED'),
(5, 101, '1403', 14, '2BHK', 'OCCUPIED')
ON DUPLICATE KEY UPDATE flat_number=VALUES(flat_number);

-- 6. Insert Residents
INSERT INTO residents (id, user_id, society_id, apartment_id, household_size, dietary_pref, wallet_balance) VALUES
(1, 3, 1, 1, '3-4 members', 'Pure Veg', 280.00),
(2, 4, 1, 2, '1-2 members', 'Jain', 0.00)
ON DUPLICATE KEY UPDATE wallet_balance=VALUES(wallet_balance);

-- 7. Insert Cooks
INSERT INTO cooks (id, user_id, society_id, type, rating, specialties, experience_years, aadhaar_verified, police_verified, vaccination_status, status) VALUES
(1, 5, 1, 'PRIMARY_COOK', 4.90, 'North Indian, Gujarati, Jain, Maharashtrian', 6, TRUE, TRUE, 'Double Vaccinated (Covishield)', 'ACTIVE'),
(2, 6, 1, 'BACKUP_FLOAT_COOK', 4.85, 'North Indian, South Indian, Dal Baati, Punjabi', 8, TRUE, TRUE, 'Double Vaccinated (Covaxin)', 'ACTIVE'),
(3, 7, 1, 'PRIMARY_COOK', 4.92, 'Kathiyawadi, Marwari, Pure Veg', 7, TRUE, TRUE, 'Double Vaccinated', 'ACTIVE')
ON DUPLICATE KEY UPDATE specialties=VALUES(specialties);

-- 8. Insert Cook Assignments
INSERT INTO cook_assignments (id, cook_id, tower_id, shift, start_date, status) VALUES
(1, 1, 101, 'EVENING', '2026-01-01', 'ACTIVE'),
(2, 3, 102, 'EVENING', '2026-01-01', 'ACTIVE')
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- 9. Insert Cooking Slots (Tower B Evening Shift 6:30 PM - 10:15 PM)
INSERT INTO cooking_slots (id, tower_id, shift, slot_order, start_time, end_time, is_active) VALUES
(201, 101, 'EVENING', 1, '18:30:00', '19:15:00', TRUE),
(202, 101, 'EVENING', 2, '19:15:00', '20:00:00', TRUE),
(203, 101, 'EVENING', 3, '20:00:00', '20:45:00', TRUE),
(204, 101, 'EVENING', 4, '20:45:00', '21:30:00', TRUE),
(205, 101, 'EVENING', 5, '21:30:00', '22:15:00', TRUE)
ON DUPLICATE KEY UPDATE is_active=VALUES(is_active);

-- 10. Insert Sample Active Bookings
INSERT INTO bookings (id, society_id, slot_id, apartment_id, resident_id, primary_cook_id, effective_cook_id, booking_date, dishes_planned, arrival_otp, status, skip_refund_amount) VALUES
(1, 1, 201, 1, 1, 1, 1, '2026-11-16', 'Dal Tadka + Aloo Gobhi + 4 Phulkas', '4829', 'COOKING', 0.00),
(2, 1, 202, 2, 2, 1, 1, '2026-11-16', 'Paneer Bhurji + Jeera Rice + 3 Phulkas', '7134', 'CONFIRMED', 0.00)
ON DUPLICATE KEY UPDATE arrival_otp=VALUES(arrival_otp);

-- 11. Insert Sample Invoices
INSERT INTO invoices (id, invoice_number, society_id, apartment_id, resident_id, billing_month, base_amount, discount_wallet, net_amount, status, due_date, paid_at) VALUES
(1, 'INV-2026-11-HG-402', 1, 1, 1, '2026-11', 4200.00, 280.00, 3920.00, 'PAID', '2026-11-05', '2026-11-03 14:20:00'),
(2, 'INV-2026-12-HG-402', 1, 1, 1, '2026-12', 4200.00, 140.00, 4060.00, 'UNPAID', '2026-12-05', NULL)
ON DUPLICATE KEY UPDATE net_amount=VALUES(net_amount);
