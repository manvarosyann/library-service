package dbtests;

import java.sql.*;

public class SQLInjectionTest {
    private static final String URL = "jdbc:postgresql://localhost:5432/librarydb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    public static void main(String[] args) throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

        String injectedUsername = "admin' --";
        String fakePassword = "anything";

        // Unsafe login using Statement
        System.out.println("Attempting SQL Injection with Statement!");
        Statement statement = connection.createStatement();
        String unsafeQuery = "select * from login_demo where username = '" + injectedUsername + "' and password = '" + fakePassword + "'";
        System.out.println("Executing: " + unsafeQuery);

        ResultSet resultSet1 = statement.executeQuery(unsafeQuery);
        if (resultSet1.next()) {
            System.out.println("Logged in (Statement) — injection worked!");
        } else {
            System.out.println("Login failed (Statement)");
        }

        // Safe login using PreparedStatement
        System.out.println("\nAttempting safe login with PreparedStatement!");
        PreparedStatement preparedStatement = connection.prepareStatement(
                "select * from login_demo where username = ? and password = ?");
        preparedStatement.setString(1, injectedUsername);
        preparedStatement.setString(2, fakePassword);

        ResultSet resultSet2 = preparedStatement.executeQuery();
        if (resultSet2.next()) {
            System.out.println("Logged in (PreparedStatement)");
        } else {
            System.out.println("Login failed (PreparedStatement) — injection prevented!");
        }
        connection.close();
    }
}
