package lk.pahana.billing.pahanabillingsystem.model;

public class BillItem {
    private int billItemId;
    private int billId;
    private String itemId;
    private int quantity;
    private double unitPriceAtSale;
    private double subTotal;

    // Constructors
    public BillItem() {
    }

    // Constructor without billItemId (for new bill items)
    public BillItem(int billId, String itemId, int quantity, double unitPriceAtSale, double subTotal) {
        this.billId = billId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPriceAtSale = unitPriceAtSale;
        this.subTotal = subTotal;
    }

    // Constructor with billItemId (for existing bill items)
    public BillItem(int billItemId, int billId, String itemId, int quantity, double unitPriceAtSale, double subTotal) {
        this.billItemId = billItemId;
        this.billId = billId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPriceAtSale = unitPriceAtSale;
        this.subTotal = subTotal;
    }

    // Getters and Setters
    public int getBillItemId() {
        return billItemId;
    }

    public void setBillItemId(int billItemId) {
        this.billItemId = billItemId;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPriceAtSale() {
        return unitPriceAtSale;
    }

    public void setUnitPriceAtSale(double unitPriceAtSale) {
        this.unitPriceAtSale = unitPriceAtSale;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    @Override
    public String toString() {
        return "BillItem{" +
                "billItemId=" + billItemId +
                ", billId=" + billId +
                ", itemId='" + itemId + '\'' +
                ", quantity=" + quantity +
                ", unitPriceAtSale=" + unitPriceAtSale +
                ", subTotal=" + subTotal +
                '}';
    }
}
