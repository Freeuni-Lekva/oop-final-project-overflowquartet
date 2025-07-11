package DB;


import java.sql.*;

public class DBConnector {
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver registered");
        } catch (ClassNotFoundException e) {
            System.err.println("Could not register MySQL driver");
            e.printStackTrace();
        }
    }


    // Update these for your own setup
    private static final String DATABASE_NAME = "quiz_db";
    private static final String USER = "root";
    private static final String PASS = "admin123";

    private static final String URL =
            "jdbc:mysql://localhost:3306/" + DATABASE_NAME;

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.err.println("Database connection failed");
            e.printStackTrace();
            return null;
        }
    }

    public static void close(Connection c) {
        if (c != null) {
            try { c.close(); }
            catch (SQLException ignored) {}
        }
    }


}
