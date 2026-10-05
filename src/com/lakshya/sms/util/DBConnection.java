package com.lakshya.sms.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Creates database connections using the details in db.properties. */
public final class DBConnection {

    private static final String CONFIG_FILE = "db.properties";
    private static final Properties CONFIG = new Properties();

    private DBConnection() { }

    static {
        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            CONFIG.load(in);
        } catch (IOException e) {
            System.out.println("Could not read '" + CONFIG_FILE + "' in the current folder.");
            System.out.println("Copy db.properties.example to db.properties and fill in your MySQL details.");
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = CONFIG.getProperty("url");
        if (url == null) {
            throw new SQLException("Database settings missing. Check db.properties.");
        }
        return DriverManager.getConnection(url, CONFIG.getProperty("user"), CONFIG.getProperty("password"));
    }
}
