package br.com.sigest.tesouraria.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@Profile("prod")
@EnableScheduling
@EnableCaching
public class ProductionConfig {
    
    // Configurações específicas para o ambiente de produção podem ser adicionadas aqui
    
}