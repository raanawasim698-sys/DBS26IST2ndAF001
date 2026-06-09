package domain;

import java.time.LocalDate;

public class MaintenanceRequest {

    private int requestId;
    private String location;
    private String issueType;
    private String description;
    private int reportedBy;
    private String reportedByName; // joined field for display
    private int assignedTo;
    private String assignedToName; // joined field for display
    private String status;         // Open / Assigned / In Progress / Completed
    private LocalDate reportedDate;
    private LocalDate completedDate;
    private double cost;

    // ── Constructors ─────────────────────────────────────────
    public MaintenanceRequest() {}

    public MaintenanceRequest(int requestId, String location, String issueType,
                              String description, int reportedBy, int assignedTo,
                              String status, LocalDate reportedDate,
                              LocalDate completedDate, double cost) {
        this.requestId     = requestId;
        this.location      = location;
        this.issueType     = issueType;
        this.description   = description;
        this.reportedBy    = reportedBy;
        this.assignedTo    = assignedTo;
        this.status        = status;
        this.reportedDate  = reportedDate;
        this.completedDate = completedDate;
        this.cost          = cost;
    }

    // ── Getters & Setters ─────────────────────────────────────
    public int getRequestId()                        { return requestId; }
    public void setRequestId(int requestId)          { this.requestId = requestId; }

    public String getLocation()                      { return location; }
    public void setLocation(String location)         { this.location = location; }

    public String getIssueType()                     { return issueType; }
    public void setIssueType(String issueType)       { this.issueType = issueType; }

    public String getDescription()                   { return description; }
    public void setDescription(String description)   { this.description = description; }

    public int getReportedBy()                       { return reportedBy; }
    public void setReportedBy(int reportedBy)        { this.reportedBy = reportedBy; }

    public String getReportedByName()                { return reportedByName; }
    public void setReportedByName(String n)          { this.reportedByName = n; }

    public int getAssignedTo()                       { return assignedTo; }
    public void setAssignedTo(int assignedTo)        { this.assignedTo = assignedTo; }

    public String getAssignedToName()                { return assignedToName; }
    public void setAssignedToName(String n)          { this.assignedToName = n; }

    public String getStatus()                        { return status; }
    public void setStatus(String status)             { this.status = status; }

    public LocalDate getReportedDate()               { return reportedDate; }
    public void setReportedDate(LocalDate d)         { this.reportedDate = d; }

    public LocalDate getCompletedDate()              { return completedDate; }
    public void setCompletedDate(LocalDate d)        { this.completedDate = d; }

    public double getCost()                          { return cost; }
    public void setCost(double cost)                 { this.cost = cost; }

    @Override
    public String toString() {
        return "Request #" + requestId + " — " + location + " — " + issueType;
    }
}
