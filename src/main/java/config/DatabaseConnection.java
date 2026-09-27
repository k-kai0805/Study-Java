package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    private DatabaseConnection() {
    }

    public static Connection getConnection() {

        try {
            return DriverManager.getConnection(
                    AppConfig.DB_URL,
                    AppConfig.DB_USER,
                    AppConfig.DB_PASSWORD
            );
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to connect to PostgreSQL",
                    e
            );
        }
    }
}