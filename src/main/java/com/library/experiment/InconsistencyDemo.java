package com.library.experiment;

import com.library.util.DatabaseUtils;

import java.sql.*;

public class InconsistencyDemo {
    public static void main(String[] args) {
        System.out.println("Running without Transaction");
        runWithoutTransaction();

        System.out.println("Running with Transaction");
        runWithTransaction();
    }

    public static void runWithoutTransaction() {
        try (Connection connection = DatabaseUtils.getConnection()) {
            connection.setAutoCommit(true);

            // 1. Insert book (should succeed)
            DatabaseUtils.execute("insert into book (title, section_id) values (?, ?)", "Broken Book", 1);

            Integer book_id = DatabaseUtils.findOne("select book_id from book where title = ? order by book_id desc limit 1",
                    rs -> {
                        try {
                            return rs.getInt("book_id");
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    "Broken Book"
            );

            if (book_id == null) {
                System.out.println("Book insert failed");
                return;
            }

            // 2. Insert author relation with invalid ID (should fail)
            DatabaseUtils.execute("insert into book_author (book_id, author_id) values (?, ?)",
                    book_id, 999);
        } catch (SQLException e) {
            System.out.println("Error occured: " + e.getMessage());
        }
    }

    public static void runWithTransaction() {
        try (Connection connection = DatabaseUtils.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement bookPreparedStatement = connection.prepareStatement(
                    "insert into book (title, section_id) values (?, ?)", Statement.RETURN_GENERATED_KEYS)
            ) {
                bookPreparedStatement.setString(1, "Consistent Book");
                bookPreparedStatement.setInt(2, 999);
                bookPreparedStatement.executeUpdate();

                ResultSet keys = bookPreparedStatement.getGeneratedKeys();
                while (keys.next()) {
                    int book_id = keys.getInt(1);

                    try (PreparedStatement authorPreparedStatement = connection.prepareStatement(
                            "insert into book_author(book_id, author_id) values (?, ?)")) {
                        authorPreparedStatement.setInt(1, book_id);
                        authorPreparedStatement.setInt(2, 999);
                        authorPreparedStatement.executeUpdate();
                    }
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Transaction rolled back due to: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }
}
