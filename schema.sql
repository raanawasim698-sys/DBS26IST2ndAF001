-- ============================================================
--  Hostel Management System — Database Schema
--  Course: CSC-104L | UET Lahore | IST 2nd-A | Spring 2026
--  Teacher: Sir Rashad Mahmood Khan
--  Project: DBS26IST2ndAF001
-- ============================================================

DROP DATABASE IF EXISTS hms_db;
CREATE DATABASE hms_db;
USE hms_db;

-- ============================================================
-- TABLE 1: departments
-- ============================================================
CREATE TABLE departments (
    dept_id       INT AUTO_INCREMENT PRIMARY KEY,
    dept_name     VARCHAR(100) NOT NULL UNIQUE,
    faculty       VARCHAR(100) NOT NULL
);

-- ============================================================
-- TABLE 2: blocks
-- ============================================================
CREATE TABLE blocks (
    block_id      INT AUTO_INCREMENT PRIMARY KEY,
    block_name    VARCHAR(50)  NOT NULL UNIQUE,
    total_rooms   INT          NOT NULL CHECK (total_rooms > 0),
    warden_id     INT          DEFAULT NULL  -- FK added after staff table
);

-- ============================================================
-- TABLE 3: rooms
-- ============================================================
CREATE TABLE rooms (
    room_id          INT AUTO_INCREMENT PRIMARY KEY,
    room_number      VARCHAR(10)    NOT NULL,
    block_id         INT            NOT NULL,
    floor            INT            NOT NULL CHECK (floor >= 0),
    room_type        ENUM('Single','Double','Triple') NOT NULL,
    capacity         INT            NOT NULL CHECK (capacity BETWEEN 1 AND 6),
    monthly_rent     DECIMAL(10,2)  NOT NULL CHECK (monthly_rent > 0),
    ac_status        ENUM('AC','Non-AC') NOT NULL DEFAULT 'Non-AC',
    bathroom_attached ENUM('Yes','No') NOT NULL DEFAULT 'No',
    status           ENUM('Available','Occupied','Maintenance') NOT NULL DEFAULT 'Available',
    CONSTRAINT fk_room_block FOREIGN KEY (block_id) REFERENCES blocks(block_id),
    CONSTRAINT uq_room_block UNIQUE (room_number, block_id)
);

-- ============================================================
-- TABLE 4: staff
-- ============================================================
CREATE TABLE staff (
    staff_id         INT AUTO_INCREMENT PRIMARY KEY,
    full_name        VARCHAR(100)   NOT NULL,
    cnic             VARCHAR(15)    NOT NULL UNIQUE,
    designation      ENUM('Warden','Security','Cleaner','Mess Staff','Accountant') NOT NULL,
    salary           DECIMAL(10,2)  NOT NULL CHECK (salary > 0),
    contact_number   VARCHAR(15)    NOT NULL,
    joining_date     DATE           NOT NULL,
    block_assigned   INT            DEFAULT NULL,
    is_active        BOOLEAN        NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_staff_block FOREIGN KEY (block_assigned) REFERENCES blocks(block_id)
);

-- Now add FK from blocks to staff (warden)
ALTER TABLE blocks
    ADD CONSTRAINT fk_block_warden
    FOREIGN KEY (warden_id) REFERENCES staff(staff_id);

-- ============================================================
-- TABLE 5: students
-- ============================================================
CREATE TABLE students (
    student_id        INT AUTO_INCREMENT PRIMARY KEY,
    full_name         VARCHAR(100)   NOT NULL,
    father_name       VARCHAR(100)   NOT NULL,
    cnic              VARCHAR(15)    NOT NULL UNIQUE,
    roll_number       VARCHAR(20)    NOT NULL UNIQUE,
    dept_id           INT            NOT NULL,
    program           VARCHAR(50)    NOT NULL,
    semester          INT            NOT NULL CHECK (semester BETWEEN 1 AND 8),
    contact_number    VARCHAR(15)    NOT NULL,
    emergency_contact VARCHAR(15)    NOT NULL,
    allotment_type    ENUM('Boarder','Day Scholar') NOT NULL DEFAULT 'Boarder',
    registration_date DATE           NOT NULL DEFAULT (CURRENT_DATE),
    status            ENUM('Active','Departed','Suspended') NOT NULL DEFAULT 'Active',
    CONSTRAINT fk_student_dept FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
);

-- ============================================================
-- TABLE 6: users
-- ============================================================
CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('Admin','Warden','Accountant','Security','Student') NOT NULL,
    staff_id      INT          DEFAULT NULL,
    student_id    INT          DEFAULT NULL,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    last_login    DATETIME     DEFAULT NULL,
    CONSTRAINT fk_user_staff   FOREIGN KEY (staff_id)   REFERENCES staff(staff_id),
    CONSTRAINT fk_user_student FOREIGN KEY (student_id) REFERENCES students(student_id)
);

-- ============================================================
-- TABLE 7: room_allocations
-- ============================================================
CREATE TABLE room_allocations (
    allocation_id   INT AUTO_INCREMENT PRIMARY KEY,
    student_id      INT  NOT NULL,
    room_id         INT  NOT NULL,
    allocation_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    end_date        DATE DEFAULT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    allocated_by    INT  DEFAULT NULL,
    CONSTRAINT fk_alloc_student FOREIGN KEY (student_id)   REFERENCES students(student_id),
    CONSTRAINT fk_alloc_room    FOREIGN KEY (room_id)      REFERENCES rooms(room_id),
    CONSTRAINT fk_alloc_staff   FOREIGN KEY (allocated_by) REFERENCES staff(staff_id)
);

-- ============================================================
-- TABLE 8: fee_structure
-- ============================================================
CREATE TABLE fee_structure (
    structure_id      INT AUTO_INCREMENT PRIMARY KEY,
    fee_type          ENUM('Hostel Rent','Security Deposit','Mess Charges','Utility Charges','Late Fine') NOT NULL,
    room_type         ENUM('Single','Double','Triple','All') NOT NULL DEFAULT 'All',
    amount            DECIMAL(10,2) NOT NULL CHECK (amount >= 0),
    late_fine_per_day DECIMAL(8,2)  NOT NULL DEFAULT 50.00,
    effective_from    DATE          NOT NULL,
    is_active         BOOLEAN       NOT NULL DEFAULT TRUE
);

-- ============================================================
-- TABLE 9: fee_bills
-- ============================================================
CREATE TABLE fee_bills (
    bill_id      INT AUTO_INCREMENT PRIMARY KEY,
    student_id   INT           NOT NULL,
    bill_month   VARCHAR(7)    NOT NULL,   -- format: YYYY-MM
    amount_due   DECIMAL(10,2) NOT NULL CHECK (amount_due > 0),
    due_date     DATE          NOT NULL,
    amount_paid  DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status       ENUM('Unpaid','Partial','Paid') NOT NULL DEFAULT 'Unpaid',
    generated_on DATE          NOT NULL DEFAULT (CURRENT_DATE),
    CONSTRAINT fk_bill_student FOREIGN KEY (student_id) REFERENCES students(student_id),
    CONSTRAINT uq_bill_month   UNIQUE (student_id, bill_month)
);

-- ============================================================
-- TABLE 10: fee_payments
-- ============================================================
CREATE TABLE fee_payments (
    payment_id     INT AUTO_INCREMENT PRIMARY KEY,
    bill_id        INT           NOT NULL,
    amount_paid    DECIMAL(10,2) NOT NULL CHECK (amount_paid > 0),
    payment_date   DATE          NOT NULL DEFAULT (CURRENT_DATE),
    payment_method ENUM('Cash','Bank Transfer','Online') NOT NULL,
    receipt_number VARCHAR(30)   NOT NULL UNIQUE,
    received_by    INT           DEFAULT NULL,
    CONSTRAINT fk_payment_bill  FOREIGN KEY (bill_id)      REFERENCES fee_bills(bill_id),
    CONSTRAINT fk_payment_staff FOREIGN KEY (received_by)  REFERENCES staff(staff_id)
);

-- ============================================================
-- TABLE 11: visitors
-- ============================================================
CREATE TABLE visitors (
    visitor_id   INT AUTO_INCREMENT PRIMARY KEY,
    visitor_name VARCHAR(100) NOT NULL,
    cnic         VARCHAR(15)  NOT NULL,
    relation     VARCHAR(50)  NOT NULL,
    student_id   INT          NOT NULL,
    purpose      VARCHAR(255) NOT NULL,
    entry_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    exit_time    DATETIME     DEFAULT NULL,
    logged_by    INT          DEFAULT NULL,
    CONSTRAINT fk_visitor_student FOREIGN KEY (student_id) REFERENCES students(student_id),
    CONSTRAINT fk_visitor_staff   FOREIGN KEY (logged_by)  REFERENCES staff(staff_id)
);

-- ============================================================
-- TABLE 12: complaints
-- ============================================================
CREATE TABLE complaints (
    complaint_id  INT AUTO_INCREMENT PRIMARY KEY,
    student_id    INT          NOT NULL,
    assigned_to   INT          DEFAULT NULL,
    category      ENUM('Electrical','Plumbing','Cleanliness','Noise','Food','Security','Other') NOT NULL,
    description   TEXT         NOT NULL,
    priority      ENUM('Low','Medium','High') NOT NULL DEFAULT 'Medium',
    status        ENUM('Pending','In Progress','Resolved','Closed') NOT NULL DEFAULT 'Pending',
    filed_date    DATE         NOT NULL DEFAULT (CURRENT_DATE),
    resolved_date DATE         DEFAULT NULL,
    CONSTRAINT fk_complaint_student FOREIGN KEY (student_id)  REFERENCES students(student_id),
    CONSTRAINT fk_complaint_staff   FOREIGN KEY (assigned_to) REFERENCES staff(staff_id)
);

-- ============================================================
-- TABLE 13: maintenance_requests
-- ============================================================
CREATE TABLE maintenance_requests (
    request_id    INT AUTO_INCREMENT PRIMARY KEY,
    location      VARCHAR(100) NOT NULL,
    issue_type    VARCHAR(100) NOT NULL,
    description   TEXT         NOT NULL,
    reported_by   INT          DEFAULT NULL,
    assigned_to   INT          DEFAULT NULL,
    status        ENUM('Open','Assigned','In Progress','Completed') NOT NULL DEFAULT 'Open',
    reported_date DATE         NOT NULL DEFAULT (CURRENT_DATE),
    completed_date DATE        DEFAULT NULL,
    cost          DECIMAL(10,2) DEFAULT NULL CHECK (cost >= 0),
    CONSTRAINT fk_maint_reporter FOREIGN KEY (reported_by) REFERENCES staff(staff_id),
    CONSTRAINT fk_maint_assigned FOREIGN KEY (assigned_to) REFERENCES staff(staff_id)
);

-- ============================================================
-- TABLE 14: meal_plans
-- ============================================================
CREATE TABLE meal_plans (
    plan_id      INT AUTO_INCREMENT PRIMARY KEY,
    plan_name    VARCHAR(50)   NOT NULL UNIQUE,
    description  VARCHAR(255)  DEFAULT NULL,
    monthly_cost DECIMAL(10,2) NOT NULL CHECK (monthly_cost >= 0)
);

-- ============================================================
-- TABLE 15: student_meal_plans
-- ============================================================
CREATE TABLE student_meal_plans (
    smp_id     INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT         NOT NULL,
    plan_id    INT         NOT NULL,
    semester   VARCHAR(20) NOT NULL,
    start_date DATE        NOT NULL,
    end_date   DATE        DEFAULT NULL,
    is_active  BOOLEAN     NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_smp_student FOREIGN KEY (student_id) REFERENCES students(student_id),
    CONSTRAINT fk_smp_plan    FOREIGN KEY (plan_id)    REFERENCES meal_plans(plan_id)
);

-- ============================================================
-- TABLE 16: notices
-- ============================================================
CREATE TABLE notices (
    notice_id       INT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(200) NOT NULL,
    description     TEXT         NOT NULL,
    posted_by       INT          NOT NULL,
    post_date       DATE         NOT NULL DEFAULT (CURRENT_DATE),
    expiry_date     DATE         DEFAULT NULL,
    target_audience ENUM('All','Block A','Block B','Block C','Block D','Block E') NOT NULL DEFAULT 'All',
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_notice_staff FOREIGN KEY (posted_by) REFERENCES staff(staff_id)
);

-- ============================================================
-- TABLE 17: audit_logs
-- ============================================================
CREATE TABLE audit_logs (
    log_id        INT AUTO_INCREMENT PRIMARY KEY,
    log_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    module        VARCHAR(100) NOT NULL,
    action        VARCHAR(255) NOT NULL,
    error_message TEXT         DEFAULT NULL,
    user_id       INT          DEFAULT NULL,
    CONSTRAINT fk_log_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- ============================================================
-- END OF SCHEMA
-- ============================================================
