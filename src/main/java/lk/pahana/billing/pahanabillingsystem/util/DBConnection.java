package lk.pahana.billing.pahanabillingsystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/pahanabillingdb";
    private static final String USER = "root";
    private static final String PASSWORD = "Adminpassword123@1";

    // Database connection එක ලබා ගන්න method එක
    public static Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            // MySQL Driver එක load කරනවා
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Connection එක හදනවා
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connection successful!"); // Console එකේ පෙන්වන්න
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Make sure mysql-connector-java.jar is in classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed. Check URL, username, and password.");
            e.printStackTrace();
            throw e;
        }
        return connection;
    }

    // Connection එක වහන්න method එක
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
