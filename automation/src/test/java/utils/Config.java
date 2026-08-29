package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Config — Loads test configuration from config.properties
 * ==========================================================
 * Centralises all configurable values (URLs, credentials, expected messages)
 * so that tests never have hard-coded strings in them.
 *
 * Usage:
 *   String email    = Config.get("valid.email");
 *   String password = Config.get("valid.password");
 *
 * The config.properties file is located at:
 *   src/test/resources/config.properties
 *
 * WHY a config file instead of constants in the test class?
 *   - Credentials and URLs change between environments (test, staging, prod)
 *   - Updating one file is safer than hunting through Java source code
 *   - The .properties format is easy for non-developers to edit
 */
public class Config {

    /** Path to the properties file, relative to the Maven project root */
    private static final String CONFIG_FILE = "src/test/resources/config.properties";

    /** Loaded properties — static so the file is only read once per JVM run */
    private static final Properties props = new Properties();

    // Static initialiser — runs once when the class is first loaded
    static {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            props.load(input);
            System.out.println("[Config] Loaded configuration from: " + CONFIG_FILE);
        } catch (IOException e) {
            throw new RuntimeException(
                "[Config] FATAL: Cannot load config file '" + CONFIG_FILE + "'. " +
                "Please ensure the file exists and contains required properties.\n" +
                "Error: " + e.getMessage(), e
            );
        }
    }

    /**
     * Retrieve a configuration value by key.
     *
     * @param key  The property key (e.g. "valid.email")
     * @return     The property value as a String
     * @throws RuntimeException if the key is not found in the config file
     */
    public static String get(String key) {
        String value = props.getProperty(key);
        
        // Specific override for password to avoid plaintext commits
        if ("valid.password".equals(key)) {
            String envPass = System.getenv("FFC_PASSWORD");
            if (envPass != null && !envPass.trim().isEmpty()) {
                return envPass.trim();
            }
            String sysPass = System.getProperty("FFC_PASSWORD");
            if (sysPass != null && !sysPass.trim().isEmpty()) {
                return sysPass.trim();
            }
        }

        if (value == null || "YOUR_PASSWORD_HERE".equals(value)) {
            throw new RuntimeException(
                "[Config] Missing required property: '" + key + "'. " +
                "Please set it in " + CONFIG_FILE + " or provide FFC_PASSWORD environment variable."
            );
        }
        return value.trim();
    }

    /** Convenience accessor for the valid login email */
    public static String getValidEmail()    { return get("valid.email"); }

    /** Convenience accessor for the valid login password */
    public static String getValidPassword() { return get("valid.password"); }

    /** Convenience accessor for the login page URL */
    public static String getLoginUrl()      { return get("login.url"); }

    /** Convenience accessor for the attendance page URL */
    public static String getAttendanceUrl() { return get("attendance.url"); }

    // Private constructor — this is a utility class, not meant to be instantiated
    private Config() {}
}
