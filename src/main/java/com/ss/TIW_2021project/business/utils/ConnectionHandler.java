package com.ss.TIW_2021project.business.utils;

import com.ss.TIW_2021project.business.Exceptions.UtilityException;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ConnectionHandler {

    private static String DB_DRIVER;
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;

    public static void setup(ServletContext servletContext) throws UnavailableException {
        DB_DRIVER = servletContext.getInitParameter("dbDriver");
        DB_URL = servletContext.getInitParameter("dbUrl");
        DB_USER = servletContext.getInitParameter("dbUser");
        DB_PASSWORD = servletContext.getInitParameter("dbPassword");
    }

    public static Connection getConnection() throws UtilityException {

        Connection connection = null;

        try {

            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        } catch (SQLException | ClassNotFoundException e) {
            throw new UtilityException(UtilityException._ERROR_GETTING_CONN);
        }
        return connection;
    }

    public static void closeConnection(Connection connection) throws UtilityException {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new UtilityException(UtilityException._ERROR_CLOSING_CONN);
            }
        }
    }


    public static void closeQuietly(ResultSet rs) {

    }
}