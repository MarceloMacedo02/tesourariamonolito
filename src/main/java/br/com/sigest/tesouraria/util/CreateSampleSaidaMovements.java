package br.com.sigest.tesouraria.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class CreateSampleSaidaMovements {
    // Database connection parameters - adjust these according to your setup
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/udv-tesouraria";
    private static final String USER = "tesourario";
    private static final String PASS = "masterkey";

    public static void main(String[] args) {
        // Load PostgreSQL driver
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL Driver not found: " + e.getMessage());
            return;
        }

        // Test database connection and insert sample data
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            System.out.println("Connected to the PostgreSQL database successfully!");
            
            // Insert sample SAIDA movements
            String insertSql = "INSERT INTO movimentos (tipo, valor, data_hora, origem_destino, conta_id, rubrica_id, centro_custo_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            // Sample SAIDA movements
            String[][] sampleData = {
                {"SAIDA", "1500.00", "2023-01-15 10:30:00", "Pagamento de aluguel", "1", "11", "5"},
                {"SAIDA", "800.50", "2023-01-20 14:15:00", "Compra de materiais", "1", "13", "5"},
                {"SAIDA", "300.75", "2023-02-05 09:45:00", "Pagamento de energia elétrica", "1", "13", "5"},
                {"SAIDA", "1200.00", "2023-02-18 11:20:00", "Manutenção predial", "1", "11", "5"},
                {"SAIDA", "500.25", "2023-03-10 13:30:00", "Pagamento de impostos", "1", "12", "5"},
                {"SAIDA", "2000.00", "2023-03-22 16:45:00", "Reforma da cozinha", "1", "14", "5"}
            };
            
            int count = 0;
            for (String[] data : sampleData) {
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, data[0]); // tipo
                    pstmt.setBigDecimal(2, new BigDecimal(data[1])); // valor
                    pstmt.setObject(3, LocalDateTime.parse(data[2].replace(" ", "T"))); // data_hora
                    pstmt.setString(4, data[3]); // origem_destino
                    pstmt.setLong(5, Long.parseLong(data[4])); // conta_id
                    pstmt.setLong(6, Long.parseLong(data[5])); // rubrica_id
                    pstmt.setLong(7, Long.parseLong(data[6])); // centro_custo_id
                    
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        count++;
                        System.out.println("Inserted movement: " + data[3]);
                    }
                }
            }
            
            System.out.println("\nTotal de movimentos de saída inseridos: " + count);
            
        } catch (SQLException e) {
            System.err.println("Error connecting to the database or executing queries: " + e.getMessage());
            e.printStackTrace();
        }
    }
}