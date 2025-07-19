package com.example.prueba_backups;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PruebaBackupsApplication {

	public static void main(String[] args) {
		// Comentario de prueba
		SpringApplication.run(PruebaBackupsApplication.class, args);
	}

}
