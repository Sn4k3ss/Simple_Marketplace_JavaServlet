package com.ss.TIW_2021project.business.utils;

import com.ss.TIW_2021project.business.Exceptions.UtilityException;
import com.ss.TIW_2021project.business.utils.connection.ConnectionPool;
import com.ss.TIW_2021project.business.utils.connection.ConnectionPoolImpl;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.sql.*;


/**
 * An utility class which setup the connection's parameters.
 */
public class ConnectionHandler {

    private static String DB_DRIVER;
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;

    private static ConnectionPool connectionPool;

    private ConnectionHandler() {

    }


    /**
     * Sets up the connection's params
     *
     * @param servletContext the ctx from which to get parameters
     * @throws UnavailableException if params are missing or mis-configured
     */
    public static void setupConnectionPool(ServletContext servletContext) throws UtilityException {

        DB_DRIVER = servletContext.getInitParameter("dbDriver");
        DB_URL = servletContext.getInitParameter("dbUrl");
        DB_USER = servletContext.getInitParameter("dbUser");
        DB_PASSWORD = servletContext.getInitParameter("dbPassword");

        try {
            connectionPool = ConnectionPoolImpl.create(DB_URL, DB_USER, DB_PASSWORD, DB_DRIVER);
        } catch (SQLException e) {
            throw new UtilityException(UtilityException._ERROR_CREATING_CONN_POOL);
        }
    }


    public static Connection getConnectionFromPool() throws UtilityException {
        Connection conn = null;

        if (connectionPool == null) {
            throw new UtilityException(UtilityException._CONNECTION_POOL_IS_NULL);
        }

        try {
            conn = connectionPool.getConnection();
        } catch (SQLException throwables) {
            throw new UtilityException(UtilityException._ERROR_GETTING_CONN);
        }

        if (conn != null)
            return conn;
        else throw new UtilityException(UtilityException._CONNECTIONS_LIMIT_REACHED);
    }

    public static void releaseConnectionToPool(Connection connection) {
        connectionPool.releaseConnection(connection);
    }

    public static void closeQuietly(ResultSet rs) throws UtilityException {
        try {
            if(rs != null)
                rs.close();
        }catch (Exception e) {
            throw new UtilityException(UtilityException._ERROR_CLOSING_RESULT_SET);
        }
    }

    public static void closeQuietly(Statement stm) throws UtilityException {
        try {
            if(stm != null)
                stm.close();
        }catch (Exception e) {
            throw new UtilityException(UtilityException._ERROR_CLOSING_RESULT_SET);
        }
    }

}