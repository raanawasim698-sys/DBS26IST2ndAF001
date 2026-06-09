package domain;

import java.time.LocalDate;

public class Staff {

    private int staffId;
    private String fullName;
    private String cnic;
    private String designation; // Warden / Security / Cleaner / Mess Staff / Accountant
    private double salary;
    private String contactNumber;
    private LocalDate joiningDate;
    private int blockAssigned;
    private String blockName;   // joined field for display
    private boolean isActive;

    // ── Constructors ─────────────────────────────────────────
    public Staff() {}

    public Staff(int staffId, String fullName, String cnic, String designation,
                 double salary, String contactNumber, LocalDate joiningDate,
                 int blockAssigned, boolean isActive) {
        this.staffId       = staffId;
        this.fullName      = fullName;
        this.cnic          = cnic;
        this.designation   = designation;
        this.salary        = salary;
        this.contactNumber = contactNumber;
        this.joiningDate   = joiningDate;
        this.blockAssigned = blockAssigned;
        this.isActive      = isActive;
    }

    // ── Getters & Setters ─────────────────────────────────────
    public int getStaffId()                          { return staffId; }
    public void setStaffId(int staffId)              { this.staffId = staffId; }

    public String getFullName()                      { return fullName; }
    public void setFullName(String fullName)         { this.fullName = fullName; }

    public String getCnic()                          { return cnic; }
    public void setCnic(String cnic)                 { this.cnic = cnic; }

    public String getDesignation()                   { return designation; }
    public void setDesignation(String designation)   { this.designation = designation; }

    public double getSalary()                        { return salary; }
    public void setSalary(double salary)             { this.salary = salary; }

    public String getContactNumber()                 { return contactNumber; }
    public void setContactNumber(String c)           { this.contactNumber = c; }

    public LocalDate getJoiningDate()                { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate){ this.joiningDate = joiningDate; }

    public int getBlockAssigned()                    { return blockAssigned; }
    public void setBlockAssigned(int blockAssigned)  { this.blockAssigned = blockAssigned; }

    public String getBlockName()                     { return blockName; }
    public void setBlockName(String blockName)       { this.blockName = blockName; }

    public boolean isActive()                        { return isActive; }
    public void setActive(boolean active)            { this.isActive = active; }

    @Override
    public String toString() { return fullName + " — " + designation; }
}
