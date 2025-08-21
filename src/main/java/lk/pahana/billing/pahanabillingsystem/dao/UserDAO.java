package lk.pahana.billing.pahanabillingsystem.dao;



import lk.pahana.billing.pahanabillingsystem.model.User;
import lk.pahana.billing.pahanabillingsystem.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // For getting generated keys (for adding new users)
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Username එකයි password එකයි check කරන්න (role එකත් ගන්නවා)
    public User getUserByUsernameAndPassword(String username, String password) {
        User user = null;
        String query = "SELECT id, username, password, role FROM users WHERE username = ? AND password = ?"; // role column එකත් ගන්නවා
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role")); // *** role එකත් set කරනවා ***
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting user by username and password: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    // User ID එකෙන් User කෙනෙක්ව හොයාගන්න (role එකත් ගන්නවා)
    public User getUserById(int id) {
        User user = null;
        String query = "SELECT id, username, password, role FROM users WHERE id = ?"; // role column එකත් ගන්නවා
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role")); // *** role එකත් set කරනවා ***
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting user by ID: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    // *** අලුත් method එක: අලුත් User කෙනෙක් Register කරන්න ***
    public boolean registerUser(User user) {
        // id AUTO_INCREMENT නිසා, SQL query එකේ id column එක දාන්නේ නැහැ
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) { // Statement.RETURN_GENERATED_KEYS අවශ්‍ය නෑ, මොකද ID එකක් return කරන්නේ නැහැ

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole()); // *** role එකත් set කරනවා ***

            int rowsAffected = ps.executeUpdate(); // Query එක execute කරනවා
            return rowsAffected > 0; // 1ට වඩා වැඩි නම් true (success)
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    // Username එක දැනටමත් තියෙනවද කියලා බලන්න (Registration වලදී වැදගත්)
    public boolean isUsernameExists(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Count එක 0ට වඩා වැඩි නම් username එක තියෙනවා
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error checking username existence: " + e.getMessage());
        }
        return false;
    }

    // සියලුම Users ලා ලබා ගැනීමට
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, username, password, role FROM users ORDER BY username ASC"; // Users ලා username අනුව sort කරනවා

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password")); // Password එකත් ගන්නවා (නමුත් display කරද්දී ප්‍රවේශමෙන්)
                user.setRole(rs.getString("role"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting all users: " + e.getMessage());
        }
        return users;
    }

    // User කෙනෙක්ගේ information Update කරන්න
    public boolean updateUser(User user) {
        // Password එකත් update කරන්න පුළුවන් විදියට query එක හදනවා
        String query = "UPDATE users SET username = ?, password = ?, role = ? WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword()); // Password එක update කරනවා
            ps.setString(3, user.getRole());
            ps.setInt(4, user.getId());

            // *** මේ debugging line එක එකතු කරන්න ***
            System.out.println("Executing UPDATE user query: " + ps.toString()); // Query එක print කරන්න
            // ********************************************

            int rowsAffected = ps.executeUpdate();

            // *** මේ debugging line එක එකතු කරන්න ***
            System.out.println("User UPDATE rows affected: " + rowsAffected); // Affected rows print කරන්න
            // ********************************************
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    // User කෙනෙක්ව Delete කරන්න
    public boolean deleteUser(int id) {
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, id);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
}
