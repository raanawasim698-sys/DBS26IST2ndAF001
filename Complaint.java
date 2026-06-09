package domain;

import java.time.LocalDate;

public class Complaint {

    private int complaintId;
    private int studentId;
    private String studentName;  // joined field for display
    private String rollNumber;   // joined field for display
    private int assignedTo;
    private String assignedToName; // joined field for display
    private String category;     // Electrical / Plumbing / Cleanliness / Noise / Food / Security / Other
    private String description;
    private String priority;     // Low / Medium / High
    private String status;       // Pending / In Progress / Resolved / Closed
    private LocalDate filedDate;
    private LocalDate resolvedDate;

    // ── Constructors ─────────────────────────────────────────
    public Complaint() {}

    public Complaint(int complaintId, int studentId, int assignedTo,
                     String category, String description, String priority,
                     String status, LocalDate filedDate, LocalDate resolvedDate) {
        this.complaintId  = complaintId;
        this.studentId    = studentId;
        this.assignedTo   = assignedTo;
        this.category     = category;
        this.description  = description;
        this.priority     = priority;
        this.status       = status;
        this.filedDate    = filedDate;
        this.resolvedDate = resolvedDate;
    }

    // ── Getters & Setters ─────────────────────────────────────
    public int getComplaintId()                      { return complaintId; }
    public void setComplaintId(int complaintId)      { this.complaintId = complaintId; }

    public int getStudentId()                        { return studentId; }
    public void setStudentId(int studentId)          { this.studentId = studentId; }

    public String getStudentName()                   { return studentName; }
    public void setStudentName(String studentName)   { this.studentName = studentName; }

    public String getRollNumber()                    { return rollNumber; }
    public void setRollNumber(String rollNumber)     { this.rollNumber = rollNumber; }

    public int getAssignedTo()                       { return assignedTo; }
    public void setAssignedTo(int assignedTo)        { this.assignedTo = assignedTo; }

    public String getAssignedToName()                { return assignedToName; }
    public void setAssignedToName(String n)          { this.assignedToName = n; }

    public String getCategory()                      { return category; }
    public void setCategory(String category)         { this.category = category; }

    public String getDescription()                   { return description; }
    public void setDescription(String description)   { this.description = description; }

    public String getPriority()                      { return priority; }
    public void setPriority(String priority)         { this.priority = priority; }

    public String getStatus()                        { return status; }
    public void setStatus(String status)             { this.status = status; }

    public LocalDate getFiledDate()                  { return filedDate; }
    public void setFiledDate(LocalDate filedDate)    { this.filedDate = filedDate; }

    public LocalDate getResolvedDate()               { return resolvedDate; }
    public void setResolvedDate(LocalDate resolvedDate){ this.resolvedDate = resolvedDate; }

    @Override
    public String toString() {
        return "Complaint #" + complaintId + " — " + category + " — " + priority;
    }
}
