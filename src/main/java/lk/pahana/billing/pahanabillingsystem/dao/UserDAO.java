package lk.pahana.billing.pahanabillingsystem.dao;



import lk.pahana.billing.pahanabillingsystem.model.User;
import lk.pahana.billing.pahanabillingsystem.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // Username එකයි password එකයි check කරන්න
    public User getUserByUsernameAndPassword(String username, String password) {
        User user = null;
        String query = "SELECT * FROM users WHERE username = ? AND password = ?"; // SQL query එක
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection(); // Database connection එක ගන්නවා
            ps = connection.prepareStatement(query); // Query එක prepare කරනවා
            ps.setString(1, username); // username එක set කරනවා
            ps.setString(2, password); // password එක set කරනවා
            rs = ps.executeQuery(); // Query එක execute කරනවා

            if (rs.next()) { // Result set එකේ record එකක් තිබ්බොත්
                user = new User(); // User object එකක් හදනවා
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Connections, Statements, ResultSets වහනවා
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close(); // DBConnection.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user; // User object එක return කරනවා (not found නම් null)
    }
}
