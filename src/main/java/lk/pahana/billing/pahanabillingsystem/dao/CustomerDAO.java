package lk.pahana.billing.pahanabillingsystem.dao;

import lk.pahana.billing.pahanabillingsystem.model.Customer;
import lk.pahana.billing.pahanabillingsystem.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    // අලුත් Customer කෙනෙක් add කරන්න
    public boolean addCustomer(Customer customer) {
        String query = "INSERT INTO customers (account_number, name, address, telephone_number, units_consumed) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, customer.getAccountNumber());
            ps.setString(2, customer.getName());
            ps.setString(3, customer.getAddress());
            ps.setString(4, customer.getTelephoneNumber());
            ps.setDouble(5, customer.getUnitsConsumed());

            int rowsAffected = ps.executeUpdate(); // Query එක execute කරලා affected rows ගාන ගන්නවා
            return rowsAffected > 0; // 1ට වඩා වැඩි නම් true (success)
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Customer කෙනෙක්ගේ information update කරන්න
    public boolean updateCustomer(Customer customer) {
        String query = "UPDATE customers SET name = ?, address = ?, telephone_number = ?, units_consumed = ? WHERE account_number = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getAddress());
            ps.setString(3, customer.getTelephoneNumber());
            ps.setDouble(4, customer.getUnitsConsumed());
            ps.setString(5, customer.getAccountNumber());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Customer කෙනෙක්ව delete කරන්න
    public boolean deleteCustomer(String accountNumber) {
        String query = "DELETE FROM customers WHERE account_number = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, accountNumber);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Account number එකෙන් Customer කෙනෙක්ව හොයාගන්න
    public Customer getCustomerByAccountNumber(String accountNumber) {
        Customer customer = null;
        String query = "SELECT * FROM customers WHERE account_number = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    customer = new Customer();
                    customer.setAccountNumber(rs.getString("account_number"));
                    customer.setName(rs.getString("name"));
                    customer.setAddress(rs.getString("address"));
                    customer.setTelephoneNumber(rs.getString("telephone_number"));
                    customer.setUnitsConsumed(rs.getDouble("units_consumed"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer;
    }

    // හැම Customer කෙනෙක්වම ගන්න
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customers";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Customer customer = new Customer();
                customer.setAccountNumber(rs.getString("account_number"));
                customer.setName(rs.getString("name"));
                customer.setAddress(rs.getString("address"));
                customer.setTelephoneNumber(rs.getString("telephone_number"));
                customer.setUnitsConsumed(rs.getDouble("units_consumed"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    // Customer Name හෝ Account Number එකෙන් Customers ලා Search කරන්න
    public List<Customer> searchCustomers(String searchTerm) {
        List<Customer> customers = new ArrayList<>();
        // SQL query එකේ LIKE clause එක භාවිතා කරනවා. % කියන්නේ ඕනෑම අකුරු ගානක් වෙන්න පුළුවන්.
        // searchTerm එක lower case කරලා search කරන්නේ, case-insensitive search එකක් වෙන්න.
        String query = "SELECT * FROM customers WHERE LOWER(name) LIKE ? OR LOWER(account_number) LIKE ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            // Search term එකට % එකතු කරනවා, මොකද කොටසක් search කරන්න.
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            ps.setString(1, searchPattern); // name column එකට
            ps.setString(2, searchPattern); // account_number column එකට

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Customer customer = new Customer();
                    customer.setAccountNumber(rs.getString("account_number"));
                    customer.setName(rs.getString("name"));
                    customer.setAddress(rs.getString("address"));
                    customer.setTelephoneNumber(rs.getString("telephone_number"));
                    customer.setUnitsConsumed(rs.getDouble("units_consumed"));
                    customers.add(customer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error searching customers: " + e.getMessage());
        }
        return customers;
    }

    // මුළු Customers ගණන ලබා ගැනීමට
    public int getTotalCustomers() {
        int totalCustomers = 0;
        String query = "SELECT COUNT(account_number) AS total_count FROM customers"; // COUNT() function එක භාවිතා කරනවා

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                totalCustomers = rs.getInt("total_count"); // total_count alias එකෙන් value එක ගන්නවා
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting total customers count: " + e.getMessage());
        }
        return totalCustomers;
    }
}
