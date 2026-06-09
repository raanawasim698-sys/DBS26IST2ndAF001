package ui;

import ui.util.Theme;
import ui.util.UIFactory;
import dao.StaffDAO;
import domain.Staff;
import software.Logger;
import software.DBConnection;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class MaintenanceForm extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtLocation, txtIssueType, txtCost;
    private JTextArea txtDescription;
    private JComboBox<String> cmbStatus, cmbAssignedTo;
    private JButton btnSave, btnClear;
    private JLabel lblFormTitle;
    private int selectedId = -1;

    private static final String[] COLUMNS = {"ID","Location","Issue Type","Status","Reported","Cost","Action"};

    public MaintenanceForm() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);
        initUI();
        loadData();
    }

    private void initUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.PRIMARY);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("🔧  Maintenance Requests");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_WHITE);
        JLabel sub = new JLabel("Log and track hostel maintenance issues");
        sub.setFont(Theme.FONT_SUBTITLE);
        sub.setForeground(new Color(200, 220, 255));
        JPanel ht = new JPanel(new GridLayout(2,1));
        ht.setOpaque(false);
        ht.add(title); ht.add(sub);
        header.add(ht, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildForm(), buildTable());
        split.setDividerLocation(370);
        split.setDividerSize(2);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildForm() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Theme.BG_MAIN);
        outer.setBorder(new EmptyBorder(16,16,16,8));

        JPanel card = UIFactory.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20,20,20,20));

        lblFormTitle = UIFactory.boldLabel("New Maintenance Request");
        lblFormTitle.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblFormTitle);
        card.add(Box.createVerticalStrut(16));

        card.add(UIFactory.label("Location *")); card.add(Box.createVerticalStrut(4));
        txtLocation = UIFactory.textField(20); card.add(txtLocation); card.add(Box.createVerticalStrut(12));

        card.add(UIFactory.label("Issue Type *")); card.add(Box.createVerticalStrut(4));
        txtIssueType = UIFactory.textField(20); card.add(txtIssueType); card.add(Box.createVerticalStrut(12));

        card.add(UIFactory.label("Description *")); card.add(Box.createVerticalStrut(4));
        txtDescription = new JTextArea(4,20);
        txtDescription.setFont(Theme.FONT_INPUT);
        txtDescription.setLineWrap(true); txtDescription.setWrapStyleWord(true);
        txtDescription.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER),
            BorderFactory.createEmptyBorder(6,8,6,8)));
        JScrollPane ds = UIFactory.textArea(txtDescription);
        ds.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        ds.setAlignmentX(LEFT_ALIGNMENT);
        card.add(ds); card.add(Box.createVerticalStrut(12));

        card.add(UIFactory.label("Assign To (Staff)")); card.add(Box.createVerticalStrut(4));
        cmbAssignedTo = UIFactory.comboBox(new String[]{"-- Unassigned --"});
        loadStaffCombo(); card.add(cmbAssignedTo); card.add(Box.createVerticalStrut(12));

        card.add(UIFactory.label("Status")); card.add(Box.createVerticalStrut(4));
        cmbStatus = UIFactory.comboBox(new String[]{"Open","Assigned","In Progress","Completed"});
        card.add(cmbStatus); card.add(Box.createVerticalStrut(12));

        card.add(UIFactory.label("Repair Cost (Rs.)")); card.add(Box.createVerticalStrut(4));
        txtCost = UIFactory.textField(20); txtCost.setText("0");
        card.add(txtCost); card.add(Box.createVerticalStrut(20));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false); btnRow.setAlignmentX(LEFT_ALIGNMENT);
        btnSave = UIFactory.primaryButton("Save Request");
        btnSave.addActionListener(e -> saveRequest());
        btnClear = UIFactory.secondaryButton("Clear");
        btnClear.addActionListener(e -> clearForm());
        btnRow.add(btnSave); btnRow.add(btnClear);
        card.add(btnRow);

        outer.add(card, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildTable() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Theme.BG_MAIN);
        outer.setBorder(new EmptyBorder(16,8,16,16));

        JPanel card = UIFactory.card();
        card.setLayout(new BorderLayout());

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 12));
        toolbar.setOpaque(false);
        toolbar.add(UIFactory.boldLabel("All Requests"));
        JButton btnRefresh = UIFactory.secondaryButton("↻ Refresh");
        btnRefresh.addActionListener(e -> loadData());
        toolbar.add(btnRefresh);
        card.add(toolbar, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return c == 6; }
        };
        table = new JTable(tableModel);
        UIFactory.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(6).setMaxWidth(80);

        // Edit button renderer
        table.getColumn("Action").setCellRenderer((t, val, sel, foc, row, col) -> {
            JButton b = UIFactory.primaryButton("Edit");
            b.setFont(Theme.FONT_BADGE);
            return b;
        });
        table.getColumn("Action").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            JButton btn = UIFactory.primaryButton("Edit");
            { btn.addActionListener(e -> editSelected()); }
            public java.awt.Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) { return btn; }
            public Object getCellEditorValue() { return "Edit"; }
        });

        card.add(UIFactory.tableScrollPane(table), BorderLayout.CENTER);
        outer.add(card, BorderLayout.CENTER);
        return outer;
    }

    private void loadStaffCombo() {
        try {
            List<Staff> list = new StaffDAO().getAllStaff();
            cmbAssignedTo.removeAllItems();
            cmbAssignedTo.addItem("-- Unassigned --");
            for (Staff s : list)
                cmbAssignedTo.addItem(s.getStaffId() + " | " + s.getFullName() + " — " + s.getDesignation());
        } catch (Exception e) { Logger.log("MaintenanceForm", "Error loading staff.", e); }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT mr.request_id, mr.location, mr.issue_type, mr.status, " +
                "mr.reported_date, mr.cost FROM maintenance_requests mr ORDER BY mr.reported_date DESC");
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getDate(5),
                    rs.getDouble(6) > 0 ? String.format("%.0f", rs.getDouble(6)) : "—", "Edit"
                });
            }
        } catch (SQLException e) { Logger.log("MaintenanceForm", "Load error.", e); }
    }

    private void saveRequest() {
        String loc = txtLocation.getText().trim();
        String issue = txtIssueType.getText().trim();
        String desc = txtDescription.getText().trim();
        if (loc.isEmpty() || issue.isEmpty() || desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Location, Issue Type and Description are required.");
            return;
        }
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            double cost = 0;
            try { cost = Double.parseDouble(txtCost.getText().trim()); } catch (Exception ignored) {}
            String status = (String) cmbStatus.getSelectedItem();
            int assignedTo = 0;
            String ai = (String) cmbAssignedTo.getSelectedItem();
            if (ai != null && !ai.startsWith("--")) assignedTo = Integer.parseInt(ai.split("\\|")[0].trim());

            if (selectedId == -1) {
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO maintenance_requests (location,issue_type,description,status,reported_date,cost" +
                    (assignedTo>0?",assigned_to":"") + ") VALUES (?,?,?,?,CURRENT_DATE,?" + (assignedTo>0?",?":"") + ")");
                ps.setString(1,loc); ps.setString(2,issue); ps.setString(3,desc);
                ps.setString(4,status); ps.setDouble(5,cost);
                if (assignedTo>0) ps.setInt(6,assignedTo);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Request saved!");
            } else {
                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE maintenance_requests SET location=?,issue_type=?,description=?,status=?,cost=? WHERE request_id=?");
                ps.setString(1,loc); ps.setString(2,issue); ps.setString(3,desc);
                ps.setString(4,status); ps.setDouble(5,cost); ps.setInt(6,selectedId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Request updated!");
            }
            clearForm(); loadData();
        } catch (Exception e) {
            Logger.log("MaintenanceForm","Save error.",e);
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage());
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedId = (int) tableModel.getValueAt(row, 0);
        txtLocation.setText((String) tableModel.getValueAt(row, 1));
        txtIssueType.setText((String) tableModel.getValueAt(row, 2));
        cmbStatus.setSelectedItem(tableModel.getValueAt(row, 3));
        lblFormTitle.setText("Edit Request #" + selectedId);
        btnSave.setText("Update");
    }

    private void clearForm() {
        selectedId = -1;
        txtLocation.setText(""); txtIssueType.setText("");
        txtDescription.setText(""); txtCost.setText("0");
        cmbStatus.setSelectedIndex(0); cmbAssignedTo.setSelectedIndex(0);
        lblFormTitle.setText("New Maintenance Request");
        btnSave.setText("Save Request");
    }
}
