package ui;

import ui.util.Theme;
import ui.util.UIFactory;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ComplaintForm — log and resolve complaints / maintenance requests.
 *
 * Columns: #, Ticket No., Reported By, Room, Category, Priority, Date, Status, Assigned To
 * Same panel for Add and Edit.
 */
public class ComplaintForm extends JPanel {

    // ── Table ─────────────────────────────────────────────────────────────────
    private static final String[] COLS = {
        "#", "Ticket No.", "Reported By", "Room", "Category",
        "Priority", "Date Reported", "Status", "Assigned To"
    };
    private final DefaultTableModel tableModel = new DefaultTableModel(COLS, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    // ── Form fields ───────────────────────────────────────────────────────────
    private final JTextField        txTicketNo   = UIFactory.textField(15);
    private final JTextField        txReportedBy = UIFactory.textField(20);
    private final JTextField        txRoom       = UIFactory.textField(10);
    private final JComboBox<String> cmbCategory  = UIFactory.comboBox(
        "Plumbing", "Electrical", "Internet/WiFi", "Cleanliness",
        "Furniture", "Security", "Noise", "Other");
    private final JComboBox<String> cmbPriority  = UIFactory.comboBox(
        "Low", "Medium", "High", "Critical");
    private final JTextField        txDateReported = UIFactory.textField(12);
    private final JTextField        txDateResolved = UIFactory.textField(12);
    private final JComboBox<String> cmbStatus    = UIFactory.comboBox(
        "Open", "In Progress", "Resolved", "Closed", "Rejected");
    private final JTextField        txAssignedTo = UIFactory.textField(20);
    private final JTextArea         txDescription= UIFactory.textArea(4, 25);
    private final JTextArea         txResolution = UIFactory.textArea(3, 25);

    // ── Toolbar ───────────────────────────────────────────────────────────────
    private final JButton    btnAdd    = UIFactory.primaryButton("+ New Complaint");
    private final JButton    btnSave   = UIFactory.primaryButton("💾 Save");
    private final JButton    btnCancel = UIFactory.secondaryButton("Cancel");
    private final JButton    btnDelete = UIFactory.dangerButton("🗑 Delete");
    private final JTextField txSearch  = UIFactory.textField(18);

    // Filter radio buttons
    private final JRadioButton rbAll        = new JRadioButton("All",        true);
    private final JRadioButton rbOpen       = new JRadioButton("Open");
    private final JRadioButton rbInProgress = new JRadioButton("In Progress");
    private final JRadioButton rbResolved   = new JRadioButton("Resolved");

    private boolean editMode   = false;
    private int     selectedRow= -1;

    // ── Constructor ───────────────────────────────────────────────────────────
    public ComplaintForm() {
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

        JLabel title = UIFactory.sectionHeader("🔧  Complaints & Maintenance");

        // Filter tabs row
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbAll); bg.add(rbOpen); bg.add(rbInProgress); bg.add(rbResolved);

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterRow.setOpaque(false);
        filterRow.add(rbAll); filterRow.add(rbOpen);
        filterRow.add(rbInProgress); filterRow.add(rbResolved);

        JPanel left = new JPanel(new BorderLayout(0, 6));
        left.setOpaque(false);
        left.add(title, BorderLayout.NORTH);
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
        for (JRadioButton rb : new JRadioButton[]{rbAll, rbOpen, rbInProgress, rbResolved}) {
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
        colorPriorityColumn();

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

        addCardHeader(card, "Complaint Details");

        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 10));
        grid.setOpaque(false);
        grid.setAlignmentX(LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 330));

        grid.add(labeledField("Ticket No.",     txTicketNo));
        grid.add(labeledField("Reported By",    txReportedBy));
        grid.add(labeledField("Room No.",       txRoom));
        grid.add(labeledField("Category",       cmbCategory));
        grid.add(labeledField("Priority",       cmbPriority));
        grid.add(labeledField("Status",         cmbStatus));
        grid.add(labeledField("Date Reported",  txDateReported));
        grid.add(labeledField("Date Resolved",  txDateResolved));
        grid.add(labeledField("Assigned To",    txAssignedTo));

        card.add(grid);
        card.add(Box.createVerticalStrut(10));

        JLabel descLbl = UIFactory.sectionLabel("Description");
        descLbl.setAlignmentX(LEFT_ALIGNMENT);
        card.add(descLbl);
        JScrollPane descSp = new JScrollPane(txDescription);
        descSp.setAlignmentX(LEFT_ALIGNMENT);
        descSp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        descSp.setBorder(new LineBorder(Theme.BORDER_INPUT, 1, true));
        card.add(descSp);
        card.add(Box.createVerticalStrut(8));

        JLabel resLbl = UIFactory.sectionLabel("Resolution Notes");
        resLbl.setAlignmentX(LEFT_ALIGNMENT);
        card.add(resLbl);
        JScrollPane resSp = new JScrollPane(txResolution);
        resSp.setAlignmentX(LEFT_ALIGNMENT);
        resSp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        resSp.setBorder(new LineBorder(Theme.BORDER_INPUT, 1, true));
        card.add(resSp);
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
                    case "Resolved"    -> Theme.SUCCESS;
                    case "In Progress" -> Theme.PRIMARY;
                    case "Open"        -> Theme.WARNING;
                    case "Rejected"    -> Theme.DANGER;
                    default            -> Theme.TEXT_SECONDARY;
                });
                setFont(Theme.FONT_TABLE_HDR);
                return this;
            }
        });
    }

    private void colorPriorityColumn() {
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                String s = val == null ? "" : val.toString();
                if (!sel)
                    setBackground(row % 2 == 0 ? Theme.BG_CARD : new Color(0xF8, 0xFA, 0xFC));
                setForeground(switch (s) {
                    case "Critical" -> Theme.DANGER;
                    case "High"     -> new Color(0xD9, 0x77, 0x06);
                    case "Medium"   -> Theme.PRIMARY;
                    default         -> Theme.TEXT_SECONDARY;
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
            setFormVisible(true); txTicketNo.requestFocus();
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
            if (r < 0) { JOptionPane.showMessageDialog(this, "Select a ticket first."); return; }
            if (JOptionPane.showConfirmDialog(this,
                    "Delete ticket " + tableModel.getValueAt(r, 1) + "?",
                    "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                tableModel.removeRow(r); setFormVisible(false);
            }
        });

        txSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { /* TODO: DAO filter */ }
        });

        ActionListener filterAction = e -> { /* TODO: filter table by status radio */ };
        rbAll.addActionListener(filterAction);
        rbOpen.addActionListener(filterAction);
        rbInProgress.addActionListener(filterAction);
        rbResolved.addActionListener(filterAction);
    }

    private boolean validateForm() {
        if (txTicketNo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ticket number is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            txTicketNo.requestFocus(); return false;
        }
        if (txReportedBy.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Reporter name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            txReportedBy.requestFocus(); return false;
        }
        return true;
    }

    private void clearForm() {
        txTicketNo.setText(""); txReportedBy.setText(""); txRoom.setText("");
        txAssignedTo.setText(""); txDateReported.setText(""); txDateResolved.setText("");
        txDescription.setText(""); txResolution.setText("");
        cmbCategory.setSelectedIndex(0); cmbPriority.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
    }

    private void populateForm(int row) {
        txTicketNo.setText(tableModel.getValueAt(row, 1).toString());
        txReportedBy.setText(tableModel.getValueAt(row, 2).toString());
        txRoom.setText(tableModel.getValueAt(row, 3).toString());
        cmbCategory.setSelectedItem(tableModel.getValueAt(row, 4).toString());
        cmbPriority.setSelectedItem(tableModel.getValueAt(row, 5).toString());
        txDateReported.setText(tableModel.getValueAt(row, 6).toString());
        cmbStatus.setSelectedItem(tableModel.getValueAt(row, 7).toString());
        txAssignedTo.setText(tableModel.getValueAt(row, 8).toString());
    }

    private void addRow() {
        tableModel.addRow(new Object[]{
            tableModel.getRowCount() + 1,
            txTicketNo.getText().trim(),
            txReportedBy.getText().trim(),
            txRoom.getText().trim(),
            cmbCategory.getSelectedItem(),
            cmbPriority.getSelectedItem(),
            txDateReported.getText().trim(),
            cmbStatus.getSelectedItem(),
            txAssignedTo.getText().trim()
        });
    }

    private void updateRow(int row) {
        tableModel.setValueAt(txTicketNo.getText().trim(),    row, 1);
        tableModel.setValueAt(txReportedBy.getText().trim(),  row, 2);
        tableModel.setValueAt(txRoom.getText().trim(),        row, 3);
        tableModel.setValueAt(cmbCategory.getSelectedItem(),  row, 4);
        tableModel.setValueAt(cmbPriority.getSelectedItem(),  row, 5);
        tableModel.setValueAt(txDateReported.getText().trim(),row, 6);
        tableModel.setValueAt(cmbStatus.getSelectedItem(),    row, 7);
        tableModel.setValueAt(txAssignedTo.getText().trim(),  row, 8);
    }

    private void setFormVisible(boolean v) {
        for (Component c : getComponents())
            if (c instanceof JSplitPane sp) {
                sp.getRightComponent().setVisible(v);
                sp.setDividerLocation(v ? 0.60 : 1.0);
                sp.revalidate();
            }
    }

    // ── Demo data (replace with DAO.getComplaints()) ──────────────────────────
    private void loadDemoData() {
        Object[][] demo = {
            {1, "TK-001", "Ali Hassan",     "101-A", "Plumbing",     "High",   "01/10/2024", "Resolved",    "Maintenance Staff"},
            {2, "TK-002", "Sara Ahmed",     "102-A", "Internet/WiFi","Medium", "03/10/2024", "In Progress", "IT Team"},
            {3, "TK-003", "Fatima Malik",   "103-A", "Electrical",   "Critical","05/10/2024","Open",        "Electrician"},
            {4, "TK-004", "Hassan Mehmood", "202-B", "Noise",        "Low",    "07/10/2024", "Open",        "Warden"},
            {5, "TK-005", "Ali Hassan",     "101-A", "Cleanliness",  "Medium", "10/10/2024", "Resolved",    "Cleaner"},
            {6, "TK-006", "Sara Ahmed",     "102-A", "Furniture",    "Low",    "12/10/2024", "Closed",      "Maintenance Staff"},
        };
        for (Object[] r : demo) tableModel.addRow(r);
    }
}
