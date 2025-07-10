import DB.DBConnector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DBConnectorTest {
    private static String USER;
    private static String PASS;

    @BeforeEach
    void setup() {
        USER = "root";
        PASS = "Apple123!";
    }


    @Test
    public void testConnectionNotNull() {
        Connection conn = DBConnector.getConnection();
        assertNotNull(conn, "Connection should not be null");
        DBConnector.close(conn);  // Clean up
    }

    @Test
    public void testConnectionIsValid() throws Exception {
        try (Connection conn = DBConnector.getConnection()) {
            assertNotNull(conn, "Connection should not be null");
            assertTrue(conn.isValid(2), "Connection should be valid within 2 seconds");
        }
    }

    @Test
    void testConnectionWithIncorrectDatabaseName() {
        String incorrectDatabaseName = "incorrect_db";
        String url = "jdbc:mysql://localhost:3306/" + incorrectDatabaseName;

        assertThrows(SQLException.class, () -> {
            Connection conn = DriverManager.getConnection(url, USER, PASS);
            DBConnector.close(conn);
        });
    }







}
