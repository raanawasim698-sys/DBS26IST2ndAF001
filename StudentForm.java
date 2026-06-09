package ui;

import ui.util.Theme;
import ui.util.UIFactory;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * StudentForm — add / edit / delete students.
 *
 * Layout:
 *   ┌─────────────────────────────────────────────────────────────┐
 *   │  Page title + toolbar (Search | + Add | Edit | Delete)      │
 *   ├──────────────────────────────┬──────────────────────────────┤
 *   │  Student table (left 60 %)   │  Detail / form panel (40 %)  │
 *   └──────────────────────────────┴──────────────────────────────┘
 *
 * The same panel is used for both Add and Edit operations.
 * Stub DAO calls are clearly marked for replacement.
 */
public class StudentForm extends JPanel {

    // ── Table ─────────────────────────────────────────────────────────────────
    private static final String[] COLS = {
        "#", "Student ID", "Full Name", "CNIC", "Phone", "Room", "Status"
    };
    private final DefaultTableModel tableModel = new DefaultTableModel(COLS, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    // ── Form fields ───────────────────────────────────────────────────────────
    private final JTextField  txStudentId  = UIFactory.textField(15);
    private final JTextField  txFullName   = UIFactory.textField(20);
    private final JTextField  txFatherName = UIFactory.textField(20);
    private final JTextField  txCnic       = UIFactory.textField(15);
    private final JTextField  txPhone      = UIFactory.textField(15);
    private final JTextField  txEmail      = UIFactory.textField(20);
    private final JComboBox<String> cmbGender =
        UIFactory.comboBox(new String[]{"Male", "Female", "Other"});
    private final JComboBox<String> cmbDept =
        UIFactory.comboBox(new String[]{"Computer Engineering", "Electrical Engineering",
                           "Mechanical Engineering", "Civil Engineering", "Other"});
    private final JSpinner    spnSemester  = UIFactory.spinner(1, 8, 1);
    private final JTextField  txAddress    = UIFactory.textField(25);
    private final JTextField  txEmergency  = UIFactory.textField(20);
    private final JComboBox<String> cmbStatus =
        UIFactory.comboBox(new String[]{"Active", "Inactive", "Graduated", "Suspended"});

    // ── Toolbar buttons ───────────────────────────────────────────────────────
    private final JButton btnAdd    = UIFactory.primaryButton("+ Add Student");
    private final JButton btnSave   = UIFactory.primaryButton("💾 Save");
    private final JButton btnCancel = UIFactory.secondaryButton("Cancel");
    private final JButton btnDelete = UIFactory.dangerButton("🗑 Delete");
    private final JTextField txSearch = UIFactory.textField(18);

    private boolean editMode = false;
    private int selectedRow = -1;

    // ── Constructor ───────────────────────────────────────────────────────────
    public StudentForm() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(buildTopBar(),   BorderLayout.NORTH);
        add(buildContent(),  BorderLayout.CENTER);

        wireEvents();
        loadDemoData();
        setFormVisible(false);
    }

    // ── Top bar (title + search + actions) ────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel title = UIFactory.sectionHeader("👥  Students");

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JLabel searchIco = new JLabel("🔍");
        txSearch.setPreferredSize(new Dimension(180, Theme.INPUT_H));
        txSearch.putClientProperty("JTextField.placeholderText", "Search students…");

        right.add(searchIco);
        right.add(txSearch);
        right.add(Box.createHorizontalStrut(8));
        right.add(btnAdd);
        right.add(btnDelete);

        bar.add(title, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Main content: table | form ────────────────────────────────────────────
    private JSplitPane buildContent() {
        UIFactory.styleTable(table);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = UIFactory.tableScrollPane(table);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, buildFormPanel());
        split.setDividerLocation(0.60);
        split.setResizeWeight(0.60);
        split.setBorder(null);
        split.setOpaque(false);
        return split;
    }

    // ── Form panel (right side) ───────────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(0, 16, 0, 0));

        JPanel card = UIFactory.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel hdr = new JLabel("Student Details");
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 15));
        hdr.setForeground(Theme.TEXT_PRIMARY);
        hdr.setAlignmentX(LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(Theme.BORDER);

        card.add(hdr);
        card.add(Box.createVerticalStrut(8));
        card.add(sep);
        card.add(Box.createVerticalStrut(12));

        // fields arranged in 2-column grid
        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 10));
        grid.setOpaque(false);
        grid.setAlignmentX(LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        addFieldPair(grid, "Student ID", txStudentId, "Full Name", txFullName);
        addFieldPair(grid, "Father Name", txFatherName, "CNIC", txCnic);
        addFieldPair(grid, "Phone", txPhone, "Email", txEmail);
        addFieldPair(grid, "Gender", cmbGender, "Department", cmbDept);
        addFieldPair(grid, "Semester", spnSemester, "Status", cmbStatus);
        addFieldPair(grid, "Address", txAddress, "Emergency Contact", txEmergency);

        card.add(grid);
        card.add(Box.createVerticalStrut(16));

        // action row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);
        btnRow.add(btnSave);
        btnRow.add(btnCancel);
        card.add(btnRow);

        outer.add(card, BorderLayout.CENTER);
        return outer;
    }

    private void addFieldPair(JPanel grid,
                               String lbl1, JComponent f1,
                               String lbl2, JComponent f2) {
        grid.add(labeledField(lbl1, f1));
        grid.add(labeledField(lbl2, f2));
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

    // ── Events ────────────────────────────────────────────────────────────────
    private void wireEvents() {
        // Add button: show blank form
        btnAdd.addActionListener(e -> {
            clearForm();
            editMode = false;
            setFormVisible(true);
            txStudentId.requestFocus();
        });

        // Table row selection → populate form for edit
        table.getSelectionModel().addListSelectionListener(ev -> {
            if (ev.getValueIsAdjusting()) return;
            selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                populateFormFromRow(selectedRow);
                editMode = true;
                setFormVisible(true);
            }
        });

        // Save
        btnSave.addActionListener(e -> {
            if (!validateForm()) return;
            if (editMode && selectedRow >= 0) {
                updateTableRow(selectedRow);
            } else {
                addTableRow();
            }
            setFormVisible(false);
            clearForm();
        });

        // Cancel
        btnCancel.addActionListener(e -> { setFormVisible(false); clearForm(); });

        // Delete
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a student first."); return; }
            int c = JOptionPane.showConfirmDialog(this, "Delete this student?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                tableModel.removeRow(row);
                setFormVisible(false);
            }
        });

        // Live search filter
        txSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { filterTable(txSearch.getText()); }
        });
    }

    private boolean validateForm() {
        if (txStudentId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            txStudentId.requestFocus(); return false;
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
        txStudentId.setText(""); txFullName.setText(""); txFatherName.setText("");
        txCnic.setText(""); txPhone.setText(""); txEmail.setText("");
        txAddress.setText(""); txEmergency.setText("");
        cmbGender.setSelectedIndex(0); cmbDept.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0); spnSemester.setValue(1);
    }

    private void setFormVisible(boolean v) {
        // find the form panel inside the split pane
        for (Component c : getComponents()) {
            if (c instanceof JSplitPane sp) {
                sp.getRightComponent().setVisible(v);
                sp.setDividerLocation(v ? 0.60 : 1.0);
                sp.revalidate();
            }
        }
    }

    private void populateFormFromRow(int row) {
        txStudentId.setText(tableModel.getValueAt(row, 1).toString());
        txFullName.setText(tableModel.getValueAt(row, 2).toString());
        txCnic.setText(tableModel.getValueAt(row, 3).toString());
        txPhone.setText(tableModel.getValueAt(row, 4).toString());
    }

    private void addTableRow() {
        int next = tableModel.getRowCount() + 1;
        tableModel.addRow(new Object[]{
            next,
            txStudentId.getText().trim(),
            txFullName.getText().trim(),
            txCnic.getText().trim(),
            txPhone.getText().trim(),
            "—",
            cmbStatus.getSelectedItem()
        });
    }

    private void updateTableRow(int row) {
        tableModel.setValueAt(txStudentId.getText().trim(), row, 1);
        tableModel.setValueAt(txFullName.getText().trim(),  row, 2);
        tableModel.setValueAt(txCnic.getText().trim(),      row, 3);
        tableModel.setValueAt(txPhone.getText().trim(),     row, 4);
        tableModel.setValueAt(cmbStatus.getSelectedItem(),  row, 6);
    }

    private void filterTable(String query) {
        // TODO: replace with DAO-based search; currently UI-only filter
        // For real implementation: reload tableModel from DAO with LIKE '%query%'
    }

    // ── Demo data (replace with DAO.getAllStudents()) ─────────────────────────
    private void loadDemoData() {
        Object[][] demo = {
            {1, "S-2024-001", "Ali Hassan",      "35202-1234567-1", "0312-3456789", "101-A", "Active"},
            {2, "S-2024-002", "Sara Ahmed",      "35202-7654321-2", "0321-9876543", "102-B", "Active"},
            {3, "S-2024-003", "Usman Tariq",     "35202-1112233-3", "0333-1122334", "—",     "Inactive"},
            {4, "S-2024-004", "Fatima Malik",    "35202-9988776-4", "0345-5566778", "103-A", "Active"},
            {5, "S-2024-005", "Ibrahim Khan",    "35202-4455667-5", "0300-4455667", "104-B", "Graduated"},
            {6, "S-2024-006", "Ayesha Raza",     "35202-2233445-6", "0311-2233445", "105-A", "Active"},
            {7, "S-2024-007", "Hassan Mehmood",  "35202-3344556-7", "0322-3344556", "106-B", "Active"},
        };
        for (Object[] row : demo) tableModel.addRow(row);
    }
}
