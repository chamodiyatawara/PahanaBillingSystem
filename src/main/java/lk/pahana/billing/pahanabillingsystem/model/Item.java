package lk.pahana.billing.pahanabillingsystem.model;

public class Item {
    private String itemId;
    private String itemName;
    private double unitPrice;
    private int quantityInStock;

    // Constructors (objects හදන්න)
    public Item() {
        // Default constructor
    }

    public Item(String itemId, String itemName, double unitPrice, int quantityInStock) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.unitPrice = unitPrice;
        this.quantityInStock = quantityInStock;
    }

    // Getters and Setters (properties වලට data ගන්න සහ set කරන්න)
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId='" + itemId + '\'' +
                ", itemName='" + itemName + '\'' +
                ", unitPrice=" + unitPrice +
                ", quantityInStock=" + quantityInStock +
                '}';
    }
}
