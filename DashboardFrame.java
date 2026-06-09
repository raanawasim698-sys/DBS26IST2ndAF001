package ui;

import ui.util.Theme;
import ui.util.UIFactory;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * HMS Main Application Window.
 *
 * Layout:
 *   ┌─────────────────────────────────────────────────┐
 *   │  Top bar (logo + user info + clock)              │
 *   ├──────────┬──────────────────────────────────────┤
 *   │ Sidebar  │  Content panel (card-swapped)         │
 *   │  (nav)   │                                       │
 *   └──────────┴──────────────────────────────────────┘
 *
 * Each sidebar item swaps the content panel via CardLayout.
 */
public class DashboardFrame extends JFrame {

    // ── Nav items: [label, card-key, emoji-icon] ──────────────────────────────
    private static final String[][] NAV_ITEMS = {
        {"Dashboard",   "HOME",       "\uD83D\uDCCA"},  // 📊
        {"Students",    "STUDENTS",   "\uD83D\uDC65"},  // 👥
        {"Rooms",       "ROOMS",      "\uD83D\uDECF"},  // 🛏
        {"Bookings",    "BOOKINGS",   "\uD83D\uDCC5"},  // 📅
        {"Fees",        "FEES",       "\uD83D\uDCB3"},  // 💳
        {"Complaints",  "COMPLAINTS", "\uD83D\uDCCB"},  // 📋
        {"Staff",       "STAFF",      "\uD83D\uDC77"},  // 👷
        {"Visitors",    "VISITORS",   "\uD83D\uDEB6"},  // 🚶
        {"Reports",     "REPORTS",    "\uD83D\uDCC4"},  // 📄
    };

    private final CardLayout   cardLayout   = new CardLayout();
    private final JPanel       contentPanel = new JPanel(cardLayout);
    private String             activeCard   = "HOME";
    private final JLabel       lblClock     = new JLabel();
    private final JLabel       lblBreadcrumb = new JLabel("Dashboard");

    // ── Constructor ───────────────────────────────────────────────────────────
    public DashboardFrame() {
        super("Hostel Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        buildMenuBar();
        buildUI();
        startClock();
    }

    // ── Menu bar ──────────────────────────────────────────────────────────────
    private void buildMenuBar() {
        JMenuBar mb = new JMenuBar();
        mb.setBackground(Theme.BG_CARD);
        mb.setBorder(new MatteBorder(0, 0, 1, 0, Theme.BORDER));

        // File menu
        JMenu file = new JMenu("File");
        file.setFont(Theme.FONT_LABEL);
        addItem(file, "Logout",        e -> logout());
        file.addSeparator();
        addItem(file, "Exit",          e -> System.exit(0));

        // Manage menu
        JMenu manage = new JMenu("Manage");
        manage.setFont(Theme.FONT_LABEL);
        addItem(manage, "Students",    e -> showCard("STUDENTS"));
        addItem(manage, "Rooms",       e -> showCard("ROOMS"));
        addItem(manage, "Bookings",    e -> showCard("BOOKINGS"));
        addItem(manage, "Fees",        e -> showCard("FEES"));
        addItem(manage, "Staff",       e -> showCard("STAFF"));

        // Reports menu
        JMenu reports = new JMenu("Reports");
        reports.setFont(Theme.FONT_LABEL);
        addItem(reports, "All Reports", e -> showCard("REPORTS"));

        // Help menu
        JMenu help = new JMenu("Help");
        help.setFont(Theme.FONT_LABEL);
        addItem(help, "About HMS", e ->
            JOptionPane.showMessageDialog(this,
                "Hostel Management System\nCSC-104L Term Project\nUET Lahore — 2026",
                "About", JOptionPane.INFORMATION_MESSAGE));

        mb.add(file);
        mb.add(manage);
        mb.add(reports);
        mb.add(help);
        setJMenuBar(mb);
    }

    private void addItem(JMenu menu, String text, ActionListener al) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(Theme.FONT_LABEL);
        item.addActionListener(al);
        menu.add(item);
    }

    // ── Main UI ───────────────────────────────────────────────────────────────
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_MAIN);

        root.add(buildTopBar(),    BorderLayout.NORTH);
        root.add(buildSidebar(),   BorderLayout.WEST);
        root.add(buildContent(),   BorderLayout.CENTER);

        setContentPane(root);
    }

    // ── Top bar ───────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.BG_CARD);
        bar.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, Theme.BORDER),
            new EmptyBorder(0, 16, 0, 16)));
        bar.setPreferredSize(new Dimension(0, 52));

        // left: breadcrumb
        lblBreadcrumb.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblBreadcrumb.setForeground(Theme.TEXT_PRIMARY);

        // right: clock + user badge
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        right.setOpaque(false);

        lblClock.setFont(Theme.FONT_SUBTITLE);
        lblClock.setForeground(Theme.TEXT_SECONDARY);

        JLabel userLbl = new JLabel("Admin  \u25BC");   // ▼
        userLbl.setFont(Theme.FONT_LABEL_BOLD);
        userLbl.setForeground(Theme.TEXT_PRIMARY);
        userLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        userLbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                String[] opts = {"Change Password", "Logout"};
                int choice = JOptionPane.showOptionDialog(DashboardFrame.this,
                    "Logged in as: Admin", "Account",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, opts, opts[0]);
                if (choice == 1) logout();
            }
        });

        right.add(lblClock);
        right.add(new JSeparator(JSeparator.VERTICAL));
        right.add(userLbl);

        bar.add(lblBreadcrumb, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(Theme.BG_SIDEBAR);
        sb.setPreferredSize(new Dimension(Theme.SIDEBAR_W, 0));
        sb.setBorder(new EmptyBorder(8, 0, 8, 0));

        // Logo area
        JPanel logo = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        logo.setOpaque(false);
        JLabel logoIcon = new JLabel("\uD83C\uDFE0");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        JLabel logoText = new JLabel("HMS");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoText.setForeground(Color.WHITE);
        logo.add(logoIcon);
        logo.add(logoText);
        logo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        sb.add(logo);

        // divider
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 30));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sb.add(sep);
        sb.add(Box.createVerticalStrut(4));

        // nav items
        ButtonGroup group = new ButtonGroup();
        for (String[] nav : NAV_ITEMS) {
            JToggleButton btn = navButton(nav[2] + "  " + nav[0], nav[1]);
            if (nav[1].equals("HOME")) btn.setSelected(true);
            group.add(btn);
            sb.add(btn);
        }

        sb.add(Box.createVerticalGlue());

        // bottom: logout
        JButton logoutBtn = new JButton("\uD83D\uDD12  Logout");
        logoutBtn.setFont(Theme.FONT_SIDEBAR);
        logoutBtn.setForeground(new Color(0xFC, 0xA5, 0xA5));
        logoutBtn.setBackground(Theme.BG_SIDEBAR);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        logoutBtn.setHorizontalAlignment(SwingConstants.LEFT);
        logoutBtn.setBorder(new EmptyBorder(0, 16, 0, 16));
        logoutBtn.addActionListener(e -> logout());
        sb.add(logoutBtn);

        return sb;
    }

    private JToggleButton navButton(String text, String card) {
        JToggleButton b = new JToggleButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    // selected: blue left-accent + tinted bg
                    g2.setColor(new Color(255, 255, 255, 18));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(Color.WHITE);
                    g2.fillRect(0, 6, 4, getHeight() - 12);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 12));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(Theme.FONT_SIDEBAR);
        b.setForeground(isSelected(b) ? Color.WHITE : Theme.TEXT_SIDEBAR);
        b.setBackground(Theme.BG_SIDEBAR);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.setBorder(new EmptyBorder(0, 20, 0, 16));

        b.addChangeListener(e -> b.setForeground(b.isSelected() ? Color.WHITE : Theme.TEXT_SIDEBAR));
        b.addActionListener(e -> showCard(card));
        return b;
    }

    private boolean isSelected(JToggleButton b) { return b.isSelected(); }

    // ── Content panel ─────────────────────────────────────────────────────────
    private JPanel buildContent() {
        contentPanel.setBackground(Theme.BG_MAIN);

        // Register all sub-panels under their card keys
        contentPanel.add(buildHomePanel(),           "HOME");
        contentPanel.add(new StudentForm(),          "STUDENTS");
        contentPanel.add(new RoomForm(),             "ROOMS");
        contentPanel.add(new BookingForm(),          "BOOKINGS");
        contentPanel.add(new FeeForm(),              "FEES");
        contentPanel.add(new ComplaintForm(),        "COMPLAINTS");
        contentPanel.add(new StaffForm(),            "STAFF");
        contentPanel.add(new VisitorForm(),          "VISITORS");
        contentPanel.add(new MaintenanceForm(),      "MAINTENANCE");
        contentPanel.add(new NoticeForm(),           "NOTICES");
        contentPanel.add(new ReportsPanel(),         "REPORTS");

        cardLayout.show(contentPanel, "HOME");
        return contentPanel;
    }

    // ── Home / Dashboard panel ────────────────────────────────────────────────
    private JPanel buildHomePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.BG_MAIN);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Page title
        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Stats row — replace numbers with DAO queries
        JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0));
        stats.setOpaque(false);
        stats.add(UIFactory.statCard("128",  "Total Students",    Theme.PRIMARY));
        stats.add(UIFactory.statCard("48",   "Occupied Rooms",    Theme.ACCENT));
        stats.add(UIFactory.statCard("12",   "Pending Fees",      Theme.WARNING));
        stats.add(UIFactory.statCard("5",    "Open Complaints",   Theme.DANGER));

        // Quick-action buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(20, 0, 20, 0));

        JButton btnStudent  = UIFactory.primaryButton("+ New Student");
        JButton btnRoom     = UIFactory.secondaryButton("+ New Room");
        JButton btnBooking  = UIFactory.secondaryButton("+ New Booking");

        btnStudent.addActionListener(e -> showCard("STUDENTS"));
        btnRoom.addActionListener(e   -> showCard("ROOMS"));
        btnBooking.addActionListener(e-> showCard("BOOKINGS"));

        actions.add(btnStudent);
        actions.add(btnRoom);
        actions.add(btnBooking);

        // Placeholder "recent activity" table
        String[] cols = {"#", "Event", "Name", "Time", "Status"};
        Object[][] rows = {
            {"1", "New Booking",   "Ali Hassan",     "Today 09:15", statusBadgeText("Active")},
            {"2", "Fee Payment",   "Sara Ahmed",     "Today 08:50", statusBadgeText("Paid")},
            {"3", "Complaint",     "Usman Tariq",    "Yesterday",   statusBadgeText("Open")},
            {"4", "Check-in",      "Fatima Malik",   "Yesterday",   statusBadgeText("Active")},
            {"5", "Room Vacated",  "Ibrahim Khan",   "2 days ago",  statusBadgeText("Closed")},
        };
        JTable recent = new JTable(rows, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        UIFactory.styleTable(recent);

        JLabel recentLbl = new JLabel("Recent Activity");
        recentLbl.setFont(Theme.FONT_LABEL_BOLD);
        recentLbl.setForeground(Theme.TEXT_PRIMARY);
        recentLbl.setBorder(new EmptyBorder(0, 0, 8, 0));

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(stats,   BorderLayout.NORTH);
        center.add(actions, BorderLayout.CENTER);

        JPanel tableArea = new JPanel(new BorderLayout());
        tableArea.setOpaque(false);
        tableArea.add(recentLbl,                              BorderLayout.NORTH);
        tableArea.add(UIFactory.tableScrollPane(recent),     BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.add(center,    BorderLayout.NORTH);
        body.add(tableArea, BorderLayout.CENTER);

        p.add(title, BorderLayout.NORTH);
        p.add(body,  BorderLayout.CENTER);
        return p;
    }

    private String statusBadgeText(String s) { return s; }  // plain text for table

    // ── Card switching ────────────────────────────────────────────────────────
    private void showCard(String key) {
        activeCard = key;
        cardLayout.show(contentPanel, key);
        // update breadcrumb
        for (String[] nav : NAV_ITEMS) {
            if (nav[1].equals(key)) { lblBreadcrumb.setText(nav[0]); break; }
        }
    }

    // ── Clock ─────────────────────────────────────────────────────────────────
    private void startClock() {
        Timer t = new Timer(1000, e -> {
            lblClock.setText(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("EEE, dd MMM  HH:mm:ss")));
        });
        t.setInitialDelay(0);
        t.start();
    }

    // ── Logout ────────────────────────────────────────────────────────────────
    private void logout() {
        int c = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?", "Logout",
            JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }
}
