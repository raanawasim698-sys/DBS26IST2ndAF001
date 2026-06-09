package domain;

public class User {

    private int userId;
    private String username;
    private String passwordHash;
    private String role; // Admin / Warden / Accountant / Security / Student
    private int staffId;
    private int studentId;
    private boolean isActive;

    public User() {}

    public User(int userId, String username, String passwordHash,
                String role, int staffId, int studentId, boolean isActive) {
        this.userId       = userId;
        this.username     = username;
        this.passwordHash = passwordHash;
        this.role         = role;
        this.staffId      = staffId;
        this.studentId    = studentId;
        this.isActive     = isActive;
    }

    public int getUserId()                       { return userId; }
    public void setUserId(int userId)            { this.userId = userId; }

    public String getUsername()                  { return username; }
    public void setUsername(String username)     { this.username = username; }

    public String getPasswordHash()              { return passwordHash; }
    public void setPasswordHash(String p)        { this.passwordHash = p; }

    public String getRole()                      { return role; }
    public void setRole(String role)             { this.role = role; }

    public int getStaffId()                      { return staffId; }
    public void setStaffId(int staffId)          { this.staffId = staffId; }

    public int getStudentId()                    { return studentId; }
    public void setStudentId(int studentId)      { this.studentId = studentId; }

    public boolean isActive()                    { return isActive; }
    public void setActive(boolean active)        { this.isActive = active; }

    @Override
    public String toString() { return username + " (" + role + ")"; }
}
