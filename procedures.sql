-- ============================================================
--  Hostel Management System — Stored Procedures
--  Course: CSC-104L | UET Lahore | IST 2nd-A | 2026
--  Teacher: Sir Rashad Mahmood Khan
--  Project: DBS26IST2ndAF001
-- ============================================================

USE hms_db;

DELIMITER $$

-- ============================================================
-- PROCEDURE 1: sp_allocate_room
-- Allocates a room to a student in a single transaction
-- Checks: room must be available, student must be active,
--         student must not already have an active allocation
-- ============================================================
DROP PROCEDURE IF EXISTS sp_allocate_room$$
CREATE PROCEDURE sp_allocate_room (
    IN  p_student_id     INT,
    IN  p_room_id        INT,
    IN  p_allocated_by   INT,
    OUT p_result_code    INT,       -- 0=success, 1=room unavailable, 2=student not found,
    OUT p_result_message VARCHAR(255) -- 3=already allocated
)
BEGIN
    DECLARE v_room_status    VARCHAR(20);
    DECLARE v_student_status VARCHAR(20);
    DECLARE v_existing_alloc INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result_code    = 99;
        SET p_result_message = 'Unexpected error during room allocation. Transaction rolled back.';
    END;

    -- Check room exists and is available
    SELECT status INTO v_room_status
    FROM rooms WHERE room_id = p_room_id;

    IF v_room_status IS NULL THEN
        SET p_result_code    = 1;
        SET p_result_message = 'Room not found.';
    ELSEIF v_room_status != 'Available' THEN
        SET p_result_code    = 1;
        SET p_result_message = CONCAT('Room is currently ', v_room_status, '. Cannot allocate.');

    ELSE
        -- Check student exists and is active
        SELECT status INTO v_student_status
        FROM students WHERE student_id = p_student_id;

        IF v_student_status IS NULL THEN
            SET p_result_code    = 2;
            SET p_result_message = 'Student not found.';
        ELSEIF v_student_status != 'Active' THEN
            SET p_result_code    = 2;
            SET p_result_message = 'Student is not active.';

        ELSE
            -- Check student doesn't already have an active allocation
            SELECT COUNT(*) INTO v_existing_alloc
            FROM room_allocations
            WHERE student_id = p_student_id AND is_active = TRUE;

            IF v_existing_alloc > 0 THEN
                SET p_result_code    = 3;
                SET p_result_message = 'Student already has an active room allocation.';

            ELSE
                -- All checks passed — begin transaction
                START TRANSACTION;

                -- Insert allocation record
                INSERT INTO room_allocations (student_id, room_id, allocation_date, is_active, allocated_by)
                VALUES (p_student_id, p_room_id, CURRENT_DATE, TRUE, p_allocated_by);

                -- Update room status (trigger will also fire here)
                UPDATE rooms SET status = 'Occupied' WHERE room_id = p_room_id;

                COMMIT;

                SET p_result_code    = 0;
                SET p_result_message = 'Room allocated successfully.';
            END IF;
        END IF;
    END IF;
END$$

-- ============================================================
-- PROCEDURE 2: sp_process_fee_payment
-- Records a payment against a bill in a single transaction.
-- Automatically updates bill status to Paid or Partial.
-- ============================================================
DROP PROCEDURE IF EXISTS sp_process_fee_payment$$
CREATE PROCEDURE sp_process_fee_payment (
    IN  p_bill_id        INT,
    IN  p_amount_paid    DECIMAL(10,2),
    IN  p_payment_method VARCHAR(20),
    IN  p_receipt_number VARCHAR(30),
    IN  p_received_by    INT,
    OUT p_result_code    INT,
    OUT p_result_message VARCHAR(255)
)
BEGIN
    DECLARE v_amount_due    DECIMAL(10,2);
    DECLARE v_already_paid  DECIMAL(10,2);
    DECLARE v_balance       DECIMAL(10,2);
    DECLARE v_bill_status   VARCHAR(10);
    DECLARE v_new_total     DECIMAL(10,2);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result_code    = 99;
        SET p_result_message = 'Unexpected error during payment. Transaction rolled back.';
    END;

    -- Validate payment amount
    IF p_amount_paid <= 0 THEN
        SET p_result_code    = 1;
        SET p_result_message = 'Payment amount must be greater than zero.';
    ELSE
        -- Fetch bill details
        SELECT amount_due, amount_paid, status
        INTO   v_amount_due, v_already_paid, v_bill_status
        FROM   fee_bills WHERE bill_id = p_bill_id;

        IF v_amount_due IS NULL THEN
            SET p_result_code    = 2;
            SET p_result_message = 'Bill not found.';
        ELSEIF v_bill_status = 'Paid' THEN
            SET p_result_code    = 3;
            SET p_result_message = 'This bill is already fully paid.';
        ELSE
            SET v_balance   = v_amount_due - v_already_paid;
            SET v_new_total = v_already_paid + p_amount_paid;

            IF p_amount_paid > v_balance THEN
                SET p_result_code    = 4;
                SET p_result_message = CONCAT('Payment exceeds balance due. Balance is: ', v_balance);
            ELSE
                START TRANSACTION;

                -- Record payment
                INSERT INTO fee_payments (bill_id, amount_paid, payment_date, payment_method, receipt_number, received_by)
                VALUES (p_bill_id, p_amount_paid, CURRENT_DATE, p_payment_method, p_receipt_number, p_received_by);

                -- Update bill
                UPDATE fee_bills
                SET amount_paid = v_new_total,
                    status = CASE
                        WHEN v_new_total >= amount_due THEN 'Paid'
                        ELSE 'Partial'
                    END
                WHERE bill_id = p_bill_id;

                COMMIT;

                SET p_result_code    = 0;
                SET p_result_message = 'Payment recorded successfully.';
            END IF;
        END IF;
    END IF;
END$$

-- ============================================================
-- PROCEDURE 3: sp_generate_monthly_bills
-- Generates fee bills for all active students for a given month.
-- Skips students who already have a bill for that month.
-- ============================================================
DROP PROCEDURE IF EXISTS sp_generate_monthly_bills$$
CREATE PROCEDURE sp_generate_monthly_bills (
    IN  p_bill_month     VARCHAR(7),   -- format: YYYY-MM
    IN  p_due_date       DATE,
    OUT p_bills_created  INT,
    OUT p_result_message VARCHAR(255)
)
BEGIN
    DECLARE v_done         INT DEFAULT FALSE;
    DECLARE v_student_id   INT;
    DECLARE v_room_type    VARCHAR(10);
    DECLARE v_rent         DECIMAL(10,2);
    DECLARE v_bill_exists  INT;

    DECLARE cur CURSOR FOR
        SELECT s.student_id, r.room_type, r.monthly_rent
        FROM students s
        JOIN room_allocations ra ON s.student_id = ra.student_id AND ra.is_active = TRUE
        JOIN rooms r ON ra.room_id = r.room_id
        WHERE s.status = 'Active';

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done = TRUE;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result_message = 'Error during bill generation. Transaction rolled back.';
    END;

    SET p_bills_created = 0;

    START TRANSACTION;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO v_student_id, v_room_type, v_rent;
        IF v_done THEN LEAVE read_loop; END IF;

        -- Check if bill already exists for this month
        SELECT COUNT(*) INTO v_bill_exists
        FROM fee_bills
        WHERE student_id = v_student_id AND bill_month = p_bill_month;

        IF v_bill_exists = 0 THEN
            INSERT INTO fee_bills (student_id, bill_month, amount_due, due_date, status)
            VALUES (v_student_id, p_bill_month, v_rent, p_due_date, 'Unpaid');
            SET p_bills_created = p_bills_created + 1;
        END IF;
    END LOOP;

    CLOSE cur;
    COMMIT;

    SET p_result_message = CONCAT(p_bills_created, ' bill(s) generated for ', p_bill_month);
END$$

-- ============================================================
-- PROCEDURE 4: sp_resolve_complaint
-- Marks a complaint as resolved and records the resolver
-- ============================================================
DROP PROCEDURE IF EXISTS sp_resolve_complaint$$
CREATE PROCEDURE sp_resolve_complaint (
    IN  p_complaint_id   INT,
    IN  p_resolved_by    INT,
    OUT p_result_code    INT,
    OUT p_result_message VARCHAR(255)
)
BEGIN
    DECLARE v_status VARCHAR(20);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result_code    = 99;
        SET p_result_message = 'Unexpected error. Transaction rolled back.';
    END;

    SELECT status INTO v_status
    FROM complaints WHERE complaint_id = p_complaint_id;

    IF v_status IS NULL THEN
        SET p_result_code    = 1;
        SET p_result_message = 'Complaint not found.';
    ELSEIF v_status = 'Resolved' OR v_status = 'Closed' THEN
        SET p_result_code    = 2;
        SET p_result_message = 'Complaint is already resolved or closed.';
    ELSE
        START TRANSACTION;

        UPDATE complaints
        SET status        = 'Resolved',
            assigned_to   = p_resolved_by,
            resolved_date = CURRENT_DATE
        WHERE complaint_id = p_complaint_id;

        COMMIT;

        SET p_result_code    = 0;
        SET p_result_message = 'Complaint marked as resolved.';
    END IF;
END$$

DELIMITER ;

-- ============================================================
-- END OF STORED PROCEDURES
-- ============================================================
