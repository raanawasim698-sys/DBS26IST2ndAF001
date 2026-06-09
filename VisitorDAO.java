package dao;

import domain.Visitor;
import software.DBConnection;
import software.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VisitorDAO {

    private Connection conn;

    public VisitorDAO() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public boolean logEntry(Visitor v) {
        String sql = "INSERT INTO visitors (visitor_name, cnic, relation, student_id, purpose, entry_time, logged_by) " +
                     "VALUES (?, ?, ?, ?, ?, NOW(), ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getVisitorName());
            ps.setString(2, v.getCnic());
            ps.setString(3, v.getRelation());
            ps.setInt(4,    v.getStudentId());
            ps.setString(5, v.getPurpose());
            ps.setInt(6,    v.getLoggedBy());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.log("VisitorDAO", "Error logging visitor entry.", e);
            return false;
        }
    }

    public boolean recordExit(int visitorId) {
        String sql = "UPDATE visitors SET exit_time = NOW() WHERE visitor_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, visitorId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.log("VisitorDAO", "Error recording visitor exit.", e);
            return false;
        }
    }

    public List<Visitor> getTodayVisitors() {
        List<Visitor> list = new ArrayList<>();
        String sql = "SELECT v.*, s.full_name AS student_name, s.roll_number, " +
                     "r.room_number, st.full_name AS logged_by_name " +
                     "FROM vw_visitor_log_today v " +
                     "JOIN students s ON v.student_id = s.student_id " +
                     "LEFT JOIN room_allocations ra ON s.student_id = ra.student_id AND ra.is_active = TRUE " +
                     "LEFT JOIN rooms r ON ra.room_id = r.room_id " +
                     "LEFT JOIN staff st ON v.logged_by = st.staff_id";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            Logger.log("VisitorDAO", "Error fetching today's visitors.", e);
        }
        return list;
    }

    public List<Visitor> getVisitorsByDateRange(java.time.LocalDate from, java.time.LocalDate to) {
        List<Visitor> list = new ArrayList<>();
        String sql = "SELECT v.*, s.full_name AS student_name, s.roll_number, " +
                     "st.full_name AS logged_by_name " +
                     "FROM visitors v " +
                     "JOIN students s ON v.student_id = s.student_id " +
                     "LEFT JOIN staff st ON v.logged_by = st.staff_id " +
                     "WHERE DATE(v.entry_time) BETWEEN ? AND ? ORDER BY v.entry_time DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            Logger.log("VisitorDAO", "Error fetching visitors by date range.", e);
        }
        return list;
    }

    private Visitor mapRow(ResultSet rs) throws SQLException {
        Visitor v = new Visitor();
        v.setVisitorId(rs.getInt("visitor_id"));
        v.setVisitorName(rs.getString("visitor_name"));
        v.setCnic(rs.getString("cnic"));
        v.setRelation(rs.getString("relation"));
        v.setStudentId(rs.getInt("student_id"));
        v.setStudentName(rs.getString("student_name"));
        v.setPurpose(rs.getString("purpose"));
        Timestamp et = rs.getTimestamp("entry_time");
        if (et != null) v.setEntryTime(et.toLocalDateTime());
        Timestamp xt = rs.getTimestamp("exit_time");
        if (xt != null) v.setExitTime(xt.toLocalDateTime());
        return v;
    }
}
