package com.ss.TIW_2021project.business.utils.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

public class ConnectionPoolImpl implements ConnectionPool {

    private String dbUrl;
    private String user;
    private String password;
    private String driver;
    private LinkedList<Connection> connectionPool;
    private LinkedList<Connection> usedConnections = new LinkedList<>();
    private static int INITIAL_POOL_SIZE = 5;
    private static int MAX_POOL_SIZE = 10;
    private static int MAX_TIMEOUT = 300;

    private ConnectionPoolImpl(String url, String user, String password, String driver, LinkedList<Connection> connectionPool) {
        this.dbUrl = url;
        this.user = user;
        this.password = password;
        this.driver = driver;
        this.connectionPool = connectionPool;
    }

    public static ConnectionPoolImpl create(String url, String user, String password, String driver) throws SQLException {

        LinkedList<Connection> pool = new LinkedList<>();

        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            pool.add(createConnection(url, user, password, driver));
        }

        return new ConnectionPoolImpl(url, user, password, driver, pool);
    }


    //return null if no connection is available
    @Override
    public synchronized Connection getConnection() throws SQLException {

        if (connectionPool.isEmpty()) {
            if (usedConnections.size() < MAX_POOL_SIZE) {
                connectionPool.add(createConnection(dbUrl, user, password, driver));
            } else {
                return null;
            }
        }

        Connection connection = connectionPool.remove(connectionPool.size() - 1);

        if(!connection.isValid(MAX_TIMEOUT)){
            connection = createConnection(dbUrl, user, password, driver);
        }

        usedConnections.add(connection);
        return connection;
    }

    @Override
    public synchronized boolean releaseConnection(Connection connection) {
        connectionPool.add(connection);
        return usedConnections.remove(connection);
    }

    private static Connection createConnection(String url, String user, String password, String driver) throws SQLException {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
        return DriverManager.getConnection(url, user, password);
    }

    public int getSize() {
        return connectionPool.size() + usedConnections.size();
    }

    public void shutdown() throws SQLException {
        usedConnections.forEach(this::releaseConnection);
        for (Connection c : connectionPool) {
            c.close();
        }
        connectionPool.clear();
    }

}