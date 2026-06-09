package ui;

import ui.util.Theme;
import ui.util.UIFactory;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.layout.borders.SolidBorder;

import dao.*;
import domain.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportsPanel extends JPanel {

    // ── Report definitions ────────────────────────────────────────────────────
    private static final String[][] REPORTS = {
            {"R01", "👥", "Student Register",      "Full list of all enrolled students with room and status details.",  "Students"},
            {"R02", "🛏", "Room Occupancy Report",  "Room-wise occupancy summary with capacity and vacancy count.",      "Rooms"},
            {"R03", "📋", "Booking History",        "All booking records filtered by date range and status.",            "Bookings"},
            {"R04", "💳", "Fee Collection Report",  "Fee receipts collected in a selected month with totals.",          "Fees"},
            {"R05", "⚠️", "Outstanding Dues",       "Students with pending or partial fee payments.",                   "Fees"},
            {"R06", "📝", "Complaint Summary",      "Complaints logged in a period, grouped by status and type.",       "Complaints"},
            {"R07", "👷", "Staff Directory",        "Complete staff list with designation, contact and status.",        "Staff"},
            {"R08", "🚶", "Visitor Log Report",     "Daily visitor entries with student and visitor details.",          "Visitors"},
            {"R09", "📊", "Monthly Income Summary", "Month-wise revenue from fees, showing collected vs outstanding.",  "Finance"},
            {"R10", "🔍", "Vacancy & Availability", "Rooms currently available for allocation with amenity details.",   "Rooms"},
    };

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── Colours for PDF ───────────────────────────────────────────────────────
    private static final DeviceRgb PDF_HEADER_BG  = new DeviceRgb(0x1A, 0x56, 0xDB);
    private static final DeviceRgb PDF_ROW_ALT    = new DeviceRgb(0xF3, 0xF4, 0xF6);
    private static final DeviceRgb PDF_TEXT_DARK  = new DeviceRgb(0x11, 0x18, 0x27);
    private static final DeviceRgb PDF_BORDER_COL = new DeviceRgb(0xE5, 0xE7, 0xEB);

    // ── Filter widgets ────────────────────────────────────────────────────────
    private final JTextField        txFromDate  = UIFactory.textField(12);
    private final JTextField        txToDate    = UIFactory.textField(12);
    private final JComboBox<String> cmbStatus   = new JComboBox<>(new String[]{
            "All","Active","Inactive","Pending","Paid","Unpaid","Resolved","Open"});
    private final JComboBox<String> cmbMonth    = new JComboBox<>(new String[]{
            "All Months","January","February","March","April","May","June",
            "July","August","September","October","November","December"});
    private final JComboBox<String> cmbYear     = new JComboBox<>(new String[]{"2026","2025","2024","2023"});
    private final JTextField        txStudentId = UIFactory.textField(12);

    private String selectedReportId    = null;
    private String selectedReportTitle = "";

    private static final String[] LOG_COLS = {"#","Report","Generated At","Parameters","Status"};
    private final DefaultTableModel logModel = new DefaultTableModel(LOG_COLS, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable    logTable          = new JTable(logModel);
    private final JLabel    lblSelectedReport = new JLabel("Select a report from the left panel");

    // ── Constructor ───────────────────────────────────────────────────────────
    public ReportsPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);

        styleCombo(cmbStatus); styleCombo(cmbMonth); styleCombo(cmbYear);
        loadSampleLog();
    }

    // ── Top bar ───────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel title = makeTitleLabel("📄  Reports");
        JLabel sub   = new JLabel("Generate and export business reports as PDF");
        sub.setFont(Theme.FONT_SUBTITLE);
        sub.setForeground(Theme.TEXT_SECONDARY);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.add(title);
        left.add(Box.createVerticalStrut(2));
        left.add(sub);

        bar.add(left, BorderLayout.WEST);
        return bar;
    }

    // ── Content ───────────────────────────────────────────────────────────────
    private JSplitPane buildContent() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildReportListPanel(), buildRightPanel());
        split.setDividerLocation(0.44);
        split.setResizeWeight(0.44);
        split.setBorder(null);
        split.setOpaque(false);
        return split;
    }

    private JScrollPane buildReportListPanel() {
        JPanel grid = new JPanel(new GridLayout(0, 1, 0, 10));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(0, 0, 0, 12));
        for (String[] r : REPORTS) grid.add(buildReportCard(r));
        JScrollPane sp = new JScrollPane(grid);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setOpaque(false);
        sp.setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private JPanel buildReportCard(String[] r) {
        JPanel card = new JPanel(new BorderLayout(10, 4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 10, 10));
                g2.setColor(Theme.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 10, 10));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel icon = new JLabel(r[1]);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        icon.setPreferredSize(new Dimension(40, 40));
        icon.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel name = new JLabel(r[0] + " — " + r[2]);
        name.setFont(Theme.FONT_LABEL_BOLD);
        name.setForeground(Theme.TEXT_PRIMARY);

        JLabel desc = new JLabel("<html><body style='width:200px'>" + r[3] + "</body></html>");
        desc.setFont(Theme.FONT_SUBTITLE);
        desc.setForeground(Theme.TEXT_SECONDARY);

        JLabel cat = makeBadge(r[4]);
        info.add(name);
        info.add(Box.createVerticalStrut(3));
        info.add(desc);
        info.add(Box.createVerticalStrut(5));
        info.add(cat);

        JButton btnSelect = UIFactory.primaryButton("Select");
        btnSelect.setPreferredSize(new Dimension(80, 30));
        btnSelect.addActionListener(e -> selectReport(r));

        card.add(icon,      BorderLayout.WEST);
        card.add(info,      BorderLayout.CENTER);
        card.add(btnSelect, BorderLayout.EAST);

        MouseAdapter hover = new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { card.setBorder(new CompoundBorder(
                    new LineBorder(Theme.PRIMARY, 1, true), new EmptyBorder(11,13,11,13))); }
            public void mouseExited(MouseEvent e)  { card.setBorder(new EmptyBorder(12,14,12,14)); }
        };
        card.addMouseListener(hover);
        return card;
    }

    // ── Right panel ───────────────────────────────────────────────────────────
    private JPanel buildRightPanel() {
        JPanel outer = new JPanel(new BorderLayout(0, 12));
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(0, 4, 0, 0));
        outer.add(buildParamCard(), BorderLayout.NORTH);
        outer.add(buildLogCard(),   BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildParamCard() {
        JPanel card = UIFactory.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        addCardHeader(card, "⚙  Report Parameters");

        lblSelectedReport.setFont(Theme.FONT_LABEL_BOLD);
        lblSelectedReport.setForeground(Theme.PRIMARY);
        lblSelectedReport.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblSelectedReport);
        card.add(Box.createVerticalStrut(14));

        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 10));
        grid.setOpaque(false);
        grid.setAlignmentX(LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
        grid.add(labeledField("From Date (DD/MM/YYYY)", txFromDate));
        grid.add(labeledField("To Date (DD/MM/YYYY)",   txToDate));
        grid.add(labeledField("Month",                  cmbMonth));
        grid.add(labeledField("Year",                   cmbYear));
        grid.add(labeledField("Status Filter",          cmbStatus));
        grid.add(labeledField("Student ID (optional)",  txStudentId));
        card.add(grid);
        card.add(Box.createVerticalStrut(14));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);

        JButton btnGenerate = UIFactory.primaryButton("📄 Generate PDF");
        JButton btnPreview  = UIFactory.secondaryButton("👁 Preview");
        JButton btnClear    = UIFactory.secondaryButton("Clear");

        btnGenerate.addActionListener(e -> generateReport());
        btnPreview.addActionListener(e  -> previewReport());
        btnClear.addActionListener(e    -> clearParams());

        btnRow.add(btnGenerate);
        btnRow.add(btnPreview);
        btnRow.add(btnClear);
        card.add(btnRow);
        return card;
    }

    private JPanel buildLogCard() {
        JPanel card = UIFactory.card();
        card.setLayout(new BorderLayout());

        JLabel hdr = new JLabel("Generation History");
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 14));
        hdr.setForeground(Theme.TEXT_PRIMARY);
        hdr.setBorder(new EmptyBorder(0, 0, 8, 0));

        UIFactory.styleTable(logTable);
        logTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        colorLogStatusColumn();

        JScrollPane sp = new JScrollPane(logTable);
        sp.setBorder(new LineBorder(Theme.BORDER, 1, true));
        sp.getViewport().setBackground(Theme.BG_CARD);

        card.add(hdr, BorderLayout.NORTH);
        card.add(sp,  BorderLayout.CENTER);
        return card;
    }

    // ── Select / action ───────────────────────────────────────────────────────
    private void selectReport(String[] r) {
        selectedReportId    = r[0];
        selectedReportTitle = r[2];
        lblSelectedReport.setText(r[1] + "  " + r[0] + " — " + r[2]);
    }

    private void generateReport() {
        if (selectedReportId == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a report first.", "No Report Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Determine save path — reports/ folder next to the project
        String ts         = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        File   reportsDir = new File(System.getProperty("user.dir") + File.separator + "reports");
        if (!reportsDir.exists()) reportsDir.mkdirs();
        String path = reportsDir.getAbsolutePath() + File.separator + selectedReportId + "_" + ts + ".pdf";

        String statusMsg = "Failed";
        String params    = buildParamString();

        try {
            buildPdf(path);
            statusMsg = "Success";

            // Open the file automatically
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(new File(path));
            }

            JOptionPane.showMessageDialog(this,
                    "✅  PDF saved to reports folder:\n" + new File(path).getName(),
                    "Report Generated", JOptionPane.INFORMATION_MESSAGE);

        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(this,
                    "❌  Failed to generate PDF:\n" + ex.getClass().getSimpleName() + ": " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        // Log the attempt
        String time = LocalDateTime.now().format(TIME_FMT);
        logModel.insertRow(0, new Object[]{
                logModel.getRowCount() + 1, selectedReportTitle, time, params, statusMsg
        });
    }

    private void buildPdf(String path) throws Exception {
        PdfWriter   writer = new PdfWriter(path);
        PdfDocument pdf    = new PdfDocument(writer);
        Document    doc    = new Document(pdf, PageSize.A4);
        doc.setMargins(36, 36, 36, 36);

        PdfFont bold    = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        // ── Cover header ──────────────────────────────────────────────────────
        doc.add(new Paragraph("UET Lahore — Hostel Management System")
                .setFont(bold).setFontSize(16).setFontColor(PDF_HEADER_BG)
                .setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph(selectedReportTitle)
                .setFont(bold).setFontSize(13).setFontColor(PDF_TEXT_DARK)
                .setTextAlignment(TextAlignment.CENTER).setMarginTop(4));
        doc.add(new Paragraph("Generated: " + LocalDateTime.now().format(TIME_FMT)
                + "   |   Parameters: " + buildParamString())
                .setFont(regular).setFontSize(9).setFontColor(new DeviceRgb(0x6B, 0x72, 0x80))
                .setTextAlignment(TextAlignment.CENTER).setMarginBottom(16));

        // ── Report body ───────────────────────────────────────────────────────
        switch (selectedReportId) {
            case "R01" -> buildStudentRegister(doc, bold, regular);
            case "R02" -> buildRoomOccupancy(doc, bold, regular);
            case "R03" -> buildBookingHistory(doc, bold, regular);
            case "R04" -> buildFeeCollection(doc, bold, regular);
            case "R05" -> buildOutstandingDues(doc, bold, regular);
            case "R06" -> buildComplaintSummary(doc, bold, regular);
            case "R07" -> buildStaffDirectory(doc, bold, regular);
            case "R08" -> buildVisitorLog(doc, bold, regular);
            case "R09" -> buildMonthlyIncome(doc, bold, regular);
            case "R10" -> buildVacancyReport(doc, bold, regular);
            default    -> buildGenericPlaceholder(doc, bold, regular);
        }

        doc.close();
    }

    // ── R01: Student Register ─────────────────────────────────────────────────
    private void buildStudentRegister(Document doc, PdfFont bold, PdfFont regular) throws Exception {
        String statusFilter = (String) cmbStatus.getSelectedItem();
        List<Student> students;
        try {
            StudentDAO dao = new StudentDAO();
            students = "Active".equals(statusFilter) ? dao.getActiveStudents() : dao.getAllStudents();
            if (!"All".equals(statusFilter) && !"Active".equals(statusFilter)) {
                students = students.stream()
                        .filter(s -> statusFilter.equalsIgnoreCase(s.getStatus()))
                        .toList();
            }
        } catch (Exception e) {
            noDbFallback(doc, bold, regular); return;
        }

        float[] cols = {30, 80, 130, 100, 80, 60, 70};
        Table table = styledTable(cols);
        addHeaderRow(table, bold, "#", "Roll No.", "Full Name", "Program", "Contact", "Sem", "Status");

        int i = 1;
        for (Student s : students) {
            boolean alt = i % 2 == 0;
            addRow(table, regular, alt,
                    String.valueOf(i++),
                    s.getRollNumber(),
                    s.getFullName(),
                    s.getProgram(),
                    s.getContactNumber(),
                    String.valueOf(s.getSemester()),
                    s.getStatus());
        }
        doc.add(table);
        addFooter(doc, regular, students.size() + " student(s) listed.");
    }

    // ── R02: Room Occupancy ───────────────────────────────────────────────────
    private void buildRoomOccupancy(Document doc, PdfFont bold, PdfFont regular) throws Exception {
        List<Room> rooms;
        try {
            rooms = new RoomDAO().getAllRooms();
        } catch (Exception e) {
            noDbFallback(doc, bold, regular); return;
        }

        float[] cols = {30, 60, 80, 70, 60, 60, 80, 80};
        Table table = styledTable(cols);
        addHeaderRow(table, bold, "#", "Room No.", "Block", "Type", "Capacity", "Floor", "Rent/Mo", "Status");

        int i = 1; int available = 0; int occupied = 0;
        for (Room r : rooms) {
            boolean alt = i % 2 == 0;
            addRow(table, regular, alt,
                    String.valueOf(i++),
                    r.getRoomNumber(),
                    r.getBlockName() != null ? r.getBlockName() : "-",
                    r.getRoomType(),
                    String.valueOf(r.getCapacity()),
                    String.valueOf(r.getFloor()),
                    String.format("PKR %.0f", r.getMonthlyRent()),
                    r.getStatus());
            if ("Available".equalsIgnoreCase(r.getStatus())) available++;
            else occupied++;
        }
        doc.add(table);
        addFooter(doc, regular, "Total: " + rooms.size() + "  |  Available: " + available + "  |  Occupied: " + occupied);
    }

    // ── R04: Fee Collection ───────────────────────────────────────────────────
    private void buildFeeCollection(Document doc, PdfFont bold, PdfFont regular) throws Exception {
        String month = (String) cmbMonth.getSelectedItem();
        String year  = (String) cmbYear.getSelectedItem();
        List<FeeBill> bills;
        try {
            FeeBillDAO dao = new FeeBillDAO();
            if ("All Months".equals(month)) {
                bills = dao.getBillsByMonth(year + "-%");
            } else {
                int mo = monthIndex(month);
                String key = year + "-" + String.format("%02d", mo);
                bills = dao.getBillsByMonth(key);
            }
        } catch (Exception e) {
            noDbFallback(doc, bold, regular); return;
        }

        float[] cols = {30, 80, 130, 100, 80, 80, 80};
        Table table = styledTable(cols);
        addHeaderRow(table, bold, "#", "Roll No.", "Student Name", "Month", "Due (PKR)", "Paid (PKR)", "Status");

        int i = 1; double totalDue = 0; double totalPaid = 0;
        for (FeeBill b : bills) {
            boolean alt = i % 2 == 0;
            addRow(table, regular, alt,
                    String.valueOf(i++),
                    b.getRollNumber(),
                    b.getStudentName(),
                    b.getBillMonth(),
                    String.format("%.0f", b.getAmountDue()),
                    String.format("%.0f", b.getAmountPaid()),
                    b.getStatus());
            totalDue  += b.getAmountDue();
            totalPaid += b.getAmountPaid();
        }
        doc.add(table);
        addFooter(doc, regular, String.format("Total Due: PKR %.0f  |  Total Collected: PKR %.0f  |  Outstanding: PKR %.0f",
                totalDue, totalPaid, totalDue - totalPaid));
    }

    // ── R05: Outstanding Dues ─────────────────────────────────────────────────
    private void buildOutstandingDues(Document doc, PdfFont bold, PdfFont regular) throws Exception {
        List<FeeBill> bills;
        try {
            bills = new FeeBillDAO().getDefaulters();
        } catch (Exception e) {
            noDbFallback(doc, bold, regular); return;
        }

        float[] cols = {30, 80, 130, 100, 90, 90, 70};
        Table table = styledTable(cols);
        addHeaderRow(table, bold, "#", "Roll No.", "Student Name", "Month", "Due (PKR)", "Balance (PKR)", "Status");

        int i = 1; double totalBalance = 0;
        for (FeeBill b : bills) {
            boolean alt = i % 2 == 0;
            addRow(table, regular, alt,
                    String.valueOf(i++),
                    b.getRollNumber(),
                    b.getStudentName(),
                    b.getBillMonth(),
                    String.format("%.0f", b.getAmountDue()),
                    String.format("%.0f", b.getBalanceDue()),
                    b.getStatus());
            totalBalance += b.getBalanceDue();
        }
        doc.add(table);
        addFooter(doc, regular, "Total Outstanding: PKR " + String.format("%.0f", totalBalance)
                + "  across " + bills.size() + " bill(s).");
    }

    // ── R06: Complaint Summary ────────────────────────────────────────────────
    private void buildComplaintSummary(Document doc, PdfFont bold, PdfFont regular) throws Exception {
        String statusFilter = (String) cmbStatus.getSelectedItem();
        List<Complaint> complaints;
        try {
            ComplaintDAO dao = new ComplaintDAO();
            complaints = "All".equals(statusFilter) ? dao.getAllComplaints()
                    : dao.getComplaintsByStatus(statusFilter);
        } catch (Exception e) {
            noDbFallback(doc, bold, regular); return;
        }

        float[] cols = {30, 70, 110, 80, 70, 80, 70};
        Table table = styledTable(cols);
        addHeaderRow(table, bold, "#", "Roll No.", "Student", "Category", "Priority", "Status", "Filed");

        int i = 1;
        for (Complaint c : complaints) {
            boolean alt = i % 2 == 0;
            addRow(table, regular, alt,
                    String.valueOf(i++),
                    c.getRollNumber(),
                    c.getStudentName(),
                    c.getCategory(),
                    c.getPriority(),
                    c.getStatus(),
                    c.getFiledDate() != null ? c.getFiledDate().format(DATE_FMT) : "-");
        }
        doc.add(table);
        addFooter(doc, regular, complaints.size() + " complaint(s) listed.");
    }

    // ── R07: Staff Directory ──────────────────────────────────────────────────
    private void buildStaffDirectory(Document doc, PdfFont bold, PdfFont regular) throws Exception {
        List<Staff> staff;
        try {
            staff = new StaffDAO().getAllStaff();
        } catch (Exception e) {
            noDbFallback(doc, bold, regular); return;
        }

        float[] cols = {30, 120, 100, 90, 80, 80, 50};
        Table table = styledTable(cols);
        addHeaderRow(table, bold, "#", "Full Name", "Designation", "Contact", "Block", "Joined", "Active");

        int i = 1;
        for (Staff s : staff) {
            boolean alt = i % 2 == 0;
            addRow(table, regular, alt,
                    String.valueOf(i++),
                    s.getFullName(),
                    s.getDesignation(),
                    s.getContactNumber(),
                    s.getBlockName() != null ? s.getBlockName() : "-",
                    s.getJoiningDate() != null ? s.getJoiningDate().format(DATE_FMT) : "-",
                    s.isActive() ? "Yes" : "No");
        }
        doc.add(table);
        addFooter(doc, regular, staff.size() + " staff member(s) listed.");
    }

    // ── R08: Visitor Log ──────────────────────────────────────────────────────
    private void buildVisitorLog(Document doc, PdfFont bold, PdfFont regular) throws Exception {
        List<Visitor> visitors;
        try {
            VisitorDAO dao = new VisitorDAO();
            String from = txFromDate.getText().trim();
            String to   = txToDate.getText().trim();
            if (!from.isEmpty() && !to.isEmpty()) {
                LocalDate fd = LocalDate.parse(from, DATE_FMT);
                LocalDate td = LocalDate.parse(to, DATE_FMT);
                visitors = dao.getVisitorsByDateRange(fd, td);
            } else {
                visitors = dao.getTodayVisitors();
            }
        } catch (Exception e) {
            noDbFallback(doc, bold, regular); return;
        }

        float[] cols = {30, 110, 90, 80, 110, 90, 60};
        Table table = styledTable(cols);
        addHeaderRow(table, bold, "#", "Visitor Name", "Relation", "Student", "Entry Time", "Exit Time", "Purpose");

        int i = 1;
        for (Visitor v : visitors) {
            boolean alt = i % 2 == 0;
            addRow(table, regular, alt,
                    String.valueOf(i++),
                    v.getVisitorName(),
                    v.getRelation(),
                    v.getStudentName(),
                    v.getEntryTime() != null ? v.getEntryTime().format(TIME_FMT) : "-",
                    v.getExitTime()  != null ? v.getExitTime().format(TIME_FMT)  : "Still In",
                    v.getPurpose());
        }
        doc.add(table);
        addFooter(doc, regular, visitors.size() + " visitor record(s) listed.");
    }

    // ── R10: Vacancy & Availability ───────────────────────────────────────────
    private void buildVacancyReport(Document doc, PdfFont bold, PdfFont regular) throws Exception {
        List<Room> rooms;
        try {
            rooms = new RoomDAO().getAvailableRooms();
        } catch (Exception e) {
            noDbFallback(doc, bold, regular); return;
        }

        float[] cols = {30, 60, 80, 70, 60, 80, 50, 50};
        Table table = styledTable(cols);
        addHeaderRow(table, bold, "#", "Room No.", "Block", "Type", "Capacity", "Rent/Mo", "AC", "Bath");

        int i = 1;
        for (Room r : rooms) {
            boolean alt = i % 2 == 0;
            addRow(table, regular, alt,
                    String.valueOf(i++),
                    r.getRoomNumber(),
                    r.getBlockName() != null ? r.getBlockName() : "-",
                    r.getRoomType(),
                    String.valueOf(r.getCapacity()),
                    String.format("PKR %.0f", r.getMonthlyRent()),
                    r.getAcStatus() != null ? r.getAcStatus() : "-",
                    r.getBathroomAttached() != null ? r.getBathroomAttached() : "-");
        }
        doc.add(table);
        addFooter(doc, regular, rooms.size() + " available room(s).");
    }

    // ── R03: Booking History ──────────────────────────────────────────────────
    private void buildBookingHistory(Document doc, PdfFont bold, PdfFont regular) throws Exception {
        List<Object[]> rows;
        try {
            rows = new RoomAllocationDAO().getAllCurrentAllocations();
        } catch (Exception e) {
            noDbFallback(doc, bold, regular); return;
        }

        // Date-range filter (allocation_date is index 5)
        String fromTxt = txFromDate.getText().trim();
        String toTxt   = txToDate.getText().trim();
        if (!fromTxt.isEmpty() && !toTxt.isEmpty()) {
            try {
                LocalDate fd = LocalDate.parse(fromTxt, DATE_FMT);
                LocalDate td = LocalDate.parse(toTxt,   DATE_FMT);
                rows = rows.stream().filter(r -> {
                    if (r[5] == null) return true;
                    LocalDate d = ((java.sql.Date) r[5]).toLocalDate();
                    return !d.isBefore(fd) && !d.isAfter(td);
                }).toList();
            } catch (Exception ignored) {}
        }

        float[] cols = {30, 80, 130, 70, 80, 80, 80};
        Table table = styledTable(cols);
        addHeaderRow(table, bold, "#", "Roll No.", "Student Name", "Room No.", "Block", "Room Type", "Alloc. Date");

        int i = 1;
        for (Object[] r : rows) {
            boolean alt = i % 2 == 0;
            String allocDate = r[5] != null ? ((java.sql.Date) r[5]).toLocalDate().format(DATE_FMT) : "-";
            addRow(table, regular, alt,
                    String.valueOf(i++),
                    r[1] != null ? r[1].toString() : "-",
                    r[0] != null ? r[0].toString() : "-",
                    r[2] != null ? r[2].toString() : "-",
                    r[3] != null ? r[3].toString() : "-",
                    r[4] != null ? r[4].toString() : "-",
                    allocDate);
        }
        doc.add(table);
        addFooter(doc, regular, rows.size() + " active booking(s) listed.");
    }

    // ── R09: Monthly Income Summary ───────────────────────────────────────────
    private void buildMonthlyIncome(Document doc, PdfFont bold, PdfFont regular) throws Exception {
        // Fetch all bills and group by month
        java.util.Map<String, double[]> monthly = new java.util.LinkedHashMap<>();
        try {
            List<FeeBill> bills = new FeeBillDAO().getBillsByMonth("%");
            for (FeeBill b : bills) {
                String key = b.getBillMonth();
                monthly.putIfAbsent(key, new double[]{0, 0});
                monthly.get(key)[0] += b.getAmountDue();
                monthly.get(key)[1] += b.getAmountPaid();
            }
        } catch (Exception e) {
            noDbFallback(doc, bold, regular); return;
        }

        float[] cols = {30, 100, 110, 110, 110};
        Table table = styledTable(cols);
        addHeaderRow(table, bold, "#", "Month", "Total Due (PKR)", "Collected (PKR)", "Outstanding (PKR)");

        int i = 1;
        double grandDue = 0, grandPaid = 0;
        for (java.util.Map.Entry<String, double[]> e : monthly.entrySet()) {
            double due  = e.getValue()[0];
            double paid = e.getValue()[1];
            grandDue  += due;
            grandPaid += paid;
            boolean alt = i % 2 == 0;
            addRow(table, regular, alt,
                    String.valueOf(i++),
                    e.getKey(),
                    String.format("%.0f", due),
                    String.format("%.0f", paid),
                    String.format("%.0f", due - paid));
        }
        doc.add(table);
        addFooter(doc, regular, String.format(
                "Grand Total Due: PKR %.0f  |  Collected: PKR %.0f  |  Outstanding: PKR %.0f",
                grandDue, grandPaid, grandDue - grandPaid));
    }

    // ── Generic placeholder (safety net, should not be reached) ──────────────
    private void buildGenericPlaceholder(Document doc, PdfFont bold, PdfFont regular) throws Exception {
        doc.add(new Paragraph("Report: " + selectedReportTitle)
                .setFont(bold).setFontSize(12).setFontColor(PDF_TEXT_DARK).setMarginTop(20));
        doc.add(new Paragraph("No implementation found for report ID: " + selectedReportId)
                .setFont(regular).setFontSize(11).setFontColor(new DeviceRgb(0x6B, 0x72, 0x80)));
    }

    private void noDbFallback(Document doc, PdfFont bold, PdfFont regular) throws Exception {
        doc.add(new Paragraph("Database Connection Unavailable")
                .setFont(bold).setFontSize(12).setFontColor(new DeviceRgb(0xDC, 0x26, 0x26)).setMarginTop(20));
        doc.add(new Paragraph(
                "Could not reach the database. Please ensure MySQL is running and\n" +
                        "DBConnection is configured correctly, then try again.")
                .setFont(regular).setFontSize(11));
    }

    // ── PDF building helpers ──────────────────────────────────────────────────
    private Table styledTable(float[] widths) {
        Table t = new Table(UnitValue.createPointArray(widths));
        t.setWidth(UnitValue.createPercentValue(100));
        t.setMarginTop(8);
        return t;
    }

    private void addHeaderRow(Table t, PdfFont bold, String... headers) throws Exception {
        for (String h : headers) {
            t.addHeaderCell(new Cell()
                    .add(new Paragraph(h).setFont(bold).setFontSize(9).setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(PDF_HEADER_BG)
                    .setPadding(6)
                    .setBorder(new SolidBorder(PDF_BORDER_COL, 0.5f)));
        }
    }

    private void addRow(Table t, PdfFont regular, boolean alt, String... vals) throws Exception {
        DeviceRgb bg = alt ? PDF_ROW_ALT : new DeviceRgb(255, 255, 255);
        for (String v : vals) {
            t.addCell(new Cell()
                    .add(new Paragraph(v != null ? v : "-").setFont(regular).setFontSize(9)
                            .setFontColor(PDF_TEXT_DARK))
                    .setBackgroundColor(bg)
                    .setPadding(5)
                    .setBorder(new SolidBorder(PDF_BORDER_COL, 0.5f)));
        }
    }

    private void addFooter(Document doc, PdfFont regular, String text) throws Exception {
        doc.add(new Paragraph(text)
                .setFont(regular).setFontSize(9)
                .setFontColor(new DeviceRgb(0x6B, 0x72, 0x80))
                .setMarginTop(8).setTextAlignment(TextAlignment.RIGHT));
    }

    // ── Preview ───────────────────────────────────────────────────────────────
    private void previewReport() {
        if (selectedReportId == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a report first.", "No Report Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Generate to temp file and open
        try {
            File tmp = File.createTempFile("HMS_preview_", ".pdf");
            tmp.deleteOnExit();
            buildPdf(tmp.getAbsolutePath());
            if (java.awt.Desktop.isDesktopSupported())
                java.awt.Desktop.getDesktop().open(tmp);
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(this,
                    "Preview failed: " + ex.getClass().getSimpleName() + ": " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearParams() {
        txFromDate.setText(""); txToDate.setText(""); txStudentId.setText("");
        cmbStatus.setSelectedIndex(0); cmbMonth.setSelectedIndex(0); cmbYear.setSelectedIndex(0);
        selectedReportId = null;
        lblSelectedReport.setText("Select a report from the left panel");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String buildParamString() {
        String from   = txFromDate.getText().trim();
        String to     = txToDate.getText().trim();
        String month  = (String) cmbMonth.getSelectedItem();
        String year   = (String) cmbYear.getSelectedItem();
        String status = (String) cmbStatus.getSelectedItem();
        String sid    = txStudentId.getText().trim();

        StringBuilder sb = new StringBuilder();
        if (!from.isEmpty()) sb.append("From:").append(from).append(" ");
        if (!to.isEmpty())   sb.append("To:").append(to).append(" ");
        if (!"All Months".equals(month)) sb.append(month).append(" ");
        sb.append(year).append(" | ").append(status);
        if (!sid.isEmpty()) sb.append(" | ID:").append(sid);
        return sb.toString().trim();
    }

    private int monthIndex(String month) {
        String[] months = {"January","February","March","April","May","June",
                "July","August","September","October","November","December"};
        for (int i = 0; i < months.length; i++)
            if (months[i].equals(month)) return i + 1;
        return 1;
    }

    private void addCardHeader(JPanel card, String text) {
        JLabel hdr = new JLabel(text);
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 14));
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
        p.add(l); p.add(Box.createVerticalStrut(3)); p.add(field);
        return p;
    }

    private JLabel makeTitleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 22));
        l.setForeground(Theme.TEXT_PRIMARY);
        return l;
    }

    private JLabel makeBadge(String text) {
        JLabel l = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.PRIMARY_LIGHT);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getHeight(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(Theme.PRIMARY);
        l.setBorder(new EmptyBorder(2, 7, 2, 7));
        l.setOpaque(false);
        return l;
    }

    private void styleCombo(JComboBox<?> c) {
        c.setFont(Theme.FONT_INPUT);
        c.setBackground(Theme.BG_CARD);
    }

    private void colorLogStatusColumn() {
        logTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                String s = val == null ? "" : val.toString();
                if (!sel) setBackground(row % 2 == 0 ? Theme.BG_CARD : new Color(0xF9, 0xFA, 0xFB));
                setForeground(switch (s) {
                    case "Success" -> Theme.ACCENT;
                    case "Failed"  -> Theme.DANGER;
                    default        -> Theme.WARNING;
                });
                setFont(Theme.FONT_TABLE_HD);
                return this;
            }
        });
    }

    private void loadSampleLog() {
        Object[][] sample = {
                {1, "Fee Collection Report",  "07/06/2025 10:22", "May 2025 | All",     "Success"},
                {2, "Student Register",        "06/06/2025 14:05", "All | Active",       "Success"},
                {3, "Outstanding Dues",        "05/06/2025 09:30", "June 2025 | Unpaid", "Success"},
                {4, "Room Occupancy Report",   "04/06/2025 16:48", "All Months 2025",    "Failed"},
        };
        for (Object[] row : sample) logModel.addRow(row);
    }
}