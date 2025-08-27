package br.com.sigest.tesouraria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class TesourariaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TesourariaApplication.class, args);
	}

}