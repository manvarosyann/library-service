package com.library.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class DatabaseUtils {
    private static final String URL = "jdbc:postgresql://localhost:5432/librarydb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    private static String testURL = null;
    private static String testUser = null;
    private static String testPassword = null;

    public static void overrideConnectionForTest(String url, String user, String password) {
        testURL = url;
        testUser = user;
        testPassword = password;
    }

    private static Connection getConnection() throws SQLException {
        if (testURL != null) {
            return DriverManager.getConnection(testURL, testUser, testPassword);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Method a
    public static void execute(String query, Object... args) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Execution failed: " + query, e);
        }
    }

    // Method b
    public static void execute(String query, Consumer<PreparedStatement> statementConsumer) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            statementConsumer.accept(ps);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Execution failed: " + query, e);
        }
    }

    // Method d
    public static <T> T findOne(String query, Function<ResultSet, T> mapper, Object... args) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                T result = mapper.apply(rs);
                if (rs.next()) {
                    throw new IllegalStateException("Found more than one row");
                }
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Execution failed: " + query, e);
        }
    }

    // Method e
    public static <T> List<T> findMany(String query, Function<ResultSet, T> mapper, Object... args) {
        List<T> results = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapper.apply(rs));
                }
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Execution failed: " + query, e);
        }
    }
}