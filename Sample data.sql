-- ============================================================
--  Hostel Management System — Sample Data
--  Course: CSC-104L | UET Lahore | IST 2nd-A | 2026
--  Teacher: Sir Rashad Mahmood Khan
--  Project: DBS26IST2ndAF001
-- ============================================================

USE hms_db;

-- ============================================================
-- DEPARTMENTS
-- ============================================================
INSERT INTO departments (dept_name, faculty) VALUES
('Computer Engineering',        'Faculty of Engineering'),
('Electrical Engineering',      'Faculty of Engineering'),
('Mechanical Engineering',      'Faculty of Engineering'),
('Civil Engineering',           'Faculty of Engineering'),
('Software Engineering',        'Faculty of Engineering'),
('Information Technology',      'Faculty of Computing'),
('Computer Science',            'Faculty of Computing'),
('Mathematics',                 'Faculty of Sciences'),
('Physics',                     'Faculty of Sciences'),
('Business Administration',     'Faculty of Management');

-- ============================================================
-- BLOCKS
-- ============================================================
INSERT INTO blocks (block_name, total_rooms, warden_id) VALUES
('Block A', 20, NULL),
('Block B', 20, NULL),
('Block C', 15, NULL),
('Block D', 15, NULL),
('Block E', 10, NULL);

-- ============================================================
-- ROOMS
-- ============================================================
-- Block A (block_id = 1)
INSERT INTO rooms (room_number, block_id, floor, room_type, capacity, monthly_rent, ac_status, bathroom_attached, status) VALUES
('A-101', 1, 1, 'Single', 1, 8000.00,  'AC',     'Yes', 'Occupied'),
('A-102', 1, 1, 'Double', 2, 5000.00,  'Non-AC', 'No',  'Occupied'),
('A-103', 1, 1, 'Double', 2, 5000.00,  'Non-AC', 'No',  'Available'),
('A-104', 1, 1, 'Triple', 3, 3500.00,  'Non-AC', 'No',  'Available'),
('A-201', 1, 2, 'Single', 1, 8000.00,  'AC',     'Yes', 'Available'),
('A-202', 1, 2, 'Double', 2, 5500.00,  'AC',     'No',  'Occupied'),
('A-203', 1, 2, 'Triple', 3, 3500.00,  'Non-AC', 'No',  'Maintenance'),
('A-204', 1, 2, 'Double', 2, 5000.00,  'Non-AC', 'No',  'Available'),
-- Block B (block_id = 2)
('B-101', 2, 1, 'Single', 1, 7500.00,  'AC',     'Yes', 'Occupied'),
('B-102', 2, 1, 'Double', 2, 4800.00,  'Non-AC', 'No',  'Available'),
('B-103', 2, 1, 'Triple', 3, 3200.00,  'Non-AC', 'No',  'Occupied'),
('B-201', 2, 2, 'Single', 1, 7500.00,  'AC',     'Yes', 'Available'),
('B-202', 2, 2, 'Double', 2, 4800.00,  'Non-AC', 'No',  'Occupied'),
('B-203', 2, 2, 'Triple', 3, 3200.00,  'Non-AC', 'No',  'Available'),
-- Block C (block_id = 3)
('C-101', 3, 1, 'Double', 2, 5200.00,  'AC',     'Yes', 'Occupied'),
('C-102', 3, 1, 'Double', 2, 5200.00,  'AC',     'Yes', 'Available'),
('C-103', 3, 1, 'Triple', 3, 3800.00,  'Non-AC', 'No',  'Occupied'),
-- Block D (block_id = 4)
('D-101', 4, 1, 'Single', 1, 9000.00,  'AC',     'Yes', 'Available'),
('D-102', 4, 1, 'Double', 2, 6000.00,  'AC',     'Yes', 'Occupied'),
-- Block E (block_id = 5)
('E-101', 5, 1, 'Triple', 3, 3000.00,  'Non-AC', 'No',  'Available');

-- ============================================================
-- STAFF
-- ============================================================
INSERT INTO staff (full_name, cnic, designation, salary, contact_number, joining_date, block_assigned) VALUES
('Muhammad Tariq',      '35201-1234567-1', 'Warden',     45000.00, '0300-1234567', '2020-03-01', 1),
('Ahmed Raza',          '35201-2345678-2', 'Warden',     45000.00, '0301-2345678', '2019-07-15', 2),
('Sajid Mehmood',       '35201-3456789-3', 'Warden',     45000.00, '0302-3456789', '2021-01-10', 3),
('Khalid Hussain',      '35201-4567890-4', 'Security',   25000.00, '0303-4567890', '2018-06-20', NULL),
('Nadeem Akhtar',       '35201-5678901-5', 'Security',   25000.00, '0304-5678901', '2022-02-14', NULL),
('Rana Imtiaz',         '35201-6789012-6', 'Cleaner',    18000.00, '0305-6789012', '2020-09-05', 1),
('Ghulam Mustafa',      '35201-7890123-7', 'Cleaner',    18000.00, '0306-7890123', '2021-04-22', 2),
('Asif Ali',            '35201-8901234-8', 'Mess Staff', 20000.00, '0307-8901234', '2019-11-30', NULL),
('Zahoor Ahmed',        '35201-9012345-9', 'Mess Staff', 20000.00, '0308-9012345', '2020-05-18', NULL),
('Bilal Anwar',         '35201-0123456-0', 'Accountant', 40000.00, '0309-0123456', '2018-01-01', NULL);

-- Update block wardens
UPDATE blocks SET warden_id = 1 WHERE block_id = 1;
UPDATE blocks SET warden_id = 2 WHERE block_id = 2;
UPDATE blocks SET warden_id = 3 WHERE block_id = 3;

-- ============================================================
-- STUDENTS
-- ============================================================
INSERT INTO students (full_name, father_name, cnic, roll_number, dept_id, program, semester, contact_number, emergency_contact, allotment_type, status) VALUES
('Ali Hassan',          'Hassan Ahmed',       '35201-1111111-1', 'UET-2021-CE-001', 1, 'B.E Computer Engineering',    5, '0311-1111111', '0321-1111111', 'Boarder',     'Active'),
('Usman Farooq',        'Farooq Ahmed',       '35201-2222222-2', 'UET-2022-EE-001', 2, 'B.E Electrical Engineering',  3, '0312-2222222', '0322-2222222', 'Boarder',     'Active'),
('Hamza Malik',         'Malik Irfan',        '35201-3333333-3', 'UET-2021-SE-001', 5, 'B.E Software Engineering',    5, '0313-3333333', '0323-3333333', 'Boarder',     'Active'),
('Zain ul Abideen',     'Abideen Khan',       '35201-4444444-4', 'UET-2023-CS-001', 7, 'B.S Computer Science',        1, '0314-4444444', '0324-4444444', 'Boarder',     'Active'),
('Bilal Chaudhry',      'Chaudhry Nasir',     '35201-5555555-5', 'UET-2022-IT-001', 6, 'B.S Information Technology',  3, '0315-5555555', '0325-5555555', 'Boarder',     'Active'),
('Faisal Rehman',       'Rehman Gul',         '35201-6666666-6', 'UET-2021-ME-001', 3, 'B.E Mechanical Engineering',  5, '0316-6666666', '0326-6666666', 'Boarder',     'Active'),
('Omer Sheikh',         'Sheikh Tariq',       '35201-7777777-7', 'UET-2020-CE-001', 1, 'B.E Computer Engineering',    7, '0317-7777777', '0327-7777777', 'Boarder',     'Active'),
('Saad Butt',           'Butt Aslam',         '35201-8888888-8', 'UET-2023-EE-001', 2, 'B.E Electrical Engineering',  1, '0318-8888888', '0328-8888888', 'Boarder',     'Active'),
('Talha Nawaz',         'Nawaz Sharif',       '35201-9999999-9', 'UET-2022-CE-002', 1, 'B.E Computer Engineering',    3, '0319-9999999', '0329-9999999', 'Boarder',     'Active'),
('Awais Iqbal',         'Iqbal Hussain',      '35201-0000000-0', 'UET-2021-IT-001', 6, 'B.S Information Technology',  5, '0310-0000000', '0330-0000000', 'Boarder',     'Active'),
('Waqas Ahmad',         'Ahmad Riaz',         '35201-1212121-1', 'UET-2020-SE-001', 5, 'B.E Software Engineering',    7, '0311-1212121', '0321-1212121', 'Boarder',     'Departed'),
('Kamran Siddiqui',     'Siddiqui Babar',     '35201-2323232-2', 'UET-2023-ME-001', 3, 'B.E Mechanical Engineering',  1, '0312-2323232', '0322-2323232', 'Boarder',     'Active');

-- ============================================================
-- USERS (passwords are BCrypt hashed — shown as plain for demo)
-- ============================================================
INSERT INTO users (username, password_hash, role, staff_id, student_id) VALUES
('admin',       'admin123',   'Admin',       1,    NULL),
('warden_a',    '$2b$10$wardenahashedpassword',  'Warden',      1,    NULL),
('warden_b',    '$2b$10$wardenbhashedpassword',  'Warden',      2,    NULL),
('accountant',  '$2b$10$accountanthashedpwd',    'Accountant',  10,   NULL),
('security1',   '$2b$10$security1hashedpwd',     'Security',    4,    NULL),
('ali.hassan',  '$2b$10$alihashedpassword',      'Student',     NULL, 1),
('usman.f',     '$2b$10$usmanhashedpassword',    'Student',     NULL, 2),
('hamza.m',     '$2b$10$hamzahashedpassword',    'Student',     NULL, 3);

-- ============================================================
-- ROOM ALLOCATIONS
-- ============================================================
INSERT INTO room_allocations (student_id, room_id, allocation_date, is_active, allocated_by) VALUES
(1,  1,  '2024-09-01', TRUE,  1),  -- Ali Hassan → A-101
(2,  9,  '2024-09-01', TRUE,  1),  -- Usman Farooq → B-101
(3,  2,  '2024-09-02', TRUE,  1),  -- Hamza Malik → A-102
(4,  11, '2024-09-05', TRUE,  2),  -- Zain → B-103
(5,  6,  '2024-09-03', TRUE,  1),  -- Bilal → A-202
(6,  13, '2024-09-01', TRUE,  2),  -- Faisal → B-202
(7,  15, '2024-09-01', TRUE,  3),  -- Omer → C-101
(8,  17, '2024-09-06', TRUE,  3),  -- Saad → C-103
(9,  19, '2024-09-04', TRUE,  1),  -- Talha → D-102
(10, 15, '2024-09-02', TRUE,  3),  -- Awais → C-101
(11, 3,  '2023-09-01', FALSE, 1);  -- Waqas (departed)

-- ============================================================
-- FEE STRUCTURE
-- ============================================================
INSERT INTO fee_structure (fee_type, room_type, amount, late_fine_per_day, effective_from, is_active) VALUES
('Hostel Rent',       'Single', 8000.00, 100.00, '2024-09-01', TRUE),
('Hostel Rent',       'Double', 5000.00, 50.00,  '2024-09-01', TRUE),
('Hostel Rent',       'Triple', 3500.00, 30.00,  '2024-09-01', TRUE),
('Security Deposit',  'All',    5000.00, 0.00,   '2024-09-01', TRUE),
('Mess Charges',      'All',    3000.00, 50.00,  '2024-09-01', TRUE),
('Utility Charges',   'All',    500.00,  0.00,   '2024-09-01', TRUE),
('Late Fine',         'All',    50.00,   50.00,  '2024-09-01', TRUE);

-- ============================================================
-- FEE BILLS
-- ============================================================
INSERT INTO fee_bills (student_id, bill_month, amount_due, due_date, amount_paid, status) VALUES
(1,  '2025-01', 8000.00, '2025-01-10', 8000.00, 'Paid'),
(1,  '2025-02', 8000.00, '2025-02-10', 8000.00, 'Paid'),
(1,  '2025-03', 8000.00, '2025-03-10', 4000.00, 'Partial'),
(2,  '2025-01', 7500.00, '2025-01-10', 7500.00, 'Paid'),
(2,  '2025-02', 7500.00, '2025-02-10', 0.00,    'Unpaid'),
(3,  '2025-01', 5000.00, '2025-01-10', 5000.00, 'Paid'),
(3,  '2025-02', 5000.00, '2025-02-10', 5000.00, 'Paid'),
(3,  '2025-03', 5000.00, '2025-03-10', 0.00,    'Unpaid'),
(4,  '2025-01', 3200.00, '2025-01-10', 3200.00, 'Paid'),
(5,  '2025-01', 5500.00, '2025-01-10', 0.00,    'Unpaid'),
(6,  '2025-01', 4800.00, '2025-01-10', 4800.00, 'Paid'),
(7,  '2025-01', 5200.00, '2025-01-10', 5200.00, 'Paid'),
(8,  '2025-01', 3800.00, '2025-01-10', 3800.00, 'Paid');

-- ============================================================
-- FEE PAYMENTS
-- ============================================================
INSERT INTO fee_payments (bill_id, amount_paid, payment_date, payment_method, receipt_number, received_by) VALUES
(1,  8000.00, '2026-01-05', 'Bank Transfer', 'RCP-2025-0001', 10),
(2,  8000.00, '2026-02-07', 'Cash',          'RCP-2025-0002', 10),
(3,  4000.00, '2026-03-06', 'Online',        'RCP-2025-0003', 10),
(4,  7500.00, '2026-01-08', 'Cash',          'RCP-2025-0004', 10),
(6,  5000.00, '2026-01-04', 'Bank Transfer', 'RCP-2025-0005', 10),
(7,  5000.00, '2026-02-09', 'Online',        'RCP-2025-0006', 10),
(9,  3200.00, '2026-01-06', 'Cash',          'RCP-2025-0007', 10),
(11, 4800.00, '2026-01-07', 'Bank Transfer', 'RCP-2025-0008', 10),
(12, 5200.00, '2026-01-03', 'Cash',          'RCP-2025-0009', 10),
(13, 3800.00, '2026-01-09', 'Online',        'RCP-2025-0010', 10);

-- ============================================================
-- VISITORS
-- ============================================================
INSERT INTO visitors (visitor_name, cnic, relation, student_id, purpose, entry_time, exit_time, logged_by) VALUES
('Hassan Ahmed',    '35201-9876543-1', 'Father',  1, 'Family visit',          '2025-03-10 10:00:00', '2025-03-10 13:00:00', 4),
('Farooq Khan',     '35201-8765432-2', 'Father',  2, 'Bring monthly items',   '2025-03-12 11:00:00', '2025-03-12 14:30:00', 4),
('Irfan Malik',     '35201-7654321-3', 'Uncle',   3, 'Document delivery',     '2025-03-14 09:30:00', '2025-03-14 10:30:00', 5),
('Sara Hassan',     '35201-6543210-4', 'Sister',  1, 'Personal visit',        '2025-03-15 15:00:00', '2025-03-15 17:00:00', 4),
('Khan Sb',         '35201-5432109-5', 'Guardian',4, 'Fee payment discussion','2025-03-16 10:00:00', '2025-03-16 11:30:00', 5),
('Riaz Ahmed',      '35201-4321098-6', 'Father',  6, 'Family visit',          '2025-03-18 12:00:00', '2025-03-18 15:00:00', 4),
('Tariq Butt',      '35201-3210987-7', 'Father',  8, 'Bring luggage',         '2025-03-20 09:00:00', '2025-03-20 11:00:00', 5),
('Asma Farooq',     '35201-2109876-8', 'Mother',  2, 'Family visit',          '2025-03-22 14:00:00', NULL,                   4);

-- ============================================================
-- COMPLAINTS
-- ============================================================
INSERT INTO complaints (student_id, assigned_to, category, description, priority, status, filed_date, resolved_date) VALUES
(1,  6,    'Electrical', 'Room fan not working since 3 days.',          'High',   'Resolved',     '2025-02-10', '2025-02-12'),
(2,  NULL, 'Plumbing',   'Water leakage from bathroom tap.',            'Medium', 'In Progress',  '2025-03-01', NULL),
(3,  7,    'Cleanliness','Common area not cleaned for a week.',         'Low',    'Resolved',     '2025-02-20', '2025-02-22'),
(4,  NULL, 'Noise',      'Room neighbors making noise after midnight.', 'High',   'Pending',      '2025-03-10', NULL),
(5,  6,    'Electrical', 'Power socket not working in room.',           'Medium', 'Resolved',     '2025-02-28', '2025-03-02'),
(6,  NULL, 'Food',       'Mess food quality has declined this month.',  'Medium', 'Pending',      '2025-03-12', NULL),
(7,  NULL, 'Security',   'Unauthorized person seen near block entrance.','High',  'In Progress',  '2025-03-15', NULL),
(8,  7,    'Cleanliness','Washrooms not cleaned regularly.',            'Medium', 'Pending',      '2025-03-18', NULL);

-- ============================================================
-- MAINTENANCE REQUESTS
-- ============================================================
INSERT INTO maintenance_requests (location, issue_type, description, reported_by, assigned_to, status, reported_date, completed_date, cost) VALUES
('A-203',    'Plumbing',   'Pipe burst in bathroom, water flooding.', 1, 4, 'Completed', '2025-01-15', '2025-01-17', 2500.00),
('Block B',  'Electrical', 'Main corridor lights not working.',       2, 5, 'Completed', '2025-02-01', '2025-02-03', 1800.00),
('Block C',  'Painting',   'Walls need repainting — paint peeling.',  3, 6, 'In Progress','2025-03-01', NULL,         NULL),
('A-101',    'Carpentry',  'Room door hinge broken.',                 1, 7, 'Completed', '2025-02-10', '2025-02-11', 500.00),
('Generator','Electrical', 'Backup generator needs servicing.',       1, NULL,'Open',     '2025-03-15', NULL,         NULL);

-- ============================================================
-- MEAL PLANS
-- ============================================================
INSERT INTO meal_plans (plan_name, description, monthly_cost) VALUES
('Full Board',  '3 meals per day — breakfast, lunch, dinner', 3000.00),
('Half Board',  '2 meals per day — lunch and dinner',         2000.00),
('No Mess',     'No mess facility — student arranges own food', 0.00);

-- ============================================================
-- STUDENT MEAL PLANS
-- ============================================================
INSERT INTO student_meal_plans (student_id, plan_id, semester, start_date, is_active) VALUES
(1,  1, 'Fall 2024',   '2024-09-01', TRUE),
(2,  2, 'Fall 2024',   '2024-09-01', TRUE),
(3,  1, 'Fall 2024',   '2024-09-01', TRUE),
(4,  3, 'Fall 2024',   '2024-09-01', TRUE),
(5,  2, 'Fall 2024',   '2024-09-01', TRUE),
(6,  1, 'Fall 2024',   '2024-09-01', TRUE),
(7,  1, 'Fall 2024',   '2024-09-01', TRUE),
(8,  2, 'Fall 2024',   '2024-09-01', TRUE),
(9,  3, 'Fall 2024',   '2024-09-01', TRUE),
(10, 1, 'Fall 2024',   '2024-09-01', TRUE);

-- ============================================================
-- NOTICES
-- ============================================================
INSERT INTO notices (title, description, posted_by, post_date, expiry_date, target_audience, is_active) VALUES
('Hostel Fee Due Date Reminder',
 'All students are reminded to pay their hostel fee before 10th of every month to avoid late fines.',
 1, '2025-03-01', '2026-03-10', 'All', TRUE),
('Water Supply Interruption — Block A',
 'Water supply in Block A will be interrupted on 20th March 2026 from 8AM to 2PM for maintenance work.',
 1, '2025-03-18', '2026-03-21', 'Block A', TRUE),
('Hostel Cleanliness Drive',
 'A cleanliness drive will be conducted on 25th March. All students are required to keep their rooms tidy.',
 2, '2025-03-20', '2026-03-26', 'All', TRUE),
('Curfew Timing Update',
 'New hostel curfew timing is 10:30 PM effective immediately. Students must be inside by this time.',
 1, '2025-02-01', '2026-06-30', 'All', TRUE),
('Mess Menu Change',
 'New mess menu for March 2025 has been displayed on the notice board outside the mess hall.',
 3, '2025-03-01', '2026-03-31', 'All', TRUE);

-- ============================================================
-- AUDIT LOGS (sample entries)
-- ============================================================
INSERT INTO audit_logs (log_time, module, action, error_message, user_id) VALUES
('2026-03-01 09:00:00', 'Auth',        'User [admin] logged in successfully.',              NULL, 1),
('2026-03-01 09:15:00', 'Students',    'New student [Ali Hassan] registered.',              NULL, 1),
('2026-03-02 10:00:00', 'Rooms',       'Room A-101 allocated to student_id=1.',             NULL, 1),
('2026-03-05 11:30:00', 'Fee',         'Bill generated for student_id=1, month=2025-03.',  NULL, 4),
('2026-03-06 08:00:00', 'Auth',        'Failed login attempt for username: unknown_user.',  'Invalid credentials', NULL),
('2026-03-10 14:00:00', 'Complaints',  'Complaint #1 status changed from [Pending] to [Resolved].', NULL, 1),
('2026-03-15 09:00:00', 'Maintenance', 'Maintenance request #5 created — Generator issue.', NULL, 1);

-- ============================================================
-- END OF SAMPLE DATA
-- ============================================================





