import com.library.util.DatabaseUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseUtilsTest {
    private static final String URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String USER = "newUser";
    private static final String PASSWORD = "newPassword";

    @BeforeAll
    static void setupDatabase() throws Exception {
        String initSQL = Files.readString(Paths.get("/Users/mvarosyan/IdeaProjects/Digital_Library/src/test/resources/db/init-h2.sql"));
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            for (String statement : initSQL.split(";")) {
                if (!statement.trim().isEmpty()) {
                    connection.createStatement().execute(statement);
                }
            }
        }
        DatabaseUtils.overrideConnectionForTest(URL, USER, PASSWORD);
    }

    @Test
    void testFindBookByTitle() {
        String title = DatabaseUtils.findOne("select * from book where title = ?", rs -> {
            try {
                return rs.getString("title");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, "1984");

        assertNotNull(title);
        assertEquals("1984", title);
    }

    @Test
    void testInsertAndFindBook() throws SQLException {
        DatabaseUtils.execute("insert into book (title, section_id) values (?, ?)", "New Book", 1);

        String result = DatabaseUtils.findOne("select title from book where title = ?", rs -> {
            try {
                return rs.getString("title");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, "New Book");

        assertEquals("New Book", result);
    }

    @Test
    void testInsertInvalidBookFails() {
        Exception ex = assertThrows(RuntimeException.class, () -> {
            DatabaseUtils.execute("insert into book (title, section_id) values (?, ?)", "Invalid Book", 999);
        });

        assertTrue(ex.getMessage().contains("Execution failed"));
    }
}
