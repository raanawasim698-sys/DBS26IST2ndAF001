package ui;

import ui.util.Theme;
import ui.util.UIFactory;
import dao.UserDAO;
import domain.User;
import software.SessionManager;
import software.Logger;
import software.Validator;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JButton        btnLogin;
    private JLabel         lblError;

    public LoginFrame() { initUI(); }

    private void initUI() {
        setTitle("HMS — Login");
        setSize(860, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));
        file.add(exit);
        bar.add(file);
        setJMenuBar(bar);

        // ── Left brand panel ──────────────────────────────────
        JPanel left = new JPanel(new GridBagLayout());
        left.setBackground(Theme.BG_SIDEBAR);
        left.setPreferredSize(new Dimension(320, 520));

        JPanel brand = new JPanel();
        brand.setBackground(Theme.BG_SIDEBAR);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBorder(new EmptyBorder(0, 28, 0, 28));

        JLabel icon = new JLabel("🏨");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        icon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel t1 = new JLabel("Hostel Management");
        t1.setFont(Theme.FONT_TITLE); t1.setForeground(Theme.TEXT_WHITE);
        t1.setAlignmentX(CENTER_ALIGNMENT);

        JLabel t2 = new JLabel("System");
        t2.setFont(Theme.FONT_TITLE); t2.setForeground(Theme.TEXT_WHITE);
        t2.setAlignmentX(CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BG_SIDEBAR_HOV);
        sep.setMaximumSize(new Dimension(160, 1));

        JLabel uni = new JLabel("UET Lahore — IST 2nd-A");
        uni.setFont(Theme.FONT_SUBTITLE); uni.setForeground(Theme.TEXT_SIDEBAR);
        uni.setAlignmentX(CENTER_ALIGNMENT);

        JLabel code = new JLabel("CSC-104L | 2026");
        code.setFont(Theme.FONT_SUBTITLE); code.setForeground(Theme.TEXT_SIDEBAR);
        code.setAlignmentX(CENTER_ALIGNMENT);

        brand.add(icon);
        brand.add(Box.createVerticalStrut(12));
        brand.add(t1); brand.add(t2);
        brand.add(Box.createVerticalStrut(16));
        brand.add(sep);
        brand.add(Box.createVerticalStrut(16));
        brand.add(uni);
        brand.add(Box.createVerticalStrut(4));
        brand.add(code);
        left.add(brand);

        // ── Right form panel ──────────────────────────────────
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Theme.BG_CARD);
        right.setBorder(new EmptyBorder(20, 48, 20, 48));

        JPanel form = new JPanel();
        form.setBackground(Theme.BG_CARD);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel welcome = new JLabel("Welcome Back");
        welcome.setFont(Theme.FONT_TITLE); welcome.setForeground(Theme.TEXT_PRIMARY);
        welcome.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to continue");
        sub.setFont(Theme.FONT_SUBTITLE); sub.setForeground(Theme.TEXT_SECONDARY);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        form.add(welcome); form.add(Box.createVerticalStrut(4)); form.add(sub);
        form.add(Box.createVerticalStrut(28));

        // Username
        JLabel lu = UIFactory.boldLabel("Username");
        lu.setAlignmentX(LEFT_ALIGNMENT);
        txtUsername = UIFactory.textField(20);
        form.add(lu); form.add(Box.createVerticalStrut(5)); form.add(txtUsername);
        form.add(Box.createVerticalStrut(14));

        // Password
        JLabel lp = UIFactory.boldLabel("Password");
        lp.setAlignmentX(LEFT_ALIGNMENT);
        txtPassword = UIFactory.passwordField(20);
        txtPassword.setAlignmentX(LEFT_ALIGNMENT);
        form.add(lp); form.add(Box.createVerticalStrut(5)); form.add(txtPassword);
        form.add(Box.createVerticalStrut(8));

        // Error
        lblError = new JLabel(" ");
        lblError.setFont(Theme.FONT_SUBTITLE); lblError.setForeground(Theme.DANGER);
        lblError.setAlignmentX(LEFT_ALIGNMENT);
        form.add(lblError); form.add(Box.createVerticalStrut(14));

        // Login button
        btnLogin = UIFactory.primaryButton("Login");
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.BTN_H));
        btnLogin.setAlignmentX(LEFT_ALIGNMENT);
        btnLogin.addActionListener(e -> handleLogin());
        txtPassword.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) handleLogin();
            }
        });
        form.add(btnLogin); form.add(Box.createVerticalStrut(20));

        JLabel footer = new JLabel("DBS26IST2ndAF001 — Sir Rashad Mahmood Khan");
        footer.setFont(Theme.FONT_SUBTITLE); footer.setForeground(Theme.TEXT_SECONDARY);
        footer.setAlignmentX(LEFT_ALIGNMENT);
        form.add(footer);

        right.add(form);
        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (!Validator.isNotEmpty(username)) { lblError.setText("Username is required."); return; }
        if (!Validator.isNotEmpty(password))  { lblError.setText("Password is required."); return; }

        try {
            UserDAO dao  = new UserDAO();
            User    user = dao.authenticate(username, password);
            if (user != null) {
                dao.updateLastLogin(user.getUserId());
                SessionManager.getInstance().setSession(
                        user.getUserId(), user.getUsername(),
                        user.getRole(), user.getStaffId(), user.getStudentId());
                Logger.logAction("LoginFrame", "Login OK: " + username);
                new DashboardFrame().setVisible(true);
                dispose();
            } else {
                lblError.setText("Invalid username or password.");
                txtPassword.setText("");
            }
        } catch (Exception ex) {
            lblError.setText("DB connection error. Check database.");
            Logger.log("LoginFrame", "Login error.", ex);
        }
    }
}