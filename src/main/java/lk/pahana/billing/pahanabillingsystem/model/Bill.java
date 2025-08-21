package lk.pahana.billing.pahanabillingsystem.model;

import java.util.Date;

public class Bill {
    private int billId;
    private String customerAccountNumber;
    private Date billDate;
    private double totalAmount;
    private int userId;

    // Constructors
    public Bill() {
    }

    // Constructor without billId (for new bills)
    public Bill(String customerAccountNumber, Date billDate, double totalAmount, int userId) {
        this.customerAccountNumber = customerAccountNumber;
        this.billDate = billDate;
        this.totalAmount = totalAmount;
        this.userId = userId;
    }

    // Constructor with billId (for existing bills)
    public Bill(int billId, String customerAccountNumber, Date billDate, double totalAmount, int userId) {
        this.billId = billId;
        this.customerAccountNumber = customerAccountNumber;
        this.billDate = billDate;
        this.totalAmount = totalAmount;
        this.userId = userId;
    }

    // Getters and Setters
    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getCustomerAccountNumber() {
        return customerAccountNumber;
    }

    public void setCustomerAccountNumber(String customerAccountNumber) {
        this.customerAccountNumber = customerAccountNumber;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId=" + billId +
                ", customerAccountNumber='" + customerAccountNumber + '\'' +
                ", billDate=" + billDate +
                ", totalAmount=" + totalAmount +
                ", userId=" + userId +
                '}';
    }
}
