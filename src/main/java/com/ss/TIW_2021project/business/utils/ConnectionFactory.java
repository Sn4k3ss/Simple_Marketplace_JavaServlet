package com.ss.TIW_2021project.business.utils;

import com.ss.TIW_2021project.business.Exceptions.UtilityException;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionFactory {


    public static Connection getConnection(ServletContext servletContext) throws UtilityException {

        Connection connection = null;

        try {

            String URL = servletContext.getInitParameter("dbUrl");
            String USER = servletContext.getInitParameter("dbUser");
            String PASSWORD = servletContext.getInitParameter("dbPassword");
            String driver = servletContext.getInitParameter("dbDriver");
            Class.forName(driver);

            connection = DriverManager.getConnection(URL, USER, PASSWORD);

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


}