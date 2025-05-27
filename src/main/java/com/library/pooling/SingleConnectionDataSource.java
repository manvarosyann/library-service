package com.library.pooling;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class SingleConnectionDataSource implements DataSource {
    private final Connection connection;

    public SingleConnectionDataSource(String url, String user, String password) {
        try {
            this.connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create connection", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) {
        return connection;
    }

    @Override
    public PrintWriter getLogWriter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLoginTimeout(int seconds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLoginTimeout() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }


    @Override
    public <T> T unwrap(Class<T> iface) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        throw new UnsupportedOperationException();
    }
}
