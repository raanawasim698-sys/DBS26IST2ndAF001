package software;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Validator — Static utility methods to validate all user inputs.
 * Call these from UI forms before passing data to DAOs.
 */
public class Validator {

    // CNIC format: 35201-1234567-1
    private static final Pattern CNIC_PATTERN =
            Pattern.compile("^\\d{5}-\\d{7}-\\d$");

    // Pakistan phone: 03XX-XXXXXXX
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^03\\d{2}-\\d{7}$");

    // ── Text validators ───────────────────────────────────────

    /** Returns true if the string is not null and not blank */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /** Returns true if string length is within the given range */
    public static boolean isValidLength(String value, int min, int max) {
        if (value == null) return false;
        int len = value.trim().length();
        return len >= min && len <= max;
    }

    // ── CNIC & Phone ──────────────────────────────────────────

    /** Returns true if CNIC matches XXXXX-XXXXXXX-X format */
    public static boolean isValidCNIC(String cnic) {
        if (cnic == null) return false;
        return CNIC_PATTERN.matcher(cnic.trim()).matches();
    }

    /** Returns true if phone matches 03XX-XXXXXXX format */
    public static boolean isValidContact(String phone) {
        if (phone == null) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    // ── Number validators ─────────────────────────────────────

    /** Returns true if value is a valid positive number */
    public static boolean isPositiveAmount(double value) {
        return value > 0;
    }

    /** Returns true if value is within the given integer range (inclusive) */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /** Returns true if the string can be parsed as a positive integer */
    public static boolean isPositiveInteger(String value) {
        try {
            return Integer.parseInt(value.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ── Date validators ───────────────────────────────────────

    /** Returns true if the date is not in the future */
    public static boolean isNotFutureDate(LocalDate date) {
        if (date == null) return false;
        return !date.isAfter(LocalDate.now());
    }

    /** Returns true if end date is after start date */
    public static boolean isValidDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) return false;
        return end.isAfter(start);
    }

    // ── Roll number ───────────────────────────────────────────

    /** Returns true if roll number follows UET format: UET-YYYY-XX-NNN */
    public static boolean isValidRollNumber(String rollNumber) {
        if (rollNumber == null) return false;
        return rollNumber.trim().matches("UET-\\d{4}-[A-Z]{2,3}-\\d{3}");
    }

    // ── Password ──────────────────────────────────────────────

    /** Returns true if password is at least 6 characters */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    // ── Semester ──────────────────────────────────────────────

    /** Returns true if semester is between 1 and 8 */
    public static boolean isValidSemester(int semester) {
        return isInRange(semester, 1, 8);
    }
}
