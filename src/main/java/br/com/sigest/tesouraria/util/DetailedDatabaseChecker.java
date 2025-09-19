package br.com.sigest.tesouraria.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DetailedDatabaseChecker {
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

        // Test database connection and query some data
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            System.out.println("Connected to the PostgreSQL database successfully!");
            
            // Check distinct values of tipo column
            String distinctTiposSql = "SELECT DISTINCT tipo FROM movimentos";
            try (PreparedStatement pstmt = conn.prepareStatement(distinctTiposSql);
                 ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\nValores distintos na coluna 'tipo':");
                while (rs.next()) {
                    String tipo = rs.getString("tipo");
                    System.out.println("- " + tipo);
                }
            }
            
            // Count movements by tipo
            String countByTipoSql = "SELECT tipo, COUNT(*) as total FROM movimentos GROUP BY tipo";
            try (PreparedStatement pstmt = conn.prepareStatement(countByTipoSql);
                 ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\nContagem de movimentos por tipo:");
                while (rs.next()) {
                    String tipo = rs.getString("tipo");
                    int total = rs.getInt("total");
                    System.out.println("- " + tipo + ": " + total);
                }
            }
            
            // Check if there are any movements with negative values (potential SAIDAS)
            String countNegativeValuesSql = "SELECT COUNT(*) as total FROM movimentos WHERE valor < 0";
            try (PreparedStatement pstmt = conn.prepareStatement(countNegativeValuesSql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    System.out.println("\nMovimentos com valores negativos: " + total);
                }
            }
            
            // Show some sample movements with all columns
            String selectAllColumnsSql = "SELECT * FROM movimentos ORDER BY data_hora DESC LIMIT 5";
            try (PreparedStatement pstmt = conn.prepareStatement(selectAllColumnsSql);
                 ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\nAmostra de movimentos (todas as colunas):");
                int columnCount = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getMetaData().getColumnName(i) + "\t");
                }
                System.out.println();
                
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print("--------\t");
                }
                System.out.println();
                
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(rs.getString(i) + "\t");
                    }
                    System.out.println();
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error connecting to the database or executing queries: " + e.getMessage());
            e.printStackTrace();
        }
    }
}