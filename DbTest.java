import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbTest {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/udv-tesouraria";
        String username = "tesourario";
        String password = "masterkey";

        try {
            System.out.println("Attempting to connect to the database...");
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Successfully connected to the database!");
            connection.close();
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}