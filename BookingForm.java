package ui;

import ui.util.Theme;
import ui.util.UIFactory;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * BookingForm — manage room allocations / bookings.
 *
 * Columns: #, Booking ID, Student ID, Student Name, Room No., Check-In, Check-Out, Status, Fee/Month
 * Same panel used for Add and Edit.
 * Stub DAO calls marked for replacement.
 */
public class BookingForm extends JPanel {

    // ── Table ─────────────────────────────────────────────────────────────────
    private static final String[] COLS = {
        "#", "Booking ID", "Student ID", "Student Name", "Room No.",
        "Check-In", "Check-Out", "Status", "Fee/Month"
    };
    private final DefaultTableModel tableModel = new DefaultTableModel(COLS, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    // ── Form fields ───────────────────────────────────────────────────────────
    private final JTextField        txBookingId  = UIFactory.textField(15);
    private final JTextField        txStudentId  = UIFactory.textField(15);
    private final JTextField        txStudentName= UIFactory.textField(20);
    private final JComboBox<String> cmbRoom      = UIFactory.comboBox(
        "101-A", "102-A", "103-A", "201-B", "202-B", "301-C", "401-D");
    private final JTextField        txCheckIn    = UIFactory.textField(12);
    private final JTextField        txCheckOut   = UIFactory.textField(12);
    private final JComboBox<String> cmbStatus    = UIFactory.comboBox(
        "Active", "Pending", "Checked-Out", "Cancelled");
    private final JTextField        txFee        = UIFactory.textField(10);
    private final JTextArea         txRemarks    = UIFactory.textArea(3, 20);

    // ── Toolbar ───────────────────────────────────────────────────────────────
    private final JButton    btnAdd    = UIFactory.primaryButton("+ New Booking");
    private final JButton    btnSave   = UIFactory.primaryButton("💾 Save");
    private final JButton    btnCancel = UIFactory.secondaryButton("Cancel");
    private final JButton    btnDelete = UIFactory.dangerButton("🗑 Delete");
    private final JTextField txSearch  = UIFactory.textField(18);

    private boolean editMode   = false;
    private int     selectedRow= -1;

    // ── Constructor ───────────────────────────────────────────────────────────
    public BookingForm() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

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

        JLabel title = UIFactory.sectionHeader("📋  Bookings & Allocations");

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        txSearch.setPreferredSize(new Dimension(180, Theme.INPUT_H));
        right.add(new JLabel("🔍"));
        right.add(txSearch);
        right.add(Box.createHorizontalStrut(8));
        right.add(btnAdd);
        right.add(btnDelete);

        bar.add(title, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Content ───────────────────────────────────────────────────────────────
    private JSplitPane buildContent() {
        UIFactory.styleTable(table);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        colorStatusColumn();

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

        addCardHeader(card, "Booking Details");

        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 10));
        grid.setOpaque(false);
        grid.setAlignmentX(LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));

        grid.add(labeledField("Booking ID",   txBookingId));
        grid.add(labeledField("Student ID",   txStudentId));
        grid.add(labeledField("Student Name", txStudentName));
        grid.add(labeledField("Room Number",  cmbRoom));
        grid.add(labeledField("Check-In Date (DD/MM/YYYY)",  txCheckIn));
        grid.add(labeledField("Check-Out Date (DD/MM/YYYY)", txCheckOut));
        grid.add(labeledField("Status",       cmbStatus));
        grid.add(labeledField("Fee/Month (PKR)", txFee));

        card.add(grid);
        card.add(Box.createVerticalStrut(12));

        JLabel remarkLbl = UIFactory.sectionLabel("Remarks");
        remarkLbl.setAlignmentX(LEFT_ALIGNMENT);
        card.add(remarkLbl);

        JScrollPane remarksSp = new JScrollPane(txRemarks);
        remarksSp.setAlignmentX(LEFT_ALIGNMENT);
        remarksSp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        remarksSp.setBorder(new LineBorder(Theme.BORDER_INPUT, 1, true));
        card.add(remarksSp);
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

    private void colorStatusColumn() {
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                String s = val == null ? "" : val.toString();
                if (!sel)
                    setBackground(row % 2 == 0 ? Theme.BG_CARD : new Color(0xF8, 0xFA, 0xFC));
                setForeground(switch (s) {
                    case "Active"       -> Theme.SUCCESS;
                    case "Pending"      -> Theme.WARNING;
                    case "Checked-Out"  -> Theme.TEXT_SECONDARY;
                    case "Cancelled"    -> Theme.DANGER;
                    default             -> Theme.TEXT_PRIMARY;
                });
                setFont(Theme.FONT_TABLE_HDR);
                return this;
            }
        });
    }

    // ── Events ────────────────────────────────────────────────────────────────
    private void wireEvents() {
        btnAdd.addActionListener(e -> {
            clearForm(); editMode = false;
            setFormVisible(true); txBookingId.requestFocus();
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
            if (r < 0) { JOptionPane.showMessageDialog(this, "Select a booking first."); return; }
            if (JOptionPane.showConfirmDialog(this,
                    "Delete Booking " + tableModel.getValueAt(r, 1) + "?",
                    "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                tableModel.removeRow(r); setFormVisible(false);
            }
        });

        txSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { /* TODO: DAO filter */ }
        });
    }

    private boolean validateForm() {
        if (txBookingId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Booking ID is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            txBookingId.requestFocus(); return false;
        }
        if (txStudentId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            txStudentId.requestFocus(); return false;
        }
        if (txCheckIn.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Check-In date is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            txCheckIn.requestFocus(); return false;
        }
        return true;
    }

    private void clearForm() {
        txBookingId.setText(""); txStudentId.setText(""); txStudentName.setText("");
        txCheckIn.setText(""); txCheckOut.setText(""); txFee.setText(""); txRemarks.setText("");
        cmbRoom.setSelectedIndex(0); cmbStatus.setSelectedIndex(0);
    }

    private void populateForm(int row) {
        txBookingId.setText(tableModel.getValueAt(row, 1).toString());
        txStudentId.setText(tableModel.getValueAt(row, 2).toString());
        txStudentName.setText(tableModel.getValueAt(row, 3).toString());
        cmbRoom.setSelectedItem(tableModel.getValueAt(row, 4).toString());
        txCheckIn.setText(tableModel.getValueAt(row, 5).toString());
        txCheckOut.setText(tableModel.getValueAt(row, 6).toString());
        cmbStatus.setSelectedItem(tableModel.getValueAt(row, 7).toString());
        txFee.setText(tableModel.getValueAt(row, 8).toString());
    }

    private void addRow() {
        tableModel.addRow(new Object[]{
            tableModel.getRowCount() + 1,
            txBookingId.getText().trim(),
            txStudentId.getText().trim(),
            txStudentName.getText().trim(),
            cmbRoom.getSelectedItem(),
            txCheckIn.getText().trim(),
            txCheckOut.getText().trim(),
            cmbStatus.getSelectedItem(),
            txFee.getText().trim()
        });
    }

    private void updateRow(int row) {
        tableModel.setValueAt(txBookingId.getText().trim(),   row, 1);
        tableModel.setValueAt(txStudentId.getText().trim(),   row, 2);
        tableModel.setValueAt(txStudentName.getText().trim(), row, 3);
        tableModel.setValueAt(cmbRoom.getSelectedItem(),      row, 4);
        tableModel.setValueAt(txCheckIn.getText().trim(),     row, 5);
        tableModel.setValueAt(txCheckOut.getText().trim(),    row, 6);
        tableModel.setValueAt(cmbStatus.getSelectedItem(),    row, 7);
        tableModel.setValueAt(txFee.getText().trim(),         row, 8);
    }

    private void setFormVisible(boolean v) {
        for (Component c : getComponents())
            if (c instanceof JSplitPane sp) {
                sp.getRightComponent().setVisible(v);
                sp.setDividerLocation(v ? 0.60 : 1.0);
                sp.revalidate();
            }
    }

    // ── Demo data (replace with DAO.getAllBookings()) ─────────────────────────
    private void loadDemoData() {
        Object[][] demo = {
            {1, "BK-001", "S-2024-001", "Ali Hassan",     "101-A", "01/09/2024", "30/06/2025", "Active",      "8,000"},
            {2, "BK-002", "S-2024-002", "Sara Ahmed",     "102-A", "01/09/2024", "30/06/2025", "Active",      "6,500"},
            {3, "BK-003", "S-2024-004", "Fatima Malik",   "103-A", "15/09/2024", "30/06/2025", "Active",      "6,500"},
            {4, "BK-004", "S-2024-005", "Ibrahim Khan",   "201-B", "01/02/2024", "31/07/2024", "Checked-Out", "5,000"},
            {5, "BK-005", "S-2024-007", "Hassan Mehmood", "202-B", "01/09/2024", "30/06/2025", "Active",      "3,500"},
            {6, "BK-006", "S-2024-003", "Usman Tariq",    "301-C", "01/09/2024", "",           "Cancelled",   "8,000"},
        };
        for (Object[] r : demo) tableModel.addRow(r);
    }
}
