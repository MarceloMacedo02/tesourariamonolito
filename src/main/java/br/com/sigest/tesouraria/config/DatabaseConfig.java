package br.com.sigest.tesouraria.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@Profile("prod")
@EntityScan(basePackages = "br.com.sigest.tesouraria.model")
@EnableJpaRepositories(basePackages = "br.com.sigest.tesouraria.repository")
public class DatabaseConfig {
    
    // Configurações específicas do banco de dados para produção podem ser adicionadas aqui
    
}