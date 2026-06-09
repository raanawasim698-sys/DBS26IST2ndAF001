package ui;

import ui.util.Theme;
import ui.util.UIFactory;
import software.Logger;
import software.DBConnection;
import software.SessionManager;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class NoticeForm extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTitle, txtExpiry;
    private JTextArea txtDescription;
    private JComboBox<String> cmbAudience;
    private JButton btnSave, btnClear;
    private JLabel lblFormTitle;
    private int selectedId = -1;

    private static final String[] COLUMNS = {"ID","Title","Target","Posted","Expiry","Active","Action"};

    public NoticeForm() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.PRIMARY);
        header.setBorder(new EmptyBorder(14,20,14,20));
        JLabel title = new JLabel("📢  Notice Board");
        title.setFont(Theme.FONT_TITLE); title.setForeground(Theme.TEXT_WHITE);
        JLabel sub = new JLabel("Post and manage hostel notices");
        sub.setFont(Theme.FONT_SUBTITLE); sub.setForeground(new Color(200,220,255));
        JPanel ht = new JPanel(new GridLayout(2,1)); ht.setOpaque(false);
        ht.add(title); ht.add(sub);
        header.add(ht, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildForm(), buildTable());
        split.setDividerLocation(370); split.setDividerSize(2); split.setBorder(null);
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildForm() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Theme.BG_MAIN);
        outer.setBorder(new EmptyBorder(16,16,16,8));

        JPanel card = UIFactory.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20,20,20,20));

        lblFormTitle = UIFactory.boldLabel("Post New Notice");
        lblFormTitle.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblFormTitle); card.add(Box.createVerticalStrut(16));

        card.add(UIFactory.label("Notice Title *")); card.add(Box.createVerticalStrut(4));
        txtTitle = UIFactory.textField(20); card.add(txtTitle); card.add(Box.createVerticalStrut(12));

        card.add(UIFactory.label("Description *")); card.add(Box.createVerticalStrut(4));
        txtDescription = new JTextArea(5,20);
        txtDescription.setFont(Theme.FONT_INPUT);
        txtDescription.setLineWrap(true); txtDescription.setWrapStyleWord(true);
        txtDescription.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER),
            BorderFactory.createEmptyBorder(6,8,6,8)));
        JScrollPane ds = UIFactory.textArea(txtDescription);
        ds.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        ds.setAlignmentX(LEFT_ALIGNMENT);
        card.add(ds); card.add(Box.createVerticalStrut(12));

        card.add(UIFactory.label("Target Audience")); card.add(Box.createVerticalStrut(4));
        cmbAudience = UIFactory.comboBox(new String[]{"All","Block A","Block B","Block C","Block D","Block E"});
        card.add(cmbAudience); card.add(Box.createVerticalStrut(12));

        card.add(UIFactory.label("Expiry Date (YYYY-MM-DD, optional)")); card.add(Box.createVerticalStrut(4));
        txtExpiry = UIFactory.textField(20); txtExpiry.setText("");
        card.add(txtExpiry); card.add(Box.createVerticalStrut(20));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        btnRow.setOpaque(false); btnRow.setAlignmentX(LEFT_ALIGNMENT);
        btnSave = UIFactory.primaryButton("Post Notice");
        btnSave.addActionListener(e -> saveNotice());
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

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,12));
        toolbar.setOpaque(false);
        toolbar.add(UIFactory.boldLabel("All Notices"));
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

        table.getColumn("Action").setCellRenderer((t,val,sel,foc,row,col) -> {
            JButton b = UIFactory.primaryButton("Edit"); b.setFont(Theme.FONT_BADGE); return b;
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

    private void loadData() {
        tableModel.setRowCount(0);
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT notice_id, title, target_audience, post_date, expiry_date, is_active " +
                "FROM notices ORDER BY post_date DESC");
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4),
                    rs.getDate(5) != null ? rs.getDate(5).toString() : "—",
                    rs.getBoolean(6) ? "Yes" : "No", "Edit"
                });
            }
        } catch (SQLException e) { Logger.log("NoticeForm","Load error.",e); }
    }

    private void saveNotice() {
        String title = txtTitle.getText().trim();
        String desc  = txtDescription.getText().trim();
        if (title.isEmpty() || desc.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Title and Description are required."); return;
        }
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            int postedBy = SessionManager.getInstance().getStaffId();
            if (postedBy == 0) postedBy = 1;
            String audience = (String) cmbAudience.getSelectedItem();
            String expiry = txtExpiry.getText().trim();

            if (selectedId == -1) {
                String sql = "INSERT INTO notices (title,description,posted_by,post_date,target_audience,is_active" +
                             (!expiry.isEmpty() ? ",expiry_date" : "") +
                             ") VALUES (?,?,?,CURRENT_DATE,?,TRUE" + (!expiry.isEmpty() ? ",?" : "") + ")";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1,title); ps.setString(2,desc); ps.setInt(3,postedBy); ps.setString(4,audience);
                if (!expiry.isEmpty()) ps.setString(5,expiry);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this,"Notice posted!");
            } else {
                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE notices SET title=?,description=?,target_audience=? WHERE notice_id=?");
                ps.setString(1,title); ps.setString(2,desc); ps.setString(3,audience); ps.setInt(4,selectedId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this,"Notice updated!");
            }
            clearForm(); loadData();
        } catch (Exception e) {
            Logger.log("NoticeForm","Save error.",e);
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage());
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedId = (int) tableModel.getValueAt(row, 0);
        txtTitle.setText((String) tableModel.getValueAt(row, 1));
        cmbAudience.setSelectedItem(tableModel.getValueAt(row, 2));
        lblFormTitle.setText("Edit Notice #" + selectedId);
        btnSave.setText("Update Notice");
    }

    private void clearForm() {
        selectedId = -1;
        txtTitle.setText(""); txtDescription.setText(""); txtExpiry.setText("");
        cmbAudience.setSelectedIndex(0);
        lblFormTitle.setText("Post New Notice");
        btnSave.setText("Post Notice");
    }
}
