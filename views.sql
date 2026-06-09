-- ============================================================
--  Hostel Management System — Views
--  Course: CSC-104L | UET Lahore | IST 2nd-A | 2026
--  Teacher: Sir Rashad Mahmood Khan
--  Project: DBS26IST2ndAF001
-- ============================================================

USE hms_db;

-- ============================================================
-- VIEW 1: vw_available_rooms
-- Shows all rooms currently available for allocation
-- ============================================================
CREATE OR REPLACE VIEW vw_available_rooms AS
SELECT
    r.room_id,
    r.room_number,
    b.block_name,
    r.floor,
    r.room_type,
    r.capacity,
    r.monthly_rent,
    r.ac_status,
    r.bathroom_attached
FROM rooms r
JOIN blocks b ON r.block_id = b.block_id
WHERE r.status = 'Available'
ORDER BY b.block_name, r.floor, r.room_number;

-- ============================================================
-- VIEW 2: vw_current_occupants
-- Shows all students currently living in rooms
-- ============================================================
CREATE OR REPLACE VIEW vw_current_occupants AS
SELECT
    s.student_id,
    s.full_name,
    s.roll_number,
    d.dept_name,
    s.program,
    s.semester,
    s.contact_number,
    r.room_number,
    b.block_name,
    r.room_type,
    r.monthly_rent,
    ra.allocation_date
FROM room_allocations ra
JOIN students s  ON ra.student_id = s.student_id
JOIN rooms r     ON ra.room_id    = r.room_id
JOIN blocks b    ON r.block_id    = b.block_id
JOIN departments d ON s.dept_id  = d.dept_id
WHERE ra.is_active = TRUE
ORDER BY b.block_name, r.room_number;

-- ============================================================
-- VIEW 3: vw_fee_defaulters
-- Shows students with unpaid or partially paid bills
-- ============================================================
CREATE OR REPLACE VIEW vw_fee_defaulters AS
SELECT
    s.student_id,
    s.full_name,
    s.roll_number,
    s.contact_number,
    r.room_number,
    b.block_name,
    fb.bill_id,
    fb.bill_month,
    fb.amount_due,
    fb.amount_paid,
    (fb.amount_due - fb.amount_paid) AS balance_due,
    fb.due_date,
    fb.status,
    DATEDIFF(CURRENT_DATE, fb.due_date) AS days_overdue
FROM fee_bills fb
JOIN students s ON fb.student_id = s.student_id
LEFT JOIN room_allocations ra ON s.student_id = ra.student_id AND ra.is_active = TRUE
LEFT JOIN rooms r  ON ra.room_id  = r.room_id
LEFT JOIN blocks b ON r.block_id  = b.block_id
WHERE fb.status IN ('Unpaid', 'Partial')
AND fb.due_date < CURRENT_DATE
ORDER BY days_overdue DESC;

-- ============================================================
-- VIEW 4: vw_active_complaints
-- Shows all complaints that are pending or in progress
-- ============================================================
CREATE OR REPLACE VIEW vw_active_complaints AS
SELECT
    c.complaint_id,
    s.full_name       AS student_name,
    s.roll_number,
    r.room_number,
    b.block_name,
    c.category,
    c.description,
    c.priority,
    c.status,
    c.filed_date,
    DATEDIFF(CURRENT_DATE, c.filed_date) AS days_open,
    st.full_name      AS assigned_to_name
FROM complaints c
JOIN students s  ON c.student_id  = s.student_id
LEFT JOIN room_allocations ra ON s.student_id = ra.student_id AND ra.is_active = TRUE
LEFT JOIN rooms r  ON ra.room_id  = r.room_id
LEFT JOIN blocks b ON r.block_id  = b.block_id
LEFT JOIN staff st ON c.assigned_to = st.staff_id
WHERE c.status IN ('Pending', 'In Progress')
ORDER BY
    FIELD(c.priority, 'High', 'Medium', 'Low'),
    c.filed_date ASC;

-- ============================================================
-- VIEW 5: vw_visitor_log_today
-- Shows all visitor entries for the current date
-- ============================================================
CREATE OR REPLACE VIEW vw_visitor_log_today AS
SELECT
    v.visitor_id,
    v.visitor_name,
    v.cnic,
    v.relation,
    s.full_name      AS student_name,
    s.roll_number,
    r.room_number,
    b.block_name,
    v.purpose,
    v.entry_time,
    v.exit_time,
    CASE
        WHEN v.exit_time IS NULL THEN 'Still Inside'
        ELSE 'Exited'
    END AS visitor_status,
    st.full_name     AS logged_by_name
FROM visitors v
JOIN students s  ON v.student_id = s.student_id
LEFT JOIN room_allocations ra ON s.student_id = ra.student_id AND ra.is_active = TRUE
LEFT JOIN rooms r  ON ra.room_id  = r.room_id
LEFT JOIN blocks b ON r.block_id  = b.block_id
LEFT JOIN staff st ON v.logged_by  = st.staff_id
WHERE DATE(v.entry_time) = CURRENT_DATE
ORDER BY v.entry_time DESC;

-- ============================================================
-- VIEW 6: vw_block_occupancy_summary
-- Shows block-wise occupancy stats
-- ============================================================
CREATE OR REPLACE VIEW vw_block_occupancy_summary AS
SELECT
    b.block_id,
    b.block_name,
    COUNT(r.room_id)                                          AS total_rooms,
    SUM(CASE WHEN r.status = 'Available'    THEN 1 ELSE 0 END) AS available_rooms,
    SUM(CASE WHEN r.status = 'Occupied'     THEN 1 ELSE 0 END) AS occupied_rooms,
    SUM(CASE WHEN r.status = 'Maintenance'  THEN 1 ELSE 0 END) AS maintenance_rooms,
    ROUND(
        SUM(CASE WHEN r.status = 'Occupied' THEN 1 ELSE 0 END)
        / COUNT(r.room_id) * 100, 1
    )                                                           AS occupancy_percent,
    st.full_name                                                AS warden_name,
    st.contact_number                                           AS warden_contact
FROM blocks b
LEFT JOIN rooms r  ON b.block_id  = r.block_id
LEFT JOIN staff st ON b.warden_id = st.staff_id
GROUP BY b.block_id, b.block_name, st.full_name, st.contact_number
ORDER BY b.block_name;

-- ============================================================
-- END OF VIEWS
-- ============================================================
