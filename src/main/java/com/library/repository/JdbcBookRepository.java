package com.library.repository;

import com.library.model.Book;
import com.library.util.DatabaseUtils;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository("jdbcBookRepository")
public class JdbcBookRepository implements BookRepository {
    private Book map(ResultSet resultSet) {
        try {
            return new Book(
                    String.valueOf(resultSet.getInt("book_id")),
                    resultSet.getString("title"),
                    "",
                    ""
            );
        } catch (SQLException e) {
            throw new RuntimeException("Failed to map ResultSet to Book", e);
        }
    }

    @Override
    public void save(Book book) throws SQLException {
        DatabaseUtils.execute("insert into book (title, section_id) values (?, ?)",
                book.getTitle(), 1
        );
    }

    @Override
    public void update(Book book) throws SQLException {
        DatabaseUtils.execute("update book set title = ? where book_id = ?",
                book.getTitle(), Integer.parseInt(book.getBookID())
        );
    }

    @Override
    public void delete(String bookID) throws SQLException {
        DatabaseUtils.execute("delete from book where book_id = ?",
                Integer.parseInt(bookID)
        );
    }

    @Override
    public Book findByID(String bookID) {
        return DatabaseUtils.findOne(
                "select * from book where book_id = ?",
                resultSet -> this.map(resultSet),
                Integer.parseInt(bookID)
        );
    }

    @Override
    public Book findByTitle(String title) {
        List<Book> results = DatabaseUtils.findMany(
                "select * from book where title = ?",
                resultSet -> this.map(resultSet),
                title
        );
        if (results.isEmpty()) return null;
        return results.get(0);
    }

    @Override
    public List<Book> findAll() {
        return DatabaseUtils.findMany(
                "select * from book",
                resultSet -> this.map(resultSet)
        );
    }

    @Override
    public List<Book> findByGenre(String genre) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
