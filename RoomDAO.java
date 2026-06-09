package dao;

import domain.Room;
import software.DBConnection;
import software.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    private Connection conn;

    public RoomDAO() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public boolean addRoom(Room r) {
        String sql = "INSERT INTO rooms (room_number, block_id, floor, room_type, capacity, " +
                     "monthly_rent, ac_status, bathroom_attached, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Available')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getRoomNumber());
            ps.setInt(2,    r.getBlockId());
            ps.setInt(3,    r.getFloor());
            ps.setString(4, r.getRoomType());
            ps.setInt(5,    r.getCapacity());
            ps.setDouble(6, r.getMonthlyRent());
            ps.setString(7, r.getAcStatus());
            ps.setString(8, r.getBathroomAttached());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.log("RoomDAO", "Error adding room: " + r.getRoomNumber(), e);
            return false;
        }
    }

    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT r.*, b.block_name FROM rooms r " +
                     "JOIN blocks b ON r.block_id = b.block_id ORDER BY b.block_name, r.room_number";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            Logger.log("RoomDAO", "Error fetching all rooms.", e);
        }
        return list;
    }

    public List<Room> getAvailableRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT r.*, b.block_name FROM rooms r " +
                     "JOIN blocks b ON r.block_id = b.block_id " +
                     "WHERE r.status = 'Available' ORDER BY b.block_name, r.room_number";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            Logger.log("RoomDAO", "Error fetching available rooms.", e);
        }
        return list;
    }

    public Room getRoomById(int roomId) {
        String sql = "SELECT r.*, b.block_name FROM rooms r " +
                     "JOIN blocks b ON r.block_id = b.block_id WHERE r.room_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            Logger.log("RoomDAO", "Error fetching room by ID: " + roomId, e);
        }
        return null;
    }

    public boolean updateRoom(Room r) {
        String sql = "UPDATE rooms SET room_number=?, block_id=?, floor=?, room_type=?, " +
                     "capacity=?, monthly_rent=?, ac_status=?, bathroom_attached=? WHERE room_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getRoomNumber());
            ps.setInt(2,    r.getBlockId());
            ps.setInt(3,    r.getFloor());
            ps.setString(4, r.getRoomType());
            ps.setInt(5,    r.getCapacity());
            ps.setDouble(6, r.getMonthlyRent());
            ps.setString(7, r.getAcStatus());
            ps.setString(8, r.getBathroomAttached());
            ps.setInt(9,    r.getRoomId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.log("RoomDAO", "Error updating room: " + r.getRoomId(), e);
            return false;
        }
    }

    public boolean updateRoomStatus(int roomId, String status) {
        String sql = "UPDATE rooms SET status=? WHERE room_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.log("RoomDAO", "Error updating room status: " + roomId, e);
            return false;
        }
    }

    private Room mapRow(ResultSet rs) throws SQLException {
        Room r = new Room();
        r.setRoomId(rs.getInt("room_id"));
        r.setRoomNumber(rs.getString("room_number"));
        r.setBlockId(rs.getInt("block_id"));
        r.setBlockName(rs.getString("block_name"));
        r.setFloor(rs.getInt("floor"));
        r.setRoomType(rs.getString("room_type"));
        r.setCapacity(rs.getInt("capacity"));
        r.setMonthlyRent(rs.getDouble("monthly_rent"));
        r.setAcStatus(rs.getString("ac_status"));
        r.setBathroomAttached(rs.getString("bathroom_attached"));
        r.setStatus(rs.getString("status"));
        return r;
    }
}
