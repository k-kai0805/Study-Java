package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class AppConfig {

    private static final Properties PROPERTIES = new Properties();
    private static boolean initialized = false;

    private AppConfig() {
    }

    private static synchronized void initialize() {
        if (initialized) return;

        String configFile = System.getProperty("config.file", "application.properties");
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                throw new IllegalStateException("Cannot load " + configFile);
            }
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load " + configFile, e);
        }
        initialized = true;
    }

    private static void ensureInitialized() {
        if (!initialized) {
            initialize();
        }
    }

    public static String dbUrl() {
        ensureInitialized();
        return PROPERTIES.getProperty("db.url");
    }

    public static String dbUser() {
        ensureInitialized();
        return PROPERTIES.getProperty("db.user");
    }

    public static String dbPassword() {
        ensureInitialized();
        return PROPERTIES.getProperty("db.password");
    }

    // For testing only
    public static void resetForTesting() {
        PROPERTIES.clear();
        initialized = false;
    }
}