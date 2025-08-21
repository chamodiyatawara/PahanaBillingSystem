package lk.pahana.billing.pahanabillingsystem.dao;

import lk.pahana.billing.pahanabillingsystem.model.BillItem;
import lk.pahana.billing.pahanabillingsystem.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // For getting generated keys
import java.util.ArrayList;
import java.util.List;

public class BillItemDAO {

    // අලුත් Bill Item එකක් Add කරන්න
    // මේ method එකෙන් Bill Item එක database එකට දාලා, generate වුණ bill_item_id එක return කරනවා.
    public int addBillItem(BillItem billItem) {
        String query = "INSERT INTO bill_items (bill_id, item_id, quantity, unit_price_at_sale, sub_total) VALUES (?, ?, ?, ?, ?)";
        int generatedBillItemId = -1;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, billItem.getBillId());
            ps.setString(2, billItem.getItemId());
            ps.setInt(3, billItem.getQuantity());
            ps.setDouble(4, billItem.getUnitPriceAtSale());
            ps.setDouble(5, billItem.getSubTotal());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedBillItemId = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding bill item: " + e.getMessage());
        }
        return generatedBillItemId;
    }

    // Bill ID එකකින් අදාළ සියලුම Bill Items ගන්න
    public List<BillItem> getBillItemsByBillId(int billId) {
        List<BillItem> billItems = new ArrayList<>();
        String query = "SELECT * FROM bill_items WHERE bill_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BillItem billItem = new BillItem();
                    billItem.setBillItemId(rs.getInt("bill_item_id"));
                    billItem.setBillId(rs.getInt("bill_id"));
                    billItem.setItemId(rs.getString("item_id"));
                    billItem.setQuantity(rs.getInt("quantity"));
                    billItem.setUnitPriceAtSale(rs.getDouble("unit_price_at_sale"));
                    billItem.setSubTotal(rs.getDouble("sub_total"));
                    billItems.add(billItem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting bill items by bill ID: " + e.getMessage());
        }
        return billItems;
    }

    // Bill එකක් delete කරද්දී ඒකට අදාළ Bill Items delete කරන්න (Transaction එකකදී භාවිතා කළ යුතුයි)
    public boolean deleteBillItemsByBillId(int billId) {
        String query = "DELETE FROM bill_items WHERE bill_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, billId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error deleting bill items by bill ID: " + e.getMessage());
            return false;
        }
    }
}
