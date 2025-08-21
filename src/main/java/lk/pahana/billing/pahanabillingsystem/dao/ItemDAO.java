package lk.pahana.billing.pahanabillingsystem.dao;

import lk.pahana.billing.pahanabillingsystem.model.Item;
import lk.pahana.billing.pahanabillingsystem.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    // අලුත් Item එකක් Add කරන්න
    public boolean addItem(Item item) {
        String query = "INSERT INTO items (item_id, item_name, unit_price, quantity_in_stock) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection(); // Database connection එක ගන්නවා
             PreparedStatement ps = connection.prepareStatement(query)) { // Query එක prepare කරනවා

            ps.setString(1, item.getItemId()); // 1st '?' ට itemId
            ps.setString(2, item.getItemName()); // 2nd '?' ට itemName
            ps.setDouble(3, item.getUnitPrice()); // 3rd '?' ට unitPrice
            ps.setInt(4, item.getQuantityInStock()); // 4th '?' ට quantityInStock

            int rowsAffected = ps.executeUpdate(); // Query එක execute කරලා affected rows ගාන ගන්නවා
            return rowsAffected > 0; // 1ට වඩා වැඩි නම් true (success)
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding item: " + e.getMessage()); // Console එකේ error message එක print කරන්න
            return false;
        }
    }

    // Item Name හෝ Item ID එකෙන් Items ලා Search කරන්න
    public List<Item> searchItems(String searchTerm) {
        List<Item> items = new ArrayList<>();
        // SQL query එකේ LIKE clause එක භාවිතා කරනවා. % කියන්නේ ඕනෑම අකුරු ගානක් වෙන්න පුළුවන්.
        // searchTerm එක lower case කරලා search කරන්නේ, case-insensitive search එකක් වෙන්න.
        String query = "SELECT * FROM items WHERE LOWER(item_name) LIKE ? OR LOWER(item_id) LIKE ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            // Search term එකට % එකතු කරනවා, මොකද කොටසක් search කරන්න.
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            ps.setString(1, searchPattern); // item_name column එකට
            ps.setString(2, searchPattern); // item_id column එකට

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Item item = new Item();
                    item.setItemId(rs.getString("item_id"));
                    item.setItemName(rs.getString("item_name"));
                    item.setUnitPrice(rs.getDouble("unit_price"));
                    item.setQuantityInStock(rs.getInt("quantity_in_stock"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error searching items: " + e.getMessage());
        }
        return items;
    }

    // Item එකක information Update කරන්න
    public boolean updateItem(Item item) {
        String query = "UPDATE items SET item_name = ?, unit_price = ?, quantity_in_stock = ? WHERE item_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, item.getItemName());
            ps.setDouble(2, item.getUnitPrice());
            ps.setInt(3, item.getQuantityInStock());
            ps.setString(4, item.getItemId()); // WHERE clause එකට itemId

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating item: " + e.getMessage());
            return false;
        }
    }

    // Item එකක් Delete කරන්න
    public boolean deleteItem(String itemId) {
        String query = "DELETE FROM items WHERE item_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, itemId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error deleting item: " + e.getMessage());
            return false;
        }
    }

    // Item ID එකෙන් Item එකක් හොයාගන්න
    public Item getItemById(String itemId) {
        Item item = null;
        String query = "SELECT * FROM items WHERE item_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, itemId);
            try (ResultSet rs = ps.executeQuery()) { // ResultSet එක try-with-resources වලින් close වෙනවා
                if (rs.next()) { // Result set එකේ record එකක් තිබ්බොත්
                    item = new Item(); // Item object එකක් හදනවා
                    item.setItemId(rs.getString("item_id"));
                    item.setItemName(rs.getString("item_name"));
                    item.setUnitPrice(rs.getDouble("unit_price"));
                    item.setQuantityInStock(rs.getInt("quantity_in_stock"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting item by ID: " + e.getMessage());
        }
        return item; // Item object එක return කරනවා (not found නම් null)
    }

    // හැම Item එකක්ම ගන්න
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM items";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) { // ResultSet එකත් try-with-resources වලින් close වෙනවා

            while (rs.next()) { // හැම record එකක්ම iterate කරනවා
                Item item = new Item();
                item.setItemId(rs.getString("item_id"));
                item.setItemName(rs.getString("item_name"));
                item.setUnitPrice(rs.getDouble("unit_price"));
                item.setQuantityInStock(rs.getInt("quantity_in_stock"));
                items.add(item); // List එකට add කරනවා
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting all items: " + e.getMessage());
        }
        return items; // Items list එක return කරනවා
    }

    // Item එකක Quantity එක Update කරන්න (තොගය අඩු කිරීමට/වැඩි කිරීමට)
    public boolean updateItemQuantity(String itemId, int newQuantity) {
        String query = "UPDATE items SET quantity_in_stock = ? WHERE item_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, newQuantity);
            ps.setString(2, itemId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating item quantity: " + e.getMessage());
            return false;
        }
    }

    // මුළු Items ගණන ලබා ගැනීමට
    public int getTotalItems() {
        int totalItems = 0;
        String query = "SELECT COUNT(item_id) AS total_count FROM items";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                totalItems = rs.getInt("total_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting total items count: " + e.getMessage());
        }
        return totalItems;
    }
}
