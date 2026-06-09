package domain;

import java.time.LocalDate;

public class Student {

    private int studentId;
    private String fullName;
    private String fatherName;
    private String cnic;
    private String rollNumber;
    private int deptId;
    private String program;
    private int semester;
    private String contactNumber;
    private String emergencyContact;
    private String allotmentType;   // Boarder / Day Scholar
    private LocalDate registrationDate;
    private String status;          // Active / Departed / Suspended

    // ── Constructors ─────────────────────────────────────────
    public Student() {}

    public Student(int studentId, String fullName, String fatherName, String cnic,
                   String rollNumber, int deptId, String program, int semester,
                   String contactNumber, String emergencyContact,
                   String allotmentType, LocalDate registrationDate, String status) {
        this.studentId         = studentId;
        this.fullName          = fullName;
        this.fatherName        = fatherName;
        this.cnic              = cnic;
        this.rollNumber        = rollNumber;
        this.deptId            = deptId;
        this.program           = program;
        this.semester          = semester;
        this.contactNumber     = contactNumber;
        this.emergencyContact  = emergencyContact;
        this.allotmentType     = allotmentType;
        this.registrationDate  = registrationDate;
        this.status            = status;
    }

    // ── Getters & Setters ─────────────────────────────────────
    public int getStudentId()                        { return studentId; }
    public void setStudentId(int studentId)          { this.studentId = studentId; }

    public String getFullName()                      { return fullName; }
    public void setFullName(String fullName)         { this.fullName = fullName; }

    public String getFatherName()                    { return fatherName; }
    public void setFatherName(String fatherName)     { this.fatherName = fatherName; }

    public String getCnic()                          { return cnic; }
    public void setCnic(String cnic)                 { this.cnic = cnic; }

    public String getRollNumber()                    { return rollNumber; }
    public void setRollNumber(String rollNumber)     { this.rollNumber = rollNumber; }

    public int getDeptId()                           { return deptId; }
    public void setDeptId(int deptId)                { this.deptId = deptId; }

    public String getProgram()                       { return program; }
    public void setProgram(String program)           { this.program = program; }

    public int getSemester()                         { return semester; }
    public void setSemester(int semester)            { this.semester = semester; }

    public String getContactNumber()                 { return contactNumber; }
    public void setContactNumber(String contactNumber){ this.contactNumber = contactNumber; }

    public String getEmergencyContact()              { return emergencyContact; }
    public void setEmergencyContact(String ec)       { this.emergencyContact = ec; }

    public String getAllotmentType()                  { return allotmentType; }
    public void setAllotmentType(String allotmentType){ this.allotmentType = allotmentType; }

    public LocalDate getRegistrationDate()           { return registrationDate; }
    public void setRegistrationDate(LocalDate d)     { this.registrationDate = d; }

    public String getStatus()                        { return status; }
    public void setStatus(String status)             { this.status = status; }

    @Override
    public String toString() {
        return rollNumber + " — " + fullName;
    }
}
