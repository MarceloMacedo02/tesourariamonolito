import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DbTest {
    // Database connection parameters - adjust these according to your setup
    private static final String DB_URL = "jdbc:h2:file:./data/sigest";
    private static final String USER = "sa";
    private static final String PASS = "";

    public static void main(String[] args) {
        // Load H2 driver
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("H2 Driver not found: " + e.getMessage());
            return;
        }

        // Test database connection and query some data
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            System.out.println("Connected to the database successfully!");
            
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
                    java.math.BigDecimal valor = rs.getBigDecimal("valor");
                    LocalDateTime dataHora = rs.getObject("data_hora", LocalDateTime.class);
                    String origemDestino = rs.getString("origem_destino");
                    
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    String dataFormatada = dataHora.format(formatter);
                    
                    System.out.println(id + "\t" + tipo + "\t\t" + valor + "\t\t" + dataFormatada + "\t\t" + origemDestino);
                }
            }
            
            // Check if there are movements with the expected pattern for socio entries
            String countMovimentosSocioSql = "SELECT COUNT(*) AS total FROM movimentos WHERE origem_destino LIKE 'Recebimento Mensalidade Sócio:%'";
            try (PreparedStatement pstmt = conn.prepareStatement(countMovimentosSocioSql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int totalMovimentosSocio = rs.getInt("total");
                    System.out.println("\nTotal de movimentos com padrão de sócios: " + totalMovimentosSocio);
                }
            }
            
            // Show some sample movements with socio pattern
            String selectMovimentosSocioSql = "SELECT id, tipo, valor, data_hora, origem_destino FROM movimentos WHERE origem_destino LIKE 'Recebimento Mensalidade Sócio:%' ORDER BY data_hora DESC LIMIT 5";
            try (PreparedStatement pstmt = conn.prepareStatement(selectMovimentosSocioSql);
                 ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\nAmostra de movimentos de sócios:");
                System.out.println("ID\tTIPO\t\tVALOR\t\tDATA\t\t\tORIGEM/DESTINO");
                System.out.println("--\t----\t\t-----\t\t----\t\t\t--------------");
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String tipo = rs.getString("tipo");
                    java.math.BigDecimal valor = rs.getBigDecimal("valor");
                    LocalDateTime dataHora = rs.getObject("data_hora", LocalDateTime.class);
                    String origemDestino = rs.getString("origem_destino");
                    
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    String dataFormatada = dataHora.format(formatter);
                    
                    System.out.println(id + "\t" + tipo + "\t\t" + valor + "\t\t" + dataFormatada + "\t\t" + origemDestino);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error connecting to the database or executing queries: " + e.getMessage());
            e.printStackTrace();
        }
    }
}