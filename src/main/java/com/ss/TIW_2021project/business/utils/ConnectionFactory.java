package com.ss.TIW_2021project.business.utils;

import javax.servlet.UnavailableException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class ConnectionFactory {


    public static Connection getConnection() throws UnavailableException {

        Connection connection = null;

        try {
            //read from "db.properties" file
            InputStream inputStream = ClassLoader.getSystemResourceAsStream("db.properties");
            Properties properties = new Properties();
            properties.load(inputStream);

            String URL = properties.getProperty("dbUrl");
            String USER = properties.getProperty("dbUser");
            String PASSWORD = properties.getProperty("dbPassword");

            connection = DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (SQLException e) {
            throw new UnavailableException("Couldn't get db connection");
        } catch (IOException e) {
            throw new UnavailableException("Can't access database");
            //error while getting credentials "from db.properties" file
        }
        return connection;
    }

    public static void closeConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }


}