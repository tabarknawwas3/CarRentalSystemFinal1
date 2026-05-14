package carrentalsystem.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://164.92.253.36:3306/car_rental_system"
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=UTC";

    private static final String USER = "12323113_project_db";
    private static final String PASSWORD = "50313776";

    private static Connection connection = null;

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connected to MySQL!");
            }
        } catch (SQLException e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
            throw e;
        }
        return connection;
    }
}