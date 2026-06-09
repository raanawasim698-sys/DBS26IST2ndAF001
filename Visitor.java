package domain;

import java.time.LocalDateTime;

public class Visitor {

    private int visitorId;
    private String visitorName;
    private String cnic;
    private String relation;
    private int studentId;
    private String studentName;  // joined field for display
    private String rollNumber;   // joined field for display
    private String roomNumber;   // joined field for display
    private String purpose;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private int loggedBy;
    private String loggedByName; // joined field for display

    // ── Constructors ─────────────────────────────────────────
    public Visitor() {}

    public Visitor(int visitorId, String visitorName, String cnic,
                   String relation, int studentId, String purpose,
                   LocalDateTime entryTime, LocalDateTime exitTime, int loggedBy) {
        this.visitorId   = visitorId;
        this.visitorName = visitorName;
        this.cnic        = cnic;
        this.relation    = relation;
        this.studentId   = studentId;
        this.purpose     = purpose;
        this.entryTime   = entryTime;
        this.exitTime    = exitTime;
        this.loggedBy    = loggedBy;
    }

    // ── Getters & Setters ─────────────────────────────────────
    public int getVisitorId()                        { return visitorId; }
    public void setVisitorId(int visitorId)          { this.visitorId = visitorId; }

    public String getVisitorName()                   { return visitorName; }
    public void setVisitorName(String visitorName)   { this.visitorName = visitorName; }

    public String getCnic()                          { return cnic; }
    public void setCnic(String cnic)                 { this.cnic = cnic; }

    public String getRelation()                      { return relation; }
    public void setRelation(String relation)         { this.relation = relation; }

    public int getStudentId()                        { return studentId; }
    public void setStudentId(int studentId)          { this.studentId = studentId; }

    public String getStudentName()                   { return studentName; }
    public void setStudentName(String studentName)   { this.studentName = studentName; }

    public String getRollNumber()                    { return rollNumber; }
    public void setRollNumber(String rollNumber)     { this.rollNumber = rollNumber; }

    public String getRoomNumber()                    { return roomNumber; }
    public void setRoomNumber(String roomNumber)     { this.roomNumber = roomNumber; }

    public String getPurpose()                       { return purpose; }
    public void setPurpose(String purpose)           { this.purpose = purpose; }

    public LocalDateTime getEntryTime()              { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime){ this.entryTime = entryTime; }

    public LocalDateTime getExitTime()               { return exitTime; }
    public void setExitTime(LocalDateTime exitTime)  { this.exitTime = exitTime; }

    public int getLoggedBy()                         { return loggedBy; }
    public void setLoggedBy(int loggedBy)            { this.loggedBy = loggedBy; }

    public String getLoggedByName()                  { return loggedByName; }
    public void setLoggedByName(String n)            { this.loggedByName = n; }

    public boolean isInsideHostel() {
        return exitTime == null;
    }

    @Override
    public String toString() {
        return visitorName + " visiting " + studentName;
    }
}
