package domain;

import java.time.LocalDate;

public class FeePayment {

    private int paymentId;
    private int billId;
    private double amountPaid;
    private LocalDate paymentDate;
    private String paymentMethod; // Cash / Bank Transfer / Online
    private String receiptNumber;
    private int receivedBy;
    private String receivedByName; // joined field for display

    // ── Constructors ─────────────────────────────────────────
    public FeePayment() {}

    public FeePayment(int paymentId, int billId, double amountPaid,
                      LocalDate paymentDate, String paymentMethod,
                      String receiptNumber, int receivedBy) {
        this.paymentId     = paymentId;
        this.billId        = billId;
        this.amountPaid    = amountPaid;
        this.paymentDate   = paymentDate;
        this.paymentMethod = paymentMethod;
        this.receiptNumber = receiptNumber;
        this.receivedBy    = receivedBy;
    }

    // ── Getters & Setters ─────────────────────────────────────
    public int getPaymentId()                        { return paymentId; }
    public void setPaymentId(int paymentId)          { this.paymentId = paymentId; }

    public int getBillId()                           { return billId; }
    public void setBillId(int billId)                { this.billId = billId; }

    public double getAmountPaid()                    { return amountPaid; }
    public void setAmountPaid(double amountPaid)     { this.amountPaid = amountPaid; }

    public LocalDate getPaymentDate()                { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate){ this.paymentDate = paymentDate; }

    public String getPaymentMethod()                 { return paymentMethod; }
    public void setPaymentMethod(String m)           { this.paymentMethod = m; }

    public String getReceiptNumber()                 { return receiptNumber; }
    public void setReceiptNumber(String r)           { this.receiptNumber = r; }

    public int getReceivedBy()                       { return receivedBy; }
    public void setReceivedBy(int receivedBy)        { this.receivedBy = receivedBy; }

    public String getReceivedByName()                { return receivedByName; }
    public void setReceivedByName(String n)          { this.receivedByName = n; }

    @Override
    public String toString() {
        return "Receipt: " + receiptNumber + " — Rs. " + amountPaid;
    }
}
