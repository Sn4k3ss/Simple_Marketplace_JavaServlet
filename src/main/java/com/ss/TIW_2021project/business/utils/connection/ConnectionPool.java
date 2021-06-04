package com.ss.TIW_2021project.business.utils.connection;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionPool {

    Connection getConnection() throws SQLException;

    boolean releaseConnection(Connection connection);

    public void shutdown() throws SQLException;

}
