package domain;

import java.time.LocalDate;

public class FeeBill {

    private int billId;
    private int studentId;
    private String studentName;  // joined field for display
    private String rollNumber;   // joined field for display
    private String billMonth;    // format: YYYY-MM
    private double amountDue;
    private LocalDate dueDate;
    private double amountPaid;
    private String status;       // Unpaid / Partial / Paid

    // ── Constructors ─────────────────────────────────────────
    public FeeBill() {}

    public FeeBill(int billId, int studentId, String billMonth,
                   double amountDue, LocalDate dueDate,
                   double amountPaid, String status) {
        this.billId      = billId;
        this.studentId   = studentId;
        this.billMonth   = billMonth;
        this.amountDue   = amountDue;
        this.dueDate     = dueDate;
        this.amountPaid  = amountPaid;
        this.status      = status;
    }

    // ── Getters & Setters ─────────────────────────────────────
    public int getBillId()                       { return billId; }
    public void setBillId(int billId)            { this.billId = billId; }

    public int getStudentId()                    { return studentId; }
    public void setStudentId(int studentId)      { this.studentId = studentId; }

    public String getStudentName()               { return studentName; }
    public void setStudentName(String n)         { this.studentName = n; }

    public String getRollNumber()                { return rollNumber; }
    public void setRollNumber(String r)          { this.rollNumber = r; }

    public String getBillMonth()                 { return billMonth; }
    public void setBillMonth(String billMonth)   { this.billMonth = billMonth; }

    public double getAmountDue()                 { return amountDue; }
    public void setAmountDue(double amountDue)   { this.amountDue = amountDue; }

    public LocalDate getDueDate()                { return dueDate; }
    public void setDueDate(LocalDate dueDate)    { this.dueDate = dueDate; }

    public double getAmountPaid()                { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }

    public String getStatus()                    { return status; }
    public void setStatus(String status)         { this.status = status; }

    public double getBalanceDue() {
        return amountDue - amountPaid;
    }

    @Override
    public String toString() {
        return "Bill #" + billId + " — " + billMonth + " — " + status;
    }
}
