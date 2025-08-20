package lk.pahana.billing.pahanabillingsystem.dao;



import lk.pahana.billing.pahanabillingsystem.model.User;
import lk.pahana.billing.pahanabillingsystem.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // Check the Username and Password
    public User getUserByUsernameAndPassword(String username, String password) {
        User user = null;
        String query = "SELECT * FROM users WHERE username = ? AND password = ?"; // SQL query එක
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection(); // Database connection
            ps = connection.prepareStatement(query); // Prepare the Query
            ps.setString(1, username); // Set username
            ps.setString(2, password); // Set password
            rs = ps.executeQuery(); // execute Query

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Connections, Statements, ResultSets
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close(); // DBConnection.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }
}
