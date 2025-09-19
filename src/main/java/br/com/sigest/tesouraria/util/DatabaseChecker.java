package br.com.sigest.tesouraria.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import br.com.sigest.tesouraria.domain.enums.TipoMovimento;

public class DatabaseChecker {
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
            
            // Check if there are any movements
            String countMovimentosSql = "SELECT COUNT(*) AS total FROM movimentos";
            try (PreparedStatement pstmt = conn.prepareStatement(countMovimentosSql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int totalMovimentos = rs.getInt("total");
                    System.out.println("Total de movimentos no banco de dados: " + totalMovimentos);
                }
            }
            
            // Check movements with ENTRADA type
            String countEntradasSql = "SELECT COUNT(*) AS total FROM movimentos WHERE tipo = 'ENTRADA'";
            try (PreparedStatement pstmt = conn.prepareStatement(countEntradasSql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int totalEntradas = rs.getInt("total");
                    System.out.println("Total de movimentos do tipo ENTRADA: " + totalEntradas);
                }
            }
            
            // Check movements with SAIDA type
            String countSaidasSql = "SELECT COUNT(*) AS total FROM movimentos WHERE tipo = 'SAIDA'";
            try (PreparedStatement pstmt = conn.prepareStatement(countSaidasSql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int totalSaidas = rs.getInt("total");
                    System.out.println("Total de movimentos do tipo SAIDA: " + totalSaidas);
                }
            }
            
            // Show some sample movements
            String selectMovimentosSql = "SELECT id, tipo, valor, data_hora, origem_destino FROM movimentos ORDER BY data_hora DESC LIMIT 10";
            try (PreparedStatement pstmt = conn.prepareStatement(selectMovimentosSql);
                 ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\nAmostra de movimentos:");
                System.out.println("ID\tTIPO\t\tVALOR\t\tDATA\t\t\tORIGEM/DESTINO");
                System.out.println("--\t----\t\t-----\t\t----\t\t\t--------------");
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String tipo = rs.getString("tipo");
                    BigDecimal valor = rs.getBigDecimal("valor");
                    LocalDateTime dataHora = rs.getObject("data_hora", LocalDateTime.class);
                    String origemDestino = rs.getString("origem_destino");
                    
                    System.out.println(id + "\t" + tipo + "\t\t" + valor + "\t\t" + dataHora + "\t\t" + origemDestino);
                }
            }
            
            // Test the sumByTipoAndPeriodo query for ENTRADA
            String sumEntradasSql = "SELECT SUM(valor) FROM movimentos WHERE tipo = 'ENTRADA' AND data_hora BETWEEN ? AND ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sumEntradasSql)) {
                LocalDateTime inicio = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
                LocalDateTime fim = LocalDateTime.of(2023, 1, 31, 23, 59, 59);
                pstmt.setObject(1, inicio);
                pstmt.setObject(2, fim);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        BigDecimal total = rs.getBigDecimal(1);
                        System.out.println("\nTotal de ENTRADAS em janeiro/2023: " + (total != null ? total : BigDecimal.ZERO));
                    }
                }
            }
            
            // Test the sumByTipoAndPeriodo query for SAIDA
            String sumSaidasSql = "SELECT SUM(valor) FROM movimentos WHERE tipo = 'SAIDA' AND data_hora BETWEEN ? AND ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sumSaidasSql)) {
                LocalDateTime inicio = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
                LocalDateTime fim = LocalDateTime.of(2023, 1, 31, 23, 59, 59);
                pstmt.setObject(1, inicio);
                pstmt.setObject(2, fim);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        BigDecimal total = rs.getBigDecimal(1);
                        System.out.println("Total de SAIDAS em janeiro/2023: " + (total != null ? total : BigDecimal.ZERO));
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error connecting to the database or executing queries: " + e.getMessage());
            e.printStackTrace();
        }
    }
}