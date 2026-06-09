package dao;

import software.DBConnection;
import software.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomAllocationDAO {

    private Connection conn;

    public RoomAllocationDAO() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /** Allocate room via stored procedure */
    public String allocateRoom(int studentId, int roomId, int allocatedBy) {
        String sql = "{CALL sp_allocate_room(?, ?, ?, ?, ?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, studentId);
            cs.setInt(2, roomId);
            cs.setInt(3, allocatedBy);
            cs.registerOutParameter(4, Types.INTEGER);
            cs.registerOutParameter(5, Types.VARCHAR);
            cs.execute();
            return cs.getString(5);
        } catch (SQLException e) {
            Logger.log("RoomAllocationDAO", "Error allocating room.", e);
            return "Error: " + e.getMessage();
        }
    }

    /** End a student's room allocation (departure) */
    public boolean vacateRoom(int studentId) {
        String sql = "UPDATE room_allocations SET is_active = FALSE, end_date = CURRENT_DATE " +
                     "WHERE student_id = ? AND is_active = TRUE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.log("RoomAllocationDAO", "Error vacating room for student: " + studentId, e);
            return false;
        }
    }

    /** Get current room info for a student */
    public java.util.Map<String, String> getCurrentRoom(int studentId) {
        java.util.Map<String, String> info = new java.util.HashMap<>();
        String sql = "SELECT r.room_number, b.block_name, r.room_type, r.monthly_rent " +
                     "FROM room_allocations ra " +
                     "JOIN rooms r ON ra.room_id = r.room_id " +
                     "JOIN blocks b ON r.block_id = b.block_id " +
                     "WHERE ra.student_id = ? AND ra.is_active = TRUE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                info.put("room_number", rs.getString("room_number"));
                info.put("block_name",  rs.getString("block_name"));
                info.put("room_type",   rs.getString("room_type"));
                info.put("monthly_rent", String.valueOf(rs.getDouble("monthly_rent")));
            }
        } catch (SQLException e) {
            Logger.log("RoomAllocationDAO", "Error fetching current room.", e);
        }
        return info;
    }

    /** Get all current allocations for the dashboard table */
    public List<Object[]> getAllCurrentAllocations() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT s.full_name, s.roll_number, r.room_number, b.block_name, " +
                     "r.room_type, ra.allocation_date " +
                     "FROM room_allocations ra " +
                     "JOIN students s ON ra.student_id = s.student_id " +
                     "JOIN rooms r ON ra.room_id = r.room_id " +
                     "JOIN blocks b ON r.block_id = b.block_id " +
                     "WHERE ra.is_active = TRUE ORDER BY b.block_name, r.room_number";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("full_name"),
                    rs.getString("roll_number"),
                    rs.getString("room_number"),
                    rs.getString("block_name"),
                    rs.getString("room_type"),
                    rs.getDate("allocation_date")
                });
            }
        } catch (SQLException e) {
            Logger.log("RoomAllocationDAO", "Error fetching all allocations.", e);
        }
        return list;
    }
}
