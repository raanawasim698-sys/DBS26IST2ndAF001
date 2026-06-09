-- ============================================================
--  Hostel Management System — Triggers
--  Course: CSC-104L | UET Lahore | IST 2nd-A | 2026
--  Teacher: Sir Rashad Mahmood Khan
--  Project: DBS26IST2ndAF001
-- ============================================================

USE hms_db;

DELIMITER $$

-- ============================================================
-- TRIGGER 1a: trg_room_status_on_allocate
-- AFTER INSERT on room_allocations
-- Automatically sets room status to 'Occupied'
-- ============================================================
DROP TRIGGER IF EXISTS trg_room_status_on_allocate$$
CREATE TRIGGER trg_room_status_on_allocate
AFTER INSERT ON room_allocations
FOR EACH ROW
BEGIN
    UPDATE rooms
    SET status = 'Occupied'
    WHERE room_id = NEW.room_id;
END$$

-- ============================================================
-- TRIGGER 1b: trg_room_status_on_vacate
-- AFTER UPDATE on room_allocations
-- When is_active is set to FALSE (student departs),
-- automatically sets room status back to 'Available'
-- ============================================================
DROP TRIGGER IF EXISTS trg_room_status_on_vacate$$
CREATE TRIGGER trg_room_status_on_vacate
AFTER UPDATE ON room_allocations
FOR EACH ROW
BEGIN
    IF OLD.is_active = TRUE AND NEW.is_active = FALSE THEN
        UPDATE rooms
        SET status = 'Available'
        WHERE room_id = NEW.room_id;
    END IF;
END$$

-- ============================================================
-- TRIGGER 2: trg_apply_late_fine
-- BEFORE UPDATE on fee_bills
-- If the bill is overdue and still unpaid/partial,
-- apply a late fine based on days overdue × late_fine_per_day
-- ============================================================
DROP TRIGGER IF EXISTS trg_apply_late_fine$$
CREATE TRIGGER trg_apply_late_fine
BEFORE UPDATE ON fee_bills
FOR EACH ROW
BEGIN
    DECLARE v_fine_per_day  DECIMAL(8,2) DEFAULT 50.00;
    DECLARE v_days_overdue  INT DEFAULT 0;
    DECLARE v_late_fine     DECIMAL(10,2) DEFAULT 0.00;

    -- Only apply fine if bill is overdue and not yet paid
    IF NEW.status != 'Paid'
    AND NEW.due_date < CURRENT_DATE
    AND OLD.status != 'Paid' THEN

        -- Get fine rate from fee_structure
        SELECT late_fine_per_day INTO v_fine_per_day
        FROM fee_structure
        WHERE fee_type = 'Late Fine' AND is_active = TRUE
        LIMIT 1;

        SET v_days_overdue = DATEDIFF(CURRENT_DATE, NEW.due_date);

        IF v_days_overdue > 0 THEN
            SET v_late_fine = v_days_overdue * v_fine_per_day;

            -- Only increase amount_due, never decrease it
            IF NEW.amount_due < OLD.amount_due + v_late_fine THEN
                SET NEW.amount_due = OLD.amount_due + v_late_fine;
            END IF;
        END IF;
    END IF;
END$$

-- ============================================================
-- TRIGGER 3: trg_log_complaint_status_change
-- AFTER UPDATE on complaints
-- Whenever complaint status changes, log the old and new
-- status with timestamp into audit_logs
-- ============================================================
DROP TRIGGER IF EXISTS trg_log_complaint_status_change$$
CREATE TRIGGER trg_log_complaint_status_change
AFTER UPDATE ON complaints
FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status THEN
        INSERT INTO audit_logs (log_time, module, action, error_message, user_id)
        VALUES (
            NOW(),
            'Complaints',
            CONCAT(
                'Complaint #', NEW.complaint_id,
                ' status changed from [', OLD.status,
                '] to [', NEW.status, ']'
            ),
            NULL,
            NULL
        );
    END IF;
END$$

DELIMITER ;

-- ============================================================
-- END OF TRIGGERS
-- ============================================================
