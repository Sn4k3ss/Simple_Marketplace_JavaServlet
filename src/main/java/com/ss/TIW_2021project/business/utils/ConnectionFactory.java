package com.ss.TIW_2021project.business.utils;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class ConnectionFactory {


    public static Connection getConnection(ServletContext servletContext) throws UnavailableException {

        Connection connection = null;

        try {

            String URL = servletContext.getInitParameter("dbUrl");
            String USER = servletContext.getInitParameter("dbUser");
            String PASSWORD = servletContext.getInitParameter("dbPassword");

            connection = DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (SQLException e) {
            throw new UnavailableException("Couldn't get db connection");
        }
        return connection;
    }

    public static void closeConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }


}