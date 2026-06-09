package software;

import java.time.LocalDateTime;

/**
 * SessionManager — Tracks the currently logged-in user for the session.
 * Set on successful login. Clear on logout.
 */
public class SessionManager {

    private static SessionManager instance;

    private int userId;
    private String username;
    private String role;           // Admin / Warden / Accountant / Security / Student
    private int staffId;
    private int studentId;
    private LocalDateTime loginTime;
    private boolean loggedIn;

    // ── Private constructor (singleton) ───────────────────────
    private SessionManager() {
        this.loggedIn = false;
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // ── Set session on login ──────────────────────────────────
    public void setSession(int userId, String username, String role,
                           int staffId, int studentId) {
        this.userId    = userId;
        this.username  = username;
        this.role      = role;
        this.staffId   = staffId;
        this.studentId = studentId;
        this.loginTime = LocalDateTime.now();
        this.loggedIn  = true;
        Logger.logAction("SessionManager", "User [" + username + "] logged in with role [" + role + "]");
    }

    // ── Clear session on logout ───────────────────────────────
    public void logout() {
        Logger.logAction("SessionManager", "User [" + username + "] logged out.");
        this.userId    = 0;
        this.username  = null;
        this.role      = null;
        this.staffId   = 0;
        this.studentId = 0;
        this.loginTime = null;
        this.loggedIn  = false;
    }

    // ── Role checks ───────────────────────────────────────────
    public boolean isAdmin()       { return "Admin".equals(role); }
    public boolean isWarden()      { return "Warden".equals(role); }
    public boolean isAccountant()  { return "Accountant".equals(role); }
    public boolean isSecurity()    { return "Security".equals(role); }
    public boolean isStudent()     { return "Student".equals(role); }
    public boolean isLoggedIn()    { return loggedIn; }

    // ── Getters ───────────────────────────────────────────────
    public int getUserId()         { return userId; }
    public String getUsername()    { return username; }
    public String getRole()        { return role; }
    public int getStaffId()        { return staffId; }
    public int getStudentId()      { return studentId; }
    public LocalDateTime getLoginTime() { return loginTime; }
}
