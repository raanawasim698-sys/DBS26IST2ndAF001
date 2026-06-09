package software;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection — Singleton class that manages the MySQL JDBC connection.
 * Every DAO calls DBConnection.getConnection() to talk to the database.
 */
public class DBConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/hms_db?useSSL=false&serverTimezone=UTC";
    private static final String USER     = "root";
    private static final String PASSWORD = "Raana123....";

    private static DBConnection instance;
    private Connection connection;

    // ── Private constructor (singleton pattern) ───────────────
    private DBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DBConnection] Connected to hms_db successfully.");
        } catch (ClassNotFoundException e) {
            Logger.log("DBConnection", "MySQL JDBC Driver not found.", e);
            throw new RuntimeException("MySQL Driver not found. Add mysql-connector-java to your classpath.");
        } catch (SQLException e) {
            Logger.log("DBConnection", "Failed to connect to database.", e);
            throw new RuntimeException("Database connection failed: " + e.getMessage());
        }
    }

    // ── Get singleton instance ────────────────────────────────
    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    // ── Get the active connection ─────────────────────────────
    public Connection getConnection() {
        try {
            // Reconnect if connection was closed or timed out
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DBConnection] Reconnected to hms_db.");
            }
        } catch (SQLException e) {
            Logger.log("DBConnection", "Failed to reconnect to database.", e);
            throw new RuntimeException("Database reconnection failed: " + e.getMessage());
        }
        return connection;
    }

    // ── Close connection ──────────────────────────────────────
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DBConnection] Connection closed.");
            }
        } catch (SQLException e) {
            Logger.log("DBConnection", "Error closing connection.", e);
        }
    }
}
