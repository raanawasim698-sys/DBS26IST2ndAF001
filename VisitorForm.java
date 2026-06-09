package ui;

import ui.util.Theme;
import ui.util.UIFactory;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * VisitorForm — log and manage hostel visitors.
 *
 * Columns: #, Visit ID, Visitor Name, CNIC, Phone, Visiting Student, Room, Check-In, Check-Out, Status
 * Same panel used for Add and Edit.
 * Stub DAO calls marked for replacement.
 */
public class VisitorForm extends JPanel {

    // ── Table ─────────────────────────────────────────────────────────────────
    private static final String[] COLS = {
        "#", "Visit ID", "Visitor Name", "CNIC", "Phone",
        "Student Visited", "Room", "Check-In", "Check-Out", "Status"
    };
    private final DefaultTableModel tableModel = new DefaultTableModel(COLS, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    // ── Form fields ───────────────────────────────────────────────────────────
    private final JTextField        txVisitId       = UIFactory.textField(15);
    private final JTextField        txVisitorName   = UIFactory.textField(20);
    private final JTextField        txCnic          = UIFactory.textField(15);
    private final JTextField        txPhone         = UIFactory.textField(15);
    private final JComboBox<String> cmbRelation     = UIFactory.comboBox(
        "Parent", "Sibling", "Relative", "Friend", "Guardian", "Other");
    private final JTextField        txStudentId     = UIFactory.textField(15);
    private final JTextField        txStudentName   = UIFactory.textField(20);
    private final JTextField        txRoom          = UIFactory.textField(10);
    private final JTextField        txCheckIn       = UIFactory.textField(15);
    private final JTextField        txCheckOut      = UIFactory.textField(15);
    private final JComboBox<String> cmbPurpose      = UIFactory.comboBox(
        "Family Visit", "Document Delivery", "Emergency", "Pickup/Drop", "Other");
    private final JComboBox<String> cmbStatus       = UIFactory.comboBox(
        "Checked-In", "Checked-Out", "Denied");

    // ID type radio buttons
    private final JRadioButton rbCnic      = new JRadioButton("CNIC",       true);
    private final JRadioButton rbPassport  = new JRadioButton("Passport");
    private final JRadioButton rbDrivingLic= new JRadioButton("Driving Lic.");

    private final JTextArea txRemarks = UIFactory.textArea(3, 20);

    // ── Toolbar ───────────────────────────────────────────────────────────────
    private final JButton    btnAdd    = UIFactory.primaryButton("+ Log Visitor");
    private final JButton    btnSave   = UIFactory.primaryButton("💾 Save");
    private final JButton    btnCancel = UIFactory.secondaryButton("Cancel");
    private final JButton    btnDelete = UIFactory.dangerButton("🗑 Delete");
    private final JTextField txSearch  = UIFactory.textField(18);

    // Status filter radio buttons
    private final JRadioButton rbAll        = new JRadioButton("All",        true);
    private final JRadioButton rbCheckedIn  = new JRadioButton("Checked-In");
    private final JRadioButton rbCheckedOut = new JRadioButton("Checked-Out");
    private final JRadioButton rbDenied     = new JRadioButton("Denied");

    private boolean editMode   = false;
    private int     selectedRow= -1;

    // ── Constructor ───────────────────────────────────────────────────────────
    public VisitorForm() {
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

        JLabel title = UIFactory.sectionHeader("🚶  Visitor Log");

        ButtonGroup bg = new ButtonGroup();
        bg.add(rbAll); bg.add(rbCheckedIn); bg.add(rbCheckedOut); bg.add(rbDenied);

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterRow.setOpaque(false);
        filterRow.add(rbAll); filterRow.add(rbCheckedIn);
        filterRow.add(rbCheckedOut); filterRow.add(rbDenied);

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
        for (JRadioButton rb : new JRadioButton[]{
                rbAll, rbCheckedIn, rbCheckedOut, rbDenied,
                rbCnic, rbPassport, rbDrivingLic}) {
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

        addCardHeader(card, "Visitor Details");

        // Visitor info grid
        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 10));
        grid.setOpaque(false);
        grid.setAlignmentX(LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 370));

        grid.add(labeledField("Visit ID",        txVisitId));
        grid.add(labeledField("Visitor Name",    txVisitorName));
        grid.add(labeledField("Phone",           txPhone));
        grid.add(labeledField("Relation",        cmbRelation));
        grid.add(labeledField("Student ID",      txStudentId));
        grid.add(labeledField("Student Name",    txStudentName));
        grid.add(labeledField("Room No.",        txRoom));
        grid.add(labeledField("Purpose",         cmbPurpose));
        grid.add(labeledField("Check-In (DD/MM/YYYY HH:MM)", txCheckIn));
        grid.add(labeledField("Check-Out (DD/MM/YYYY HH:MM)", txCheckOut));
        grid.add(labeledField("Status",          cmbStatus));

        card.add(grid);
        card.add(Box.createVerticalStrut(10));

        // ID type section
        JLabel idTypeLbl = UIFactory.sectionLabel("ID Type");
        idTypeLbl.setAlignmentX(LEFT_ALIGNMENT);
        card.add(idTypeLbl);

        JPanel idRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        idRow.setOpaque(false);
        idRow.setAlignmentX(LEFT_ALIGNMENT);

        // Group the ID-type radios separately
        ButtonGroup idGroup = new ButtonGroup();
        idGroup.add(rbCnic); idGroup.add(rbPassport); idGroup.add(rbDrivingLic);

        JPanel idRowWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        idRowWrap.setOpaque(false);
        idRowWrap.setAlignmentX(LEFT_ALIGNMENT);
        idRowWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        idRowWrap.add(rbCnic); idRowWrap.add(rbPassport); idRowWrap.add(rbDrivingLic);
        card.add(idRowWrap);

        // CNIC field below radio
        JPanel cnicPanel = labeledField("ID Number", txCnic);
        cnicPanel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(Box.createVerticalStrut(6));
        card.add(cnicPanel);
        card.add(Box.createVerticalStrut(10));

        // Remarks
        JLabel remLbl = UIFactory.sectionLabel("Remarks");
        remLbl.setAlignmentX(LEFT_ALIGNMENT);
        card.add(remLbl);

        JScrollPane remSp = new JScrollPane(txRemarks);
        remSp.setAlignmentX(LEFT_ALIGNMENT);
        remSp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        remSp.setBorder(new LineBorder(Theme.BORDER_FOCUS, 1, true));
        card.add(remSp);
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
                    case "Checked-In"  -> Theme.SUCCESS;
                    case "Checked-Out" -> Theme.TEXT_SECONDARY;
                    case "Denied"      -> Theme.DANGER;
                    default            -> Theme.TEXT_PRIMARY;
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
            setFormVisible(true); txVisitId.requestFocus();
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
            if (r < 0) { JOptionPane.showMessageDialog(this, "Select a visitor record first."); return; }
            if (JOptionPane.showConfirmDialog(this,
                    "Delete visit record " + tableModel.getValueAt(r, 1) + "?",
                    "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                tableModel.removeRow(r); setFormVisible(false);
            }
        });

        txSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { /* TODO: DAO filter */ }
        });

        ActionListener filterAction = e -> { /* TODO: filter table by status radio */ };
        rbAll.addActionListener(filterAction);
        rbCheckedIn.addActionListener(filterAction);
        rbCheckedOut.addActionListener(filterAction);
        rbDenied.addActionListener(filterAction);
    }

    private boolean validateForm() {
        if (txVisitId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Visit ID is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            txVisitId.requestFocus(); return false;
        }
        if (txVisitorName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Visitor Name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            txVisitorName.requestFocus(); return false;
        }
        if (txPhone.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            txPhone.requestFocus(); return false;
        }
        if (txCheckIn.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Check-In time is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            txCheckIn.requestFocus(); return false;
        }
        return true;
    }

    private void clearForm() {
        txVisitId.setText(""); txVisitorName.setText(""); txCnic.setText("");
        txPhone.setText(""); txStudentId.setText(""); txStudentName.setText("");
        txRoom.setText(""); txCheckIn.setText(""); txCheckOut.setText("");
        txRemarks.setText("");
        cmbRelation.setSelectedIndex(0); cmbPurpose.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0); rbCnic.setSelected(true);
    }

    private void populateForm(int row) {
        txVisitId.setText(tableModel.getValueAt(row, 1).toString());
        txVisitorName.setText(tableModel.getValueAt(row, 2).toString());
        txCnic.setText(tableModel.getValueAt(row, 3).toString());
        txPhone.setText(tableModel.getValueAt(row, 4).toString());
        txStudentName.setText(tableModel.getValueAt(row, 5).toString());
        txRoom.setText(tableModel.getValueAt(row, 6).toString());
        txCheckIn.setText(tableModel.getValueAt(row, 7).toString());
        txCheckOut.setText(tableModel.getValueAt(row, 8).toString());
        cmbStatus.setSelectedItem(tableModel.getValueAt(row, 9).toString());
    }

    private void addRow() {
        tableModel.addRow(new Object[]{
            tableModel.getRowCount() + 1,
            txVisitId.getText().trim(),
            txVisitorName.getText().trim(),
            txCnic.getText().trim(),
            txPhone.getText().trim(),
            txStudentName.getText().trim(),
            txRoom.getText().trim(),
            txCheckIn.getText().trim(),
            txCheckOut.getText().trim(),
            cmbStatus.getSelectedItem()
        });
    }

    private void updateRow(int row) {
        tableModel.setValueAt(txVisitId.getText().trim(),     row, 1);
        tableModel.setValueAt(txVisitorName.getText().trim(), row, 2);
        tableModel.setValueAt(txCnic.getText().trim(),        row, 3);
        tableModel.setValueAt(txPhone.getText().trim(),       row, 4);
        tableModel.setValueAt(txStudentName.getText().trim(), row, 5);
        tableModel.setValueAt(txRoom.getText().trim(),        row, 6);
        tableModel.setValueAt(txCheckIn.getText().trim(),     row, 7);
        tableModel.setValueAt(txCheckOut.getText().trim(),    row, 8);
        tableModel.setValueAt(cmbStatus.getSelectedItem(),    row, 9);
    }

    private void setFormVisible(boolean v) {
        for (Component c : getComponents())
            if (c instanceof JSplitPane sp) {
                sp.getRightComponent().setVisible(v);
                sp.setDividerLocation(v ? 0.60 : 1.0);
                sp.revalidate();
            }
    }

    // ── Demo data (replace with DAO.getVisitors()) ────────────────────────────
    private void loadDemoData() {
        Object[][] demo = {
            {1, "VT-001", "Khalid Hassan",   "35202-1111111-1", "0300-1111111", "Ali Hassan",     "101-A", "01/10/2024 10:30", "01/10/2024 12:00", "Checked-Out"},
            {2, "VT-002", "Amina Ahmed",     "35202-2222222-2", "0311-2222222", "Sara Ahmed",     "102-A", "02/10/2024 14:00", "",                  "Checked-In"},
            {3, "VT-003", "Rizwan Malik",    "35202-3333333-3", "0333-3333333", "Fatima Malik",   "103-A", "03/10/2024 11:00", "03/10/2024 13:30", "Checked-Out"},
            {4, "VT-004", "Imtiaz Khan",     "35202-4444444-4", "0345-4444444", "Ibrahim Khan",   "104-B", "04/10/2024 09:00", "04/10/2024 09:15", "Denied"},
            {5, "VT-005", "Noor Mehmood",    "35202-5555555-5", "0322-5555555", "Hassan Mehmood", "202-B", "05/10/2024 16:00", "05/10/2024 18:00", "Checked-Out"},
            {6, "VT-006", "Sajida Raza",     "35202-6666666-6", "0312-6666666", "Ayesha Raza",    "105-A", "06/10/2024 10:00", "",                  "Checked-In"},
        };
        for (Object[] r : demo) tableModel.addRow(r);
    }
}
