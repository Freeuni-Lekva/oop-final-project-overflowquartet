import DB.DBConnector;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class DBConnectorTest {

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
}
