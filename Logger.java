package software;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger — Writes errors and system events to a rolling log file.
 * Call Logger.log() from any DAO or class when an exception occurs.
 */
public class Logger {

    private static final String LOG_FILE = "logs/hms_errors.log";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ── Log an exception with module name and message ─────────
    public static void log(String module, String message, Exception e) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logEntry  = String.format("[%s] [%s] %s", timestamp, module, message);

        System.err.println(logEntry); // also print to console

        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println("─".repeat(70));
            pw.println(logEntry);
            if (e != null) {
                pw.println("Exception: " + e.getClass().getName());
                pw.println("Message  : " + e.getMessage());
                e.printStackTrace(pw);
            }
            pw.println();

        } catch (IOException ioEx) {
            System.err.println("[Logger] Failed to write to log file: " + ioEx.getMessage());
        }
    }

    // ── Log a simple info message (no exception) ──────────────
    public static void log(String module, String message) {
        log(module, message, null);
    }

    // ── Log a user action ─────────────────────────────────────
    public static void logAction(String module, String action) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logEntry  = String.format("[%s] [%s] ACTION: %s", timestamp, module, action);

        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(logEntry);
        } catch (IOException ioEx) {
            System.err.println("[Logger] Failed to write action log: " + ioEx.getMessage());
        }
    }
}
