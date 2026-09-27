package config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseConnection {

    private DatabaseConnection() {
    }

    public static Connection getConnection() {

        try {
            String url = AppConfig.dbUrl();
            if (url.startsWith("jdbc:h2:")) {
                Class.forName("org.h2.Driver");
            } else {
                Class.forName("org.postgresql.Driver");
            }
            return DriverManager.getConnection(
                    url,
                    AppConfig.dbUser(),
                    AppConfig.dbPassword()
            );
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to connect to database",
                    e
            );
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "Database driver not found",
                    e
            );
        }
    }

    /**
     * Initialize the database schema for testing.
     * Runs the schema-test.sql file against the current database connection.
     */
    public static void initializeTestSchema() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Read and execute schema-test.sql
            try (InputStream is = DatabaseConnection.class.getClassLoader().getResourceAsStream("schema-test.sql")) {
                if (is == null) {
                    throw new IllegalStateException("schema-test.sql not found in classpath");
                }
                String sql = new String(is.readAllBytes());
                // Split by semicolon and execute each statement
                for (String stmtSql : sql.split(";")) {
                    String trimmed = stmtSql.trim();
                    if (!trimmed.isEmpty()) {
                        stmt.execute(trimmed);
                    }
                }
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Failed to initialize test schema", e);
        }
    }
}