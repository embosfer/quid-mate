package com.embosfer.quidmate.integration;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by embosfer on 27/07/2017.
 */
public class DbTestSupport {

    private static Connection connection;
    protected static DSLContext execute;

    @BeforeClass
    public static void setUp() throws SQLException {
        startDB();
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        stopDB();
    }

    private static void startDB() throws SQLException {
        String userName = "root";
        String password = "xxxxx";
        String url = "jdbc:mysql://localhost:3306/QuidMate";
        connection = DriverManager.getConnection(url, userName, password);
        execute = DSL.using(connection);
    }

    private static void stopDB() throws SQLException {
        connection.close();
        execute.close();
    }
}
