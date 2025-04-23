package com.employee;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ApplicationScoped
public class DatabaseConfig {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
    private final Properties properties = new Properties();
    
    public DatabaseConfig() {
        loadProperties();
    }
    
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                LOGGER.log(Level.SEVERE, "Unable to find database.properties");
                return;
            }
            properties.load(input);
            LOGGER.log(Level.INFO, "Database properties loaded successfully");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading database properties", e);
        }
    }
    
    public String getUrl() {
        return properties.getProperty("db.url");
    }
    
    public String getUsername() {
        return properties.getProperty("db.username");
    }
    
    public String getPassword() {
        return properties.getProperty("db.password");
    }
    
    public String getDriver() {
        return properties.getProperty("db.driver");
    }
}