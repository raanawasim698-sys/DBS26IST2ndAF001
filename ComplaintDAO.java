package dao;

import domain.Complaint;
import software.DBConnection;
import software.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDAO {

    private Connection conn;

    public ComplaintDAO() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public boolean addComplaint(Complaint c) {
        String sql = "INSERT INTO complaints (student_id, category, description, priority, status, filed_date) " +
                     "VALUES (?, ?, ?, ?, 'Pending', CURRENT_DATE)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,    c.getStudentId());
            ps.setString(2, c.getCategory());
            ps.setString(3, c.getDescription());
            ps.setString(4, c.getPriority());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.log("ComplaintDAO", "Error adding complaint.", e);
            return false;
        }
    }

    public List<Complaint> getAllComplaints() {
        List<Complaint> list = new ArrayList<>();
        String sql = "SELECT c.*, s.full_name AS student_name, s.roll_number, " +
                     "st.full_name AS assigned_to_name " +
                     "FROM complaints c " +
                     "JOIN students s ON c.student_id = s.student_id " +
                     "LEFT JOIN staff st ON c.assigned_to = st.staff_id " +
                     "ORDER BY FIELD(c.priority,'High','Medium','Low'), c.filed_date DESC";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            Logger.log("ComplaintDAO", "Error fetching complaints.", e);
        }
        return list;
    }

    public List<Complaint> getComplaintsByStatus(String status) {
        List<Complaint> list = new ArrayList<>();
        String sql = "SELECT c.*, s.full_name AS student_name, s.roll_number, " +
                     "st.full_name AS assigned_to_name " +
                     "FROM complaints c " +
                     "JOIN students s ON c.student_id = s.student_id " +
                     "LEFT JOIN staff st ON c.assigned_to = st.staff_id " +
                     "WHERE c.status = ? ORDER BY c.filed_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            Logger.log("ComplaintDAO", "Error fetching complaints by status.", e);
        }
        return list;
    }

    public boolean assignComplaint(int complaintId, int staffId) {
        String sql = "UPDATE complaints SET assigned_to=?, status='In Progress' WHERE complaint_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            ps.setInt(2, complaintId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.log("ComplaintDAO", "Error assigning complaint: " + complaintId, e);
            return false;
        }
    }

    public boolean resolveComplaint(int complaintId, int resolvedBy) {
        // Calls the stored procedure sp_resolve_complaint
        String sql = "{CALL sp_resolve_complaint(?, ?, ?, ?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, complaintId);
            cs.setInt(2, resolvedBy);
            cs.registerOutParameter(3, Types.INTEGER);
            cs.registerOutParameter(4, Types.VARCHAR);
            cs.execute();
            int code = cs.getInt(3);
            return code == 0;
        } catch (SQLException e) {
            Logger.log("ComplaintDAO", "Error resolving complaint: " + complaintId, e);
            return false;
        }
    }

    private Complaint mapRow(ResultSet rs) throws SQLException {
        Complaint c = new Complaint();
        c.setComplaintId(rs.getInt("complaint_id"));
        c.setStudentId(rs.getInt("student_id"));
        c.setStudentName(rs.getString("student_name"));
        c.setRollNumber(rs.getString("roll_number"));
        c.setAssignedTo(rs.getInt("assigned_to"));
        c.setAssignedToName(rs.getString("assigned_to_name"));
        c.setCategory(rs.getString("category"));
        c.setDescription(rs.getString("description"));
        c.setPriority(rs.getString("priority"));
        c.setStatus(rs.getString("status"));
        Date fd = rs.getDate("filed_date");
        if (fd != null) c.setFiledDate(fd.toLocalDate());
        Date rd = rs.getDate("resolved_date");
        if (rd != null) c.setResolvedDate(rd.toLocalDate());
        return c;
    }
}
