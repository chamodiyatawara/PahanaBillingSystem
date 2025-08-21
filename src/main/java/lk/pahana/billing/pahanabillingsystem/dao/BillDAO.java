package lk.pahana.billing.pahanabillingsystem.dao;


import lk.pahana.billing.pahanabillingsystem.model.Bill;
import lk.pahana.billing.pahanabillingsystem.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // For getting generated keys
import java.sql.Timestamp; // For LocalDateTime to Timestamp conversion
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    // අලුත් Bill එකක් Add කරන්න
    // මේ method එකෙන් Bill එක database එකට දාලා, generate වුණ bill_id එක return කරනවා.
    public int addBill(Bill bill) {
        String query = "INSERT INTO bills (customer_account_number, bill_date, total_amount, user_id) VALUES (?, ?, ?, ?)";
        int generatedBillId = -1; // Default value if no ID is generated

        // Statement.RETURN_GENERATED_KEYS වලින් AUTO_INCREMENT ID එක ලබා ගන්නවා
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, bill.getCustomerAccountNumber());
            // LocalDateTime එක Timestamp එකකට convert කරනවා database එකට දාන්න
            ps.setTimestamp(2, new Timestamp(bill.getBillDate().getTime()));
            ps.setDouble(3, bill.getTotalAmount());
            ps.setInt(4, bill.getUserId()); // User ID එක

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                // Generate වුණ Key (bill_id) එක ලබා ගන්නවා
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedBillId = rs.getInt(1); // 1st column එකේ ID එක
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding bill: " + e.getMessage());
        }
        return generatedBillId; // Generated Bill ID එක return කරනවා
    }

    // Bill ID එකෙන් Bill එකක් හොයාගන්න
    public Bill getBillById(int billId) {
        Bill bill = null;
        String query = "SELECT * FROM bills WHERE bill_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    bill = new Bill();
                    bill.setBillId(rs.getInt("bill_id"));
                    bill.setCustomerAccountNumber(rs.getString("customer_account_number"));
                    // Timestamp එක LocalDateTime එකකට convert කරනවා
                    bill.setBillDate(new Date(rs.getTimestamp("bill_date").getTime()));
                    bill.setTotalAmount(rs.getDouble("total_amount"));
                    bill.setUserId(rs.getInt("user_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting bill by ID: " + e.getMessage());
        }
        return bill;
    }

    // සියලුම Bills ලා ගන්න
    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        String query = "SELECT * FROM bills ORDER BY bill_date DESC"; // අලුත්ම bills මුලින්ම පෙන්වන්න
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Bill bill = new Bill();
                bill.setBillId(rs.getInt("bill_id"));
                bill.setCustomerAccountNumber(rs.getString("customer_account_number"));
                bill.setBillDate(new Date(rs.getTimestamp("bill_date").getTime()));
                bill.setTotalAmount(rs.getDouble("total_amount"));
                bill.setUserId(rs.getInt("user_id"));
                bills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting all bills: " + e.getMessage());
        }
        return bills;
    }

    // Bill එකක් Delete කරන්න (අවශ්‍ය නම්)
    public boolean deleteBill(int billId) {
        String query = "DELETE FROM bills WHERE bill_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, billId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error deleting bill: " + e.getMessage());
            return false;
        }
    }

    // මුළු Sales Amount එක ලබා ගැනීමට
    public double getTotalSalesAmount() {
        double totalSalesAmount = 0.0;
        String query = "SELECT SUM(total_amount) AS total_sales FROM bills"; // SUM() function එක භාවිතා කරනවා

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                totalSalesAmount = rs.getDouble("total_sales"); // total_sales alias එකෙන් value එක ගන්නවා
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting total sales amount: " + e.getMessage());
        }
        return totalSalesAmount;
    }

    // Customer Account Number හෝ Bill ID එකෙන් Bills ලා Search කරන්න
    public List<Bill> searchBills(String searchTerm) {
        List<Bill> bills = new ArrayList<>();
        // SQL query එකේ LIKE clause එක භාවිතා කරනවා.
        // searchTerm එක lower case කරලා search කරන්නේ, case-insensitive search එකක් වෙන්න.
        // Bill ID එක INT නිසා, ඒක String එකකට convert කරලා search කරනවා.
        String query = "SELECT * FROM bills WHERE LOWER(customer_account_number) LIKE ? OR CAST(bill_id AS CHAR) LIKE ? ORDER BY bill_date DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            ps.setString(1, searchPattern); // customer_account_number column එකට
            ps.setString(2, searchPattern); // bill_id column එකට (CAST කරලා)

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bill bill = new Bill();
                    bill.setBillId(rs.getInt("bill_id"));
                    bill.setCustomerAccountNumber(rs.getString("customer_account_number"));
                    bill.setBillDate(new Date(rs.getTimestamp("bill_date").getTime()));
                    bill.setTotalAmount(rs.getDouble("total_amount"));
                    bill.setUserId(rs.getInt("user_id"));
                    bills.add(bill);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error searching bills: " + e.getMessage());
        }
        return bills;
    }
}
