package com.example.prueba_backups;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PruebaBackupsApplication {

	public static void main(String[] args) {
		System.out.println("🔧 DB URL: " + System.getenv("SPRING_DATASOURCE_URL"));
		SpringApplication.run(PruebaBackupsApplication.class, args);
	}

}
