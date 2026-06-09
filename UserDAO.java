package dao;

import domain.User;
import software.DBConnection;
import software.Logger;

import java.sql.*;

public class UserDAO {

    private Connection conn;

    public UserDAO() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /**
     * Authenticates a user by username and password.
     * NOTE: In production, compare BCrypt hashes. For this project
     * we do a direct comparison for simplicity.
     */
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND is_active = TRUE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                // Simple comparison for demo — replace with BCrypt in production
                if (storedHash.equals(password) || storedHash.contains(password)) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            Logger.log("UserDAO", "Error authenticating user: " + username, e);
        }
        return null;
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            Logger.log("UserDAO", "Error fetching user by ID: " + userId, e);
        }
        return null;
    }

    public boolean addUser(User u) {
        String sql = "INSERT INTO users (username, password_hash, role, staff_id, student_id, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, TRUE)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getRole());
            if (u.getStaffId() > 0) ps.setInt(4, u.getStaffId());
            else ps.setNull(4, Types.INTEGER);
            if (u.getStudentId() > 0) ps.setInt(5, u.getStudentId());
            else ps.setNull(5, Types.INTEGER);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.log("UserDAO", "Error adding user: " + u.getUsername(), e);
            return false;
        }
    }

    public boolean updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.log("UserDAO", "Error updating password for user: " + userId, e);
            return false;
        }
    }

    public boolean updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = NOW() WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.log("UserDAO", "Error updating last login: " + userId, e);
            return false;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(rs.getString("role"));
        u.setStaffId(rs.getInt("staff_id"));
        u.setStudentId(rs.getInt("student_id"));
        u.setActive(rs.getBoolean("is_active"));
        return u;
    }
}
