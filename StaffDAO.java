package dao;

import domain.Staff;
import software.DBConnection;
import software.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    private Connection conn;

    public StaffDAO() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public boolean addStaff(Staff s) {
        String sql = "INSERT INTO staff (full_name, cnic, designation, salary, contact_number, joining_date, block_assigned, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, TRUE)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getFullName());
            ps.setString(2, s.getCnic());
            ps.setString(3, s.getDesignation());
            ps.setDouble(4, s.getSalary());
            ps.setString(5, s.getContactNumber());
            ps.setDate(6,   Date.valueOf(s.getJoiningDate()));
            if (s.getBlockAssigned() > 0)
                ps.setInt(7, s.getBlockAssigned());
            else
                ps.setNull(7, Types.INTEGER);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.log("StaffDAO", "Error adding staff: " + s.getFullName(), e);
            return false;
        }
    }

    public List<Staff> getAllStaff() {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT s.*, b.block_name FROM staff s " +
                     "LEFT JOIN blocks b ON s.block_assigned = b.block_id " +
                     "WHERE s.is_active = TRUE ORDER BY s.designation, s.full_name";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            Logger.log("StaffDAO", "Error fetching all staff.", e);
        }
        return list;
    }

    public List<Staff> getStaffByDesignation(String designation) {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT s.*, b.block_name FROM staff s " +
                     "LEFT JOIN blocks b ON s.block_assigned = b.block_id " +
                     "WHERE s.designation = ? AND s.is_active = TRUE ORDER BY s.full_name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, designation);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            Logger.log("StaffDAO", "Error fetching staff by designation.", e);
        }
        return list;
    }

    public Staff getStaffById(int staffId) {
        String sql = "SELECT s.*, b.block_name FROM staff s " +
                     "LEFT JOIN blocks b ON s.block_assigned = b.block_id WHERE s.staff_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            Logger.log("StaffDAO", "Error fetching staff by ID: " + staffId, e);
        }
        return null;
    }

    public boolean updateStaff(Staff s) {
        String sql = "UPDATE staff SET full_name=?, cnic=?, designation=?, salary=?, " +
                     "contact_number=?, block_assigned=? WHERE staff_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getFullName());
            ps.setString(2, s.getCnic());
            ps.setString(3, s.getDesignation());
            ps.setDouble(4, s.getSalary());
            ps.setString(5, s.getContactNumber());
            if (s.getBlockAssigned() > 0)
                ps.setInt(6, s.getBlockAssigned());
            else
                ps.setNull(6, Types.INTEGER);
            ps.setInt(7, s.getStaffId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.log("StaffDAO", "Error updating staff: " + s.getStaffId(), e);
            return false;
        }
    }

    private Staff mapRow(ResultSet rs) throws SQLException {
        Staff s = new Staff();
        s.setStaffId(rs.getInt("staff_id"));
        s.setFullName(rs.getString("full_name"));
        s.setCnic(rs.getString("cnic"));
        s.setDesignation(rs.getString("designation"));
        s.setSalary(rs.getDouble("salary"));
        s.setContactNumber(rs.getString("contact_number"));
        s.setActive(rs.getBoolean("is_active"));
        s.setBlockAssigned(rs.getInt("block_assigned"));
        s.setBlockName(rs.getString("block_name"));
        Date jd = rs.getDate("joining_date");
        if (jd != null) s.setJoiningDate(jd.toLocalDate());
        return s;
    }
}
