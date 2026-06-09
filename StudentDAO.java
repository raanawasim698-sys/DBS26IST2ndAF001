package dao;

import domain.Student;
import software.DBConnection;
import software.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * StudentDAO — All database operations for the students table.
 */
public class StudentDAO {

    private Connection conn;

    public StudentDAO() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    // ── INSERT ────────────────────────────────────────────────
    public boolean addStudent(Student s) {
        String sql = "INSERT INTO students (full_name, father_name, cnic, roll_number, " +
                     "dept_id, program, semester, contact_number, emergency_contact, " +
                     "allotment_type, registration_date, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,  s.getFullName());
            ps.setString(2,  s.getFatherName());
            ps.setString(3,  s.getCnic());
            ps.setString(4,  s.getRollNumber());
            ps.setInt(5,     s.getDeptId());
            ps.setString(6,  s.getProgram());
            ps.setInt(7,     s.getSemester());
            ps.setString(8,  s.getContactNumber());
            ps.setString(9,  s.getEmergencyContact());
            ps.setString(10, s.getAllotmentType());
            ps.setDate(11,   Date.valueOf(LocalDate.now()));
            ps.setString(12, "Active");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.log("StudentDAO", "Error adding student: " + s.getRollNumber(), e);
            return false;
        }
    }

    // ── SELECT ALL ────────────────────────────────────────────
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, d.dept_name FROM students s " +
                     "JOIN departments d ON s.dept_id = d.dept_id " +
                     "ORDER BY s.full_name";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            Logger.log("StudentDAO", "Error fetching all students.", e);
        }
        return list;
    }

    // ── SELECT ACTIVE ONLY ────────────────────────────────────
    public List<Student> getActiveStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, d.dept_name FROM students s " +
                     "JOIN departments d ON s.dept_id = d.dept_id " +
                     "WHERE s.status = 'Active' ORDER BY s.full_name";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            Logger.log("StudentDAO", "Error fetching active students.", e);
        }
        return list;
    }

    // ── SELECT BY ID ──────────────────────────────────────────
    public Student getStudentById(int studentId) {
        String sql = "SELECT s.*, d.dept_name FROM students s " +
                     "JOIN departments d ON s.dept_id = d.dept_id " +
                     "WHERE s.student_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            Logger.log("StudentDAO", "Error fetching student by ID: " + studentId, e);
        }
        return null;
    }

    // ── SEARCH ────────────────────────────────────────────────
    public List<Student> searchStudents(String keyword) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, d.dept_name FROM students s " +
                     "JOIN departments d ON s.dept_id = d.dept_id " +
                     "WHERE s.full_name LIKE ? OR s.roll_number LIKE ? OR s.cnic LIKE ? " +
                     "ORDER BY s.full_name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            Logger.log("StudentDAO", "Error searching students: " + keyword, e);
        }
        return list;
    }

    // ── UPDATE ────────────────────────────────────────────────
    public boolean updateStudent(Student s) {
        String sql = "UPDATE students SET full_name=?, father_name=?, cnic=?, " +
                     "roll_number=?, dept_id=?, program=?, semester=?, " +
                     "contact_number=?, emergency_contact=?, allotment_type=? " +
                     "WHERE student_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,  s.getFullName());
            ps.setString(2,  s.getFatherName());
            ps.setString(3,  s.getCnic());
            ps.setString(4,  s.getRollNumber());
            ps.setInt(5,     s.getDeptId());
            ps.setString(6,  s.getProgram());
            ps.setInt(7,     s.getSemester());
            ps.setString(8,  s.getContactNumber());
            ps.setString(9,  s.getEmergencyContact());
            ps.setString(10, s.getAllotmentType());
            ps.setInt(11,    s.getStudentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.log("StudentDAO", "Error updating student: " + s.getStudentId(), e);
            return false;
        }
    }

    // ── UPDATE STATUS ─────────────────────────────────────────
    public boolean updateStudentStatus(int studentId, String status) {
        String sql = "UPDATE students SET status=? WHERE student_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.log("StudentDAO", "Error updating student status: " + studentId, e);
            return false;
        }
    }

    // ── MAP ROW ───────────────────────────────────────────────
    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setFullName(rs.getString("full_name"));
        s.setFatherName(rs.getString("father_name"));
        s.setCnic(rs.getString("cnic"));
        s.setRollNumber(rs.getString("roll_number"));
        s.setDeptId(rs.getInt("dept_id"));
        s.setProgram(rs.getString("program"));
        s.setSemester(rs.getInt("semester"));
        s.setContactNumber(rs.getString("contact_number"));
        s.setEmergencyContact(rs.getString("emergency_contact"));
        s.setAllotmentType(rs.getString("allotment_type"));
        s.setStatus(rs.getString("status"));
        Date rd = rs.getDate("registration_date");
        if (rd != null) s.setRegistrationDate(rd.toLocalDate());
        return s;
    }
}
