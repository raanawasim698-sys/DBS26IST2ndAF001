package ui;

import ui.util.Theme;
import ui.util.UIFactory;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * StaffForm — add / edit / delete hostel staff members.
 *
 * Columns: #, Staff ID, Full Name, CNIC, Role, Phone, Shift, Join Date, Status
 * Same panel used for Add and Edit.
 * Stub DAO calls marked for replacement.
 */
public class StaffForm extends JPanel {

    // ── Table ─────────────────────────────────────────────────────────────────
    private static final String[] COLS = {
        "#", "Staff ID", "Full Name", "CNIC", "Role", "Phone", "Shift", "Join Date", "Status"
    };
    private final DefaultTableModel tableModel = new DefaultTableModel(COLS, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    // ── Form fields ───────────────────────────────────────────────────────────
    private final JTextField        txStaffId    = UIFactory.textField(15);
    private final JTextField        txFullName   = UIFactory.textField(20);
    private final JTextField        txFatherName = UIFactory.textField(20);
    private final JTextField        txCnic       = UIFactory.textField(15);
    private final JTextField        txPhone      = UIFactory.textField(15);
    private final JTextField        txEmail      = UIFactory.textField(20);
    private final JComboBox<String> cmbRole      = UIFactory.comboBox(
        "Warden", "Assistant Warden", "Guard", "Cleaner",
        "Electrician", "Plumber", "Cook", "Administrator", "Other");
    private final JComboBox<String> cmbShift     = UIFactory.comboBox(
        "Morning (6AM-2PM)", "Afternoon (2PM-10PM)", "Night (10PM-6AM)", "Full Day");
    private final JComboBox<String> cmbGender    = UIFactory.comboBox(new String[]{"Male", "Female", "Other"});
    private final JTextField        txSalary     = UIFactory.textField(12);
    private final JTextField        txJoinDate   = UIFactory.textField(12);
    private final JTextField        txAddress    = UIFactory.textField(25);
    private final JTextField        txEmergency  = UIFactory.textField(20);
    private final JComboBox<String> cmbStatus    = UIFactory.comboBox(
        "Active", "On Leave", "Resigned", "Terminated");
    private final JTextArea         txNotes      = UIFactory.textArea(3, 20);

    // ── Toolbar ───────────────────────────────────────────────────────────────
    private final JButton    btnAdd    = UIFactory.primaryButton("+ Add Staff");
    private final JButton    btnSave   = UIFactory.primaryButton("💾 Save");
    private final JButton    btnCancel = UIFactory.secondaryButton("Cancel");
    private final JButton    btnDelete = UIFactory.dangerButton("🗑 Delete");
    private final JTextField txSearch  = UIFactory.textField(18);

    // Role filter radio buttons
    private final JRadioButton rbAll     = new JRadioButton("All",     true);
    private final JRadioButton rbWarden  = new JRadioButton("Warden");
    private final JRadioButton rbGuard   = new JRadioButton("Guard");
    private final JRadioButton rbOther   = new JRadioButton("Other");

    private boolean editMode   = false;
    private int     selectedRow= -1;

    // ── Constructor ───────────────────────────────────────────────────────────
    public StaffForm() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        styleRadioButtons();
        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);

        wireEvents();
        loadDemoData();
        setFormVisible(false);
    }

    // ── Top bar ───────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel title = UIFactory.sectionHeader("👷  Staff Management");

        ButtonGroup bg = new ButtonGroup();
        bg.add(rbAll); bg.add(rbWarden); bg.add(rbGuard); bg.add(rbOther);

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterRow.setOpaque(false);
        filterRow.add(rbAll); filterRow.add(rbWarden);
        filterRow.add(rbGuard); filterRow.add(rbOther);

        JPanel left = new JPanel(new BorderLayout(0, 6));
        left.setOpaque(false);
        left.add(title,     BorderLayout.NORTH);
        left.add(filterRow, BorderLayout.SOUTH);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        txSearch.setPreferredSize(new Dimension(180, Theme.INPUT_H));
        right.add(new JLabel("🔍"));
        right.add(txSearch);
        right.add(Box.createHorizontalStrut(8));
        right.add(btnAdd);
        right.add(btnDelete);

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private void styleRadioButtons() {
        for (JRadioButton rb : new JRadioButton[]{rbAll, rbWarden, rbGuard, rbOther}) {
            rb.setFont(Theme.FONT_LABEL);
            rb.setOpaque(false);
            rb.setForeground(Theme.TEXT_PRIMARY);
            rb.setFocusPainted(false);
        }
    }

    // ── Content ───────────────────────────────────────────────────────────────
    private JSplitPane buildContent() {
        UIFactory.styleTable(table);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        colorStatusColumn();
        colorShiftColumn();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            UIFactory.tableScrollPane(table), buildFormPanel());
        split.setDividerLocation(0.60);
        split.setResizeWeight(0.60);
        split.setBorder(null);
        split.setOpaque(false);
        return split;
    }

    // ── Form panel ────────────────────────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(0, 16, 0, 0));

        JPanel card = UIFactory.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        addCardHeader(card, "Staff Details");

        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 10));
        grid.setOpaque(false);
        grid.setAlignmentX(LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

        grid.add(labeledField("Staff ID",          txStaffId));
        grid.add(labeledField("Full Name",          txFullName));
        grid.add(labeledField("Father Name",        txFatherName));
        grid.add(labeledField("CNIC",               txCnic));
        grid.add(labeledField("Phone",              txPhone));
        grid.add(labeledField("Email",              txEmail));
        grid.add(labeledField("Role",               cmbRole));
        grid.add(labeledField("Shift",              cmbShift));
        grid.add(labeledField("Gender",             cmbGender));
        grid.add(labeledField("Salary (PKR)",       txSalary));
        grid.add(labeledField("Join Date (DD/MM/YYYY)", txJoinDate));
        grid.add(labeledField("Status",             cmbStatus));
        grid.add(labeledField("Address",            txAddress));
        grid.add(labeledField("Emergency Contact",  txEmergency));

        card.add(grid);
        card.add(Box.createVerticalStrut(10));

        JLabel notesLbl = UIFactory.sectionLabel("Notes");
        notesLbl.setAlignmentX(LEFT_ALIGNMENT);
        card.add(notesLbl);

        JScrollPane notesSp = new JScrollPane(txNotes);
        notesSp.setAlignmentX(LEFT_ALIGNMENT);
        notesSp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        notesSp.setBorder(new LineBorder(Theme.BORDER_FOCUS, 1, true));
        card.add(notesSp);
        card.add(Box.createVerticalStrut(14));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);
        btnRow.add(btnSave);
        btnRow.add(btnCancel);
        card.add(btnRow);

        outer.add(card, BorderLayout.CENTER);
        return outer;
    }

    private void addCardHeader(JPanel card, String text) {
        JLabel hdr = new JLabel(text);
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 15));
        hdr.setForeground(Theme.TEXT_PRIMARY);
        hdr.setAlignmentX(LEFT_ALIGNMENT);
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(Theme.BORDER);
        card.add(hdr);
        card.add(Box.createVerticalStrut(6));
        card.add(sep);
        card.add(Box.createVerticalStrut(12));
    }

    private JPanel labeledField(String lbl, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel l = UIFactory.boldLabel(lbl);
        l.setAlignmentX(LEFT_ALIGNMENT);
        field.setAlignmentX(LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.INPUT_H));
        p.add(l);
        p.add(Box.createVerticalStrut(3));
        p.add(field);
        return p;
    }

    // ── Coloured cell renderers ───────────────────────────────────────────────
    private void colorStatusColumn() {
        table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                String s = val == null ? "" : val.toString();
                if (!sel)
                    setBackground(row % 2 == 0 ? Theme.BG_CARD : new Color(0xF8, 0xFA, 0xFC));
                setForeground(switch (s) {
                    case "Active"     -> Theme.SUCCESS;
                    case "On Leave"   -> Theme.WARNING;
                    case "Resigned"   -> Theme.TEXT_SECONDARY;
                    case "Terminated" -> Theme.DANGER;
                    default           -> Theme.TEXT_PRIMARY;
                });
                setFont(Theme.FONT_TABLE_HDR);
                return this;
            }
        });
    }

    private void colorShiftColumn() {
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!sel)
                    setBackground(row % 2 == 0 ? Theme.BG_CARD : new Color(0xF8, 0xFA, 0xFC));
                setForeground(Theme.TEXT_SECONDARY);
                return this;
            }
        });
    }

    // ── Events ────────────────────────────────────────────────────────────────
    private void wireEvents() {
        btnAdd.addActionListener(e -> {
            clearForm(); editMode = false;
            setFormVisible(true); txStaffId.requestFocus();
        });

        table.getSelectionModel().addListSelectionListener(ev -> {
            if (ev.getValueIsAdjusting()) return;
            selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                populateForm(selectedRow); editMode = true; setFormVisible(true);
            }
        });

        btnSave.addActionListener(e -> {
            if (!validateForm()) return;
            if (editMode && selectedRow >= 0) updateRow(selectedRow); else addRow();
            setFormVisible(false); clearForm();
        });

        btnCancel.addActionListener(e -> { setFormVisible(false); clearForm(); });

        btnDelete.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) { JOptionPane.showMessageDialog(this, "Select a staff member first."); return; }
            if (JOptionPane.showConfirmDialog(this,
                    "Delete staff member " + tableModel.getValueAt(r, 2) + "?",
                    "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                tableModel.removeRow(r); setFormVisible(false);
            }
        });

        txSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { /* TODO: DAO filter */ }
        });

        ActionListener filterAction = e -> { /* TODO: filter table by role radio */ };
        rbAll.addActionListener(filterAction);
        rbWarden.addActionListener(filterAction);
        rbGuard.addActionListener(filterAction);
        rbOther.addActionListener(filterAction);
    }

    private boolean validateForm() {
        if (txStaffId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Staff ID is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            txStaffId.requestFocus(); return false;
        }
        if (txFullName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full Name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            txFullName.requestFocus(); return false;
        }
        if (txPhone.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            txPhone.requestFocus(); return false;
        }
        return true;
    }

    private void clearForm() {
        txStaffId.setText(""); txFullName.setText(""); txFatherName.setText("");
        txCnic.setText(""); txPhone.setText(""); txEmail.setText("");
        txSalary.setText(""); txJoinDate.setText(""); txAddress.setText("");
        txEmergency.setText(""); txNotes.setText("");
        cmbRole.setSelectedIndex(0); cmbShift.setSelectedIndex(0);
        cmbGender.setSelectedIndex(0); cmbStatus.setSelectedIndex(0);
    }

    private void populateForm(int row) {
        txStaffId.setText(tableModel.getValueAt(row, 1).toString());
        txFullName.setText(tableModel.getValueAt(row, 2).toString());
        txCnic.setText(tableModel.getValueAt(row, 3).toString());
        cmbRole.setSelectedItem(tableModel.getValueAt(row, 4).toString());
        txPhone.setText(tableModel.getValueAt(row, 5).toString());
        cmbShift.setSelectedItem(tableModel.getValueAt(row, 6).toString());
        txJoinDate.setText(tableModel.getValueAt(row, 7).toString());
        cmbStatus.setSelectedItem(tableModel.getValueAt(row, 8).toString());
    }

    private void addRow() {
        tableModel.addRow(new Object[]{
            tableModel.getRowCount() + 1,
            txStaffId.getText().trim(),
            txFullName.getText().trim(),
            txCnic.getText().trim(),
            cmbRole.getSelectedItem(),
            txPhone.getText().trim(),
            cmbShift.getSelectedItem(),
            txJoinDate.getText().trim(),
            cmbStatus.getSelectedItem()
        });
    }

    private void updateRow(int row) {
        tableModel.setValueAt(txStaffId.getText().trim(),   row, 1);
        tableModel.setValueAt(txFullName.getText().trim(),  row, 2);
        tableModel.setValueAt(txCnic.getText().trim(),      row, 3);
        tableModel.setValueAt(cmbRole.getSelectedItem(),    row, 4);
        tableModel.setValueAt(txPhone.getText().trim(),     row, 5);
        tableModel.setValueAt(cmbShift.getSelectedItem(),   row, 6);
        tableModel.setValueAt(txJoinDate.getText().trim(),  row, 7);
        tableModel.setValueAt(cmbStatus.getSelectedItem(),  row, 8);
    }

    private void setFormVisible(boolean v) {
        for (Component c : getComponents())
            if (c instanceof JSplitPane sp) {
                sp.getRightComponent().setVisible(v);
                sp.setDividerLocation(v ? 0.60 : 1.0);
                sp.revalidate();
            }
    }

    // ── Demo data (replace with DAO.getAllStaff()) ────────────────────────────
    private void loadDemoData() {
        Object[][] demo = {
            {1, "ST-001", "Muhammad Asif",    "35202-1010101-1", "Warden",           "0300-1010101", "Full Day",             "01/01/2020", "Active"},
            {2, "ST-002", "Tariq Hussain",    "35202-2020202-2", "Assistant Warden", "0311-2020202", "Morning (6AM-2PM)",    "15/03/2021", "Active"},
            {3, "ST-003", "Rashid Khan",      "35202-3030303-3", "Guard",            "0333-3030303", "Night (10PM-6AM)",     "01/07/2021", "Active"},
            {4, "ST-004", "Zubair Ahmed",     "35202-4040404-4", "Guard",            "0322-4040404", "Afternoon (2PM-10PM)","01/07/2021", "Active"},
            {5, "ST-005", "Nadia Bibi",       "35202-5050505-5", "Cleaner",          "0345-5050505", "Morning (6AM-2PM)",    "10/09/2022", "Active"},
            {6, "ST-006", "Imran Electrician","35202-6060606-6", "Electrician",      "0312-6060606", "Full Day",             "20/11/2022", "On Leave"},
            {7, "ST-007", "Ghulam Fareed",    "35202-7070707-7", "Cook",             "0300-7070707", "Morning (6AM-2PM)",    "01/01/2023", "Active"},
        };
        for (Object[] r : demo) tableModel.addRow(r);
    }
}
