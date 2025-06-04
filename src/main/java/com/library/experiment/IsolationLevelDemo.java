package com.library.experiment;

import com.library.util.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IsolationLevelDemo {
    public static void main(String[] args) throws Exception {
        IsolationLevelDemo.runWithIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);
        IsolationLevelDemo.runWithIsolationLevel(Connection.TRANSACTION_REPEATABLE_READ);
    }

    public static void runWithIsolationLevel(int isolationLevel) throws Exception {
        // Make sure book_id = 2 is available
        DatabaseUtils.execute("delete from borrowing where book_id = ? and return_date is null", 2);

        try (Connection connectionA = DatabaseUtils.getConnection()) {
            connectionA.setTransactionIsolation(isolationLevel);
            connectionA.setAutoCommit(false);

            System.out.println();
            System.out.println(STR."Running with isolation level \{getIsolationName(isolationLevel)}");

            // First read
            int countBefore = getAvailableBookCount(connectionA);
            System.out.println(STR."User A (before borrow): available books = \{countBefore}");

            // Trigger B
            Thread threadB = new Thread(IsolationLevelDemo::runTransactionB);
            threadB.start();
            threadB.join();

            // Second read (within the same transaction)
            int countAfter = getAvailableBookCount(connectionA);
            System.out.println(STR."User A (after borrow): available books = \{countAfter}");
            System.out.println(STR."Availability changed in same transaction? \{countBefore != countAfter}");

            connectionA.commit();
        }
    }

    private static int getAvailableBookCount(Connection connection) {
        String sql = "select count(*) from book where book_id not in (select book_id from borrowing where return_date is null)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Count failed", e);
        }
    }

    private static String getIsolationName(int level) {
        return switch (level) {
            case Connection.TRANSACTION_READ_COMMITTED -> "READ COMMITTED";
            case Connection.TRANSACTION_REPEATABLE_READ -> "REPEATABLE READ";
            default -> "UNKNOWN";
        };
    }

    private static void runTransactionB() {
        try (Connection connectionB = DatabaseUtils.getConnection()) {
            connectionB.setAutoCommit(false);
            DatabaseUtils.executeWithConnection(connectionB,
                    "insert into borrowing (user_id, book_id, borrow_date) values (?, ?, current_timestamp)",
                    1, 2);
            connectionB.commit();
            System.out.println("User B borrowed book_id = 2");
        } catch (SQLException e) {
            throw new RuntimeException("Transaction B failed", e);
        }
    }
}
