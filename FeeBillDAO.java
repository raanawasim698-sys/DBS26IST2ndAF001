package dao;

import domain.FeeBill;
import software.DBConnection;
import software.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeeBillDAO {

    private Connection conn;

    public FeeBillDAO() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    // ── Generate monthly bills via stored procedure ────────────
    public String generateMonthlyBills(String billMonth, java.time.LocalDate dueDate) {
        String sql = "{CALL sp_generate_monthly_bills(?, ?, ?, ?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, billMonth);
            cs.setDate(2, Date.valueOf(dueDate));
            cs.registerOutParameter(3, Types.INTEGER);
            cs.registerOutParameter(4, Types.VARCHAR);
            cs.execute();
            return cs.getString(4); // result message
        } catch (SQLException e) {
            Logger.log("FeeBillDAO", "Error generating monthly bills.", e);
            return "Error generating bills.";
        }
    }

    // ── Process payment via stored procedure ──────────────────
    public String processPayment(int billId, double amount, String method,
                                  String receipt, int receivedBy) {
        String sql = "{CALL sp_process_fee_payment(?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1,    billId);
            cs.setDouble(2, amount);
            cs.setString(3, method);
            cs.setString(4, receipt);
            cs.setInt(5,    receivedBy);
            cs.registerOutParameter(6, Types.INTEGER);
            cs.registerOutParameter(7, Types.VARCHAR);
            cs.execute();
            return cs.getString(7);
        } catch (SQLException e) {
            Logger.log("FeeBillDAO", "Error processing payment.", e);
            return "Error processing payment.";
        }
    }

    // ── Get all bills for a student ───────────────────────────
    public List<FeeBill> getBillsByStudent(int studentId) {
        List<FeeBill> list = new ArrayList<>();
        String sql = "SELECT fb.*, s.full_name AS student_name, s.roll_number " +
                     "FROM fee_bills fb JOIN students s ON fb.student_id = s.student_id " +
                     "WHERE fb.student_id = ? ORDER BY fb.bill_month DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            Logger.log("FeeBillDAO", "Error fetching bills for student: " + studentId, e);
        }
        return list;
    }

    // ── Get all defaulters (from view) ────────────────────────
    public List<FeeBill> getDefaulters() {
        List<FeeBill> list = new ArrayList<>();
        String sql = "SELECT bill_id, student_id, full_name AS student_name, roll_number, " +
                     "bill_month, amount_due, amount_paid, balance_due, due_date, status " +
                     "FROM vw_fee_defaulters ORDER BY days_overdue DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                FeeBill b = mapRow(rs);
                b.setAmountDue(rs.getDouble("balance_due")); // show remaining balance
                list.add(b);
            }
        } catch (SQLException e) {
            Logger.log("FeeBillDAO", "Error fetching fee defaulters.", e);
        }
        return list;
    }

    // ── Get bills by month ────────────────────────────────────
    public List<FeeBill> getBillsByMonth(String billMonth) {
        List<FeeBill> list = new ArrayList<>();
        String sql = "SELECT fb.*, s.full_name AS student_name, s.roll_number " +
                     "FROM fee_bills fb JOIN students s ON fb.student_id = s.student_id " +
                     "WHERE fb.bill_month = ? ORDER BY fb.status, s.full_name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, billMonth);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            Logger.log("FeeBillDAO", "Error fetching bills by month.", e);
        }
        return list;
    }

    private FeeBill mapRow(ResultSet rs) throws SQLException {
        FeeBill b = new FeeBill();
        b.setBillId(rs.getInt("bill_id"));
        b.setStudentId(rs.getInt("student_id"));
        b.setStudentName(rs.getString("student_name"));
        b.setRollNumber(rs.getString("roll_number"));
        b.setBillMonth(rs.getString("bill_month"));
        b.setAmountDue(rs.getDouble("amount_due"));
        b.setAmountPaid(rs.getDouble("amount_paid"));
        b.setStatus(rs.getString("status"));
        Date dd = rs.getDate("due_date");
        if (dd != null) b.setDueDate(dd.toLocalDate());
        return b;
    }
}
