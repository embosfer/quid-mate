package com.embosfer.quidmate.core;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by embosfer on 07/08/2017.
 */
public class DbConfig {

    public final String url;
    public final String user;
    public final String password;

    public DbConfig() {

        try {
            InputStream input = getClass().getResourceAsStream("/dbconfig.properties");

            Properties properties = new Properties();
            properties.load(input);

            url = properties.getProperty("db.url");
            user = properties.getProperty("db.user");
            password = properties.getProperty("db.password");

        } catch (Exception e) {
            throw new RuntimeException("Failed to read the DB connection details", e);
        }
    }

}
