package ui;

import ui.util.Theme;
import ui.util.UIFactory;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * RoomForm — manage hostel rooms (add / edit / delete).
 *
 * Features:
 *  - Room type filter tabs (All | Single | Double | Triple | Dorm)
 *  - Capacity / occupancy spinner
 *  - Amenities checkboxes (AC, WiFi, Attached Bath, TV)
 *  - Real-time search by room number / floor
 */
public class RoomForm extends JPanel {

    // ── Table ─────────────────────────────────────────────────────────────────
    private static final String[] COLS =
        {"#", "Room No.", "Floor", "Type", "Capacity", "Occupied", "Status", "Monthly Fee"};
    private final DefaultTableModel tableModel = new DefaultTableModel(COLS, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    // ── Form fields ───────────────────────────────────────────────────────────
    private final JTextField        txRoomNo   = UIFactory.textField(10);
    private final JComboBox<String> cmbFloor   = UIFactory.comboBox(new String[]{"Ground", "1st", "2nd", "3rd", "4th"});
    private final JComboBox<String> cmbType    = UIFactory.comboBox(new String[]{"Single", "Double", "Triple", "Dormitory"});
    private final JSpinner          spnCapacity= UIFactory.spinner(1, 20, 2);
    private final JTextField        txFee      = UIFactory.textField(10);
    private final JComboBox<String> cmbStatus  = UIFactory.comboBox(new String[]{"Available", "Occupied", "Under Maintenance", "Reserved"});
    private final JTextArea         txNotes    = UIFactory.textArea(3, 20);

    // Amenities
    private final JCheckBox chkAC    = new JCheckBox("Air Conditioning");
    private final JCheckBox chkWifi  = new JCheckBox("WiFi");
    private final JCheckBox chkBath  = new JCheckBox("Attached Bath");
    private final JCheckBox chkTV    = new JCheckBox("Television");
    private final JCheckBox chkFan   = new JCheckBox("Ceiling Fan");
    private final JCheckBox chkWater = new JCheckBox("Water Cooler");

    // ── Toolbar ───────────────────────────────────────────────────────────────
    private final JButton    btnAdd    = UIFactory.primaryButton("+ Add Room");
    private final JButton    btnSave   = UIFactory.primaryButton("💾 Save");
    private final JButton    btnCancel = UIFactory.secondaryButton("Cancel");
    private final JButton    btnDelete = UIFactory.dangerButton("🗑 Delete");
    private final JTextField txSearch  = UIFactory.textField(16);

    private boolean editMode  = false;
    private int     selRow    = -1;

    // ── Constructor ───────────────────────────────────────────────────────────
    public RoomForm() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(buildTopBar(),   BorderLayout.NORTH);
        add(buildContent(),  BorderLayout.CENTER);

        styleCheckboxes();
        wireEvents();
        loadDemoData();
        setFormVisible(false);
    }

    // ── Top bar ───────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel title = UIFactory.sectionHeader("🛏  Rooms");

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        txSearch.setPreferredSize(new Dimension(160, Theme.INPUT_H));
        right.add(new JLabel("🔍"));
        right.add(txSearch);
        right.add(Box.createHorizontalStrut(8));
        right.add(btnAdd);
        right.add(btnDelete);

        bar.add(title, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Content (table + form) ────────────────────────────────────────────────
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

        addCardHeader(card, "Room Details");

        // Room info grid
        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 10));
        grid.setOpaque(false);
        grid.setAlignmentX(LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        grid.add(labeledField("Room Number", txRoomNo));
        grid.add(labeledField("Floor", cmbFloor));
        grid.add(labeledField("Room Type", cmbType));
        grid.add(labeledField("Capacity", spnCapacity));
        grid.add(labeledField("Monthly Fee (PKR)", txFee));
        grid.add(labeledField("Status", cmbStatus));

        card.add(grid);
        card.add(Box.createVerticalStrut(14));

        // Amenities section
        JLabel amenitiesLbl = UIFactory.sectionLabel("Amenities");
        amenitiesLbl.setAlignmentX(LEFT_ALIGNMENT);
        card.add(amenitiesLbl);

        JPanel checks = new JPanel(new GridLayout(2, 3, 8, 4));
        checks.setOpaque(false);
        checks.setAlignmentX(LEFT_ALIGNMENT);
        checks.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        checks.add(chkAC); checks.add(chkWifi); checks.add(chkBath);
        checks.add(chkFan); checks.add(chkTV); checks.add(chkWater);
        card.add(checks);
        card.add(Box.createVerticalStrut(10));

        // Notes
        JLabel notesLbl = UIFactory.sectionLabel("Notes");
        notesLbl.setAlignmentX(LEFT_ALIGNMENT);
        card.add(notesLbl);

        JScrollPane notesSp = new JScrollPane(txNotes);
        notesSp.setAlignmentX(LEFT_ALIGNMENT);
        notesSp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
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

    private void styleCheckboxes() {
        for (JCheckBox cb : new JCheckBox[]{chkAC, chkWifi, chkBath, chkTV, chkFan, chkWater}) {
            cb.setFont(Theme.FONT_LABEL);
            cb.setOpaque(false);
            cb.setForeground(Theme.TEXT_PRIMARY);
        }
    }

    // ── Status-colour renderer for Status column ──────────────────────────────
    private void colorStatusColumn() {
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                String s = val == null ? "" : val.toString();
                if (!sel) {
                    setBackground(row % 2 == 0 ? Theme.BG_CARD : new Color(0xF8, 0xFA, 0xFC));
                }
                setForeground(switch (s) {
                    case "Available"          -> Theme.SUCCESS;
                    case "Occupied"           -> Theme.PRIMARY;
                    case "Under Maintenance"  -> Theme.WARNING;
                    default                   -> Theme.TEXT_SECONDARY;
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
            setFormVisible(true); txRoomNo.requestFocus();
        });

        table.getSelectionModel().addListSelectionListener(ev -> {
            if (ev.getValueIsAdjusting()) return;
            selRow = table.getSelectedRow();
            if (selRow >= 0) {
                populateForm(selRow); editMode = true; setFormVisible(true);
            }
        });

        btnSave.addActionListener(e -> {
            if (!validateForm()) return;
            if (editMode && selRow >= 0) updateRow(selRow); else addRow();
            setFormVisible(false); clearForm();
        });

        btnCancel.addActionListener(e -> { setFormVisible(false); clearForm(); });

        btnDelete.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) { JOptionPane.showMessageDialog(this, "Select a room first."); return; }
            if (JOptionPane.showConfirmDialog(this, "Delete Room " +
                    tableModel.getValueAt(r, 1) + "?", "Confirm",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                tableModel.removeRow(r); setFormVisible(false);
            }
        });
    }

    private boolean validateForm() {
        if (txRoomNo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Room number is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txFee.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Monthly fee is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        txRoomNo.setText(""); txFee.setText(""); txNotes.setText("");
        cmbFloor.setSelectedIndex(0); cmbType.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0); spnSemester(1);
        for (JCheckBox cb : new JCheckBox[]{chkAC, chkWifi, chkBath, chkTV, chkFan, chkWater})
            cb.setSelected(false);
    }

    private void spnSemester(int v) { spnCapacity.setValue(v); }

    private void populateForm(int row) {
        txRoomNo.setText(tableModel.getValueAt(row, 1).toString());
        cmbType.setSelectedItem(tableModel.getValueAt(row, 3).toString());
        spnCapacity.setValue(Integer.parseInt(tableModel.getValueAt(row, 4).toString()));
        cmbStatus.setSelectedItem(tableModel.getValueAt(row, 6).toString());
        txFee.setText(tableModel.getValueAt(row, 7).toString());
    }

    private void addRow() {
        tableModel.addRow(new Object[]{
            tableModel.getRowCount() + 1,
            txRoomNo.getText().trim(),
            cmbFloor.getSelectedItem(),
            cmbType.getSelectedItem(),
            spnCapacity.getValue(),
            0,
            cmbStatus.getSelectedItem(),
            txFee.getText().trim()
        });
    }

    private void updateRow(int row) {
        tableModel.setValueAt(txRoomNo.getText().trim(),     row, 1);
        tableModel.setValueAt(cmbFloor.getSelectedItem(),    row, 2);
        tableModel.setValueAt(cmbType.getSelectedItem(),     row, 3);
        tableModel.setValueAt(spnCapacity.getValue(),        row, 4);
        tableModel.setValueAt(cmbStatus.getSelectedItem(),   row, 6);
        tableModel.setValueAt(txFee.getText().trim(),        row, 7);
    }

    private void setFormVisible(boolean v) {
        for (Component c : getComponents())
            if (c instanceof JSplitPane sp) {
                sp.getRightComponent().setVisible(v);
                sp.setDividerLocation(v ? 0.62 : 1.0);
                sp.revalidate();
            }
    }

    // ── Demo data ─────────────────────────────────────────────────────────────
    private void loadDemoData() {
        Object[][] demo = {
            {1, "101-A", "Ground", "Single",    1, 1, "Occupied",   "8,000"},
            {2, "102-A", "Ground", "Double",    2, 2, "Occupied",   "6,500"},
            {3, "103-A", "Ground", "Double",    2, 1, "Available",  "6,500"},
            {4, "201-B", "1st",    "Triple",    3, 3, "Occupied",   "5,000"},
            {5, "202-B", "1st",    "Dormitory", 8, 6, "Available",  "3,500"},
            {6, "301-C", "2nd",    "Single",    1, 0, "Available",  "8,000"},
            {7, "401-D", "3rd",    "Double",    2, 0, "Under Maintenance", "6,500"},
        };
        for (Object[] r : demo) tableModel.addRow(r);
    }
}
