package br.com.sigest.tesouraria.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseFixRunner implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // Verifica se a sequência existe
        Boolean sequenceExists = jdbcTemplate.queryForObject(
            "SELECT EXISTS (SELECT 1 FROM information_schema.sequences WHERE sequence_name = 'movimentos_id_seq')", 
            Boolean.class);
        
        if (!sequenceExists) {
            // Cria a sequência se não existir
            jdbcTemplate.execute("CREATE SEQUENCE movimentos_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1");
        }
        
        // Verifica se a coluna id tem default
        String defaultValue = jdbcTemplate.queryForObject(
            "SELECT column_default FROM information_schema.columns WHERE table_name = 'movimentos' AND column_name = 'id'", 
            String.class);
        
        if (defaultValue == null || !defaultValue.contains("nextval")) {
            // Define o valor padrão para auto-incremento
            jdbcTemplate.execute("ALTER TABLE movimentos ALTER COLUMN id SET DEFAULT nextval('movimentos_id_seq')");
            jdbcTemplate.execute("ALTER SEQUENCE movimentos_id_seq OWNED BY movimentos.id");
        }
        
        // Atualiza a sequência para começar após o maior ID existente
        try {
            jdbcTemplate.execute("SELECT setval('movimentos_id_seq', COALESCE((SELECT MAX(id) FROM movimentos), 0) + 1)");
        } catch (Exception e) {
            // Se houver erro, apenas continua
            System.out.println("Não foi possível atualizar a sequência: " + e.getMessage());
        }
        
        System.out.println("Correção do problema de auto-incremento da tabela movimentos concluída.");
    }
}