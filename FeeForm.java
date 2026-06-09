package ui;

import ui.util.Theme;
import ui.util.UIFactory;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * FeeForm — track hostel fee payments.
 *
 * Columns: #, Receipt No., Student ID, Student Name, Room, Month, Amount, Paid, Balance, Status
 * Same panel for Add and Edit.
 * Stub DAO calls marked for replacement.
 */
public class FeeForm extends JPanel {

    // ── Table ─────────────────────────────────────────────────────────────────
    private static final String[] COLS = {
        "#", "Receipt No.", "Student ID", "Student Name",
        "Room", "Month", "Amount (PKR)", "Paid (PKR)", "Balance", "Status"
    };
    private final DefaultTableModel tableModel = new DefaultTableModel(COLS, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    // ── Form fields ───────────────────────────────────────────────────────────
    private final JTextField        txReceiptNo  = UIFactory.textField(15);
    private final JTextField        txStudentId  = UIFactory.textField(15);
    private final JTextField        txStudentName= UIFactory.textField(20);
    private final JTextField        txRoom       = UIFactory.textField(10);
    private final JComboBox<String> cmbMonth     = UIFactory.comboBox(
        "January","February","March","April","May","June",
        "July","August","September","October","November","December");
    private final JTextField        txYear       = UIFactory.textField(6);
    private final JTextField        txAmount     = UIFactory.textField(12);
    private final JTextField        txPaid       = UIFactory.textField(12);
    private final JComboBox<String> cmbPayMethod = UIFactory.comboBox(
        "Cash", "Bank Transfer", "Cheque", "Online");
    private final JTextField        txPayDate    = UIFactory.textField(12);
    private final JComboBox<String> cmbStatus    = UIFactory.comboBox(
        "Paid", "Partial", "Unpaid", "Overdue", "Waived");
    private final JTextArea         txNotes      = UIFactory.textArea(3, 20);

    // ── Toolbar ───────────────────────────────────────────────────────────────
    private final JButton    btnAdd    = UIFactory.primaryButton("+ Add Record");
    private final JButton    btnSave   = UIFactory.primaryButton("💾 Save");
    private final JButton    btnCancel = UIFactory.secondaryButton("Cancel");
    private final JButton    btnDelete = UIFactory.dangerButton("🗑 Delete");
    private final JTextField txSearch  = UIFactory.textField(18);

    private boolean editMode   = false;
    private int     selectedRow= -1;

    // ── Constructor ───────────────────────────────────────────────────────────
    public FeeForm() {
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

        JLabel title = UIFactory.sectionHeader("💰  Fee Management");

        // Summary badges
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.add(buildSummaryChip("Total Collected: PKR 3,24,000", Theme.SUCCESS));
        summaryPanel.add(buildSummaryChip("Pending: PKR 48,000", Theme.WARNING));
        summaryPanel.add(buildSummaryChip("Overdue: 3 students", Theme.DANGER));

        JPanel left = new JPanel(new BorderLayout(0, 6));
        left.setOpaque(false);
        left.add(title, BorderLayout.NORTH);
        left.add(summaryPanel, BorderLayout.SOUTH);

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

    private JLabel buildSummaryChip(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(color);
        l.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(color, 1, true),
            new EmptyBorder(2, 8, 2, 8)));
        l.setOpaque(false);
        return l;
    }

    // ── Content ───────────────────────────────────────────────────────────────
    private JSplitPane buildContent() {
        UIFactory.styleTable(table);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        colorStatusColumn();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            UIFactory.tableScrollPane(table), buildFormPanel());
        split.setDividerLocation(0.62);
        split.setResizeWeight(0.62);
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

        addCardHeader(card, "Fee Record");

        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 10));
        grid.setOpaque(false);
        grid.setAlignmentX(LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        grid.add(labeledField("Receipt No.",     txReceiptNo));
        grid.add(labeledField("Student ID",      txStudentId));
        grid.add(labeledField("Student Name",    txStudentName));
        grid.add(labeledField("Room No.",        txRoom));
        grid.add(labeledField("Month",           cmbMonth));
        grid.add(labeledField("Year",            txYear));
        grid.add(labeledField("Total Amount (PKR)", txAmount));
        grid.add(labeledField("Amount Paid (PKR)",  txPaid));
        grid.add(labeledField("Payment Method", cmbPayMethod));
        grid.add(labeledField("Payment Date",   txPayDate));
        grid.add(labeledField("Status",         cmbStatus));

        card.add(grid);
        card.add(Box.createVerticalStrut(10));

        JLabel notesLbl = UIFactory.sectionLabel("Notes");
        notesLbl.setAlignmentX(LEFT_ALIGNMENT);
        card.add(notesLbl);

        JScrollPane notesSp = new JScrollPane(txNotes);
        notesSp.setAlignmentX(LEFT_ALIGNMENT);
        notesSp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        notesSp.setBorder(new LineBorder(Theme.BORDER_INPUT, 1, true));
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

    private void colorStatusColumn() {
        table.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                String s = val == null ? "" : val.toString();
                if (!sel)
                    setBackground(row % 2 == 0 ? Theme.BG_CARD : new Color(0xF8, 0xFA, 0xFC));
                setForeground(switch (s) {
                    case "Paid"    -> Theme.SUCCESS;
                    case "Partial" -> Theme.WARNING;
                    case "Overdue" -> Theme.DANGER;
                    case "Waived"  -> Theme.TEXT_SECONDARY;
                    default        -> Theme.PRIMARY;
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
            setFormVisible(true); txReceiptNo.requestFocus();
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
            if (r < 0) { JOptionPane.showMessageDialog(this, "Select a record first."); return; }
            if (JOptionPane.showConfirmDialog(this,
                    "Delete receipt " + tableModel.getValueAt(r, 1) + "?",
                    "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                tableModel.removeRow(r); setFormVisible(false);
            }
        });

        // Auto-calculate balance when amount or paid changes
        txAmount.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { recalcBalance(); }
        });
        txPaid.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { recalcBalance(); }
        });

        txSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { /* TODO: DAO filter */ }
        });
    }

    private void recalcBalance() {
        try {
            double amt  = Double.parseDouble(txAmount.getText().trim().replace(",", ""));
            double paid = Double.parseDouble(txPaid.getText().trim().replace(",", ""));
            // balance shown in table on save; could add a live label here
        } catch (NumberFormatException ignored) {}
    }

    private boolean validateForm() {
        if (txReceiptNo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Receipt number is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            txReceiptNo.requestFocus(); return false;
        }
        if (txStudentId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            txStudentId.requestFocus(); return false;
        }
        if (txAmount.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Amount is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            txAmount.requestFocus(); return false;
        }
        return true;
    }

    private void clearForm() {
        txReceiptNo.setText(""); txStudentId.setText(""); txStudentName.setText("");
        txRoom.setText(""); txYear.setText(""); txAmount.setText("");
        txPaid.setText(""); txPayDate.setText(""); txNotes.setText("");
        cmbMonth.setSelectedIndex(0); cmbPayMethod.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
    }

    private void populateForm(int row) {
        txReceiptNo.setText(tableModel.getValueAt(row, 1).toString());
        txStudentId.setText(tableModel.getValueAt(row, 2).toString());
        txStudentName.setText(tableModel.getValueAt(row, 3).toString());
        txRoom.setText(tableModel.getValueAt(row, 4).toString());
        cmbStatus.setSelectedItem(tableModel.getValueAt(row, 9).toString());
    }

    private String calcBalance(String amount, String paid) {
        try {
            double a = Double.parseDouble(amount.replace(",", ""));
            double p = Double.parseDouble(paid.replace(",", ""));
            return String.format("%.0f", Math.max(0, a - p));
        } catch (NumberFormatException e) { return "0"; }
    }

    private void addRow() {
        String bal = calcBalance(txAmount.getText().trim(), txPaid.getText().trim());
        tableModel.addRow(new Object[]{
            tableModel.getRowCount() + 1,
            txReceiptNo.getText().trim(),
            txStudentId.getText().trim(),
            txStudentName.getText().trim(),
            txRoom.getText().trim(),
            cmbMonth.getSelectedItem() + " " + txYear.getText().trim(),
            txAmount.getText().trim(),
            txPaid.getText().trim(),
            bal,
            cmbStatus.getSelectedItem()
        });
    }

    private void updateRow(int row) {
        String bal = calcBalance(txAmount.getText().trim(), txPaid.getText().trim());
        tableModel.setValueAt(txReceiptNo.getText().trim(),   row, 1);
        tableModel.setValueAt(txStudentId.getText().trim(),   row, 2);
        tableModel.setValueAt(txStudentName.getText().trim(), row, 3);
        tableModel.setValueAt(txRoom.getText().trim(),        row, 4);
        tableModel.setValueAt(cmbMonth.getSelectedItem() + " " + txYear.getText().trim(), row, 5);
        tableModel.setValueAt(txAmount.getText().trim(),      row, 6);
        tableModel.setValueAt(txPaid.getText().trim(),        row, 7);
        tableModel.setValueAt(bal,                            row, 8);
        tableModel.setValueAt(cmbStatus.getSelectedItem(),    row, 9);
    }

    private void setFormVisible(boolean v) {
        for (Component c : getComponents())
            if (c instanceof JSplitPane sp) {
                sp.getRightComponent().setVisible(v);
                sp.setDividerLocation(v ? 0.62 : 1.0);
                sp.revalidate();
            }
    }

    // ── Demo data (replace with DAO.getFeeRecords()) ──────────────────────────
    private void loadDemoData() {
        Object[][] demo = {
            {1, "RC-001", "S-2024-001", "Ali Hassan",     "101-A", "September 2024", "8,000", "8,000", "0",     "Paid"},
            {2, "RC-002", "S-2024-002", "Sara Ahmed",     "102-A", "September 2024", "6,500", "6,500", "0",     "Paid"},
            {3, "RC-003", "S-2024-004", "Fatima Malik",   "103-A", "September 2024", "6,500", "3,000", "3,500", "Partial"},
            {4, "RC-004", "S-2024-007", "Hassan Mehmood", "202-B", "September 2024", "3,500", "0",     "3,500", "Overdue"},
            {5, "RC-005", "S-2024-001", "Ali Hassan",     "101-A", "October 2024",   "8,000", "8,000", "0",     "Paid"},
            {6, "RC-006", "S-2024-002", "Sara Ahmed",     "102-A", "October 2024",   "6,500", "6,500", "0",     "Paid"},
        };
        for (Object[] r : demo) tableModel.addRow(r);
    }
}
