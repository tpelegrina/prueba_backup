package com.example.prueba_backups;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.ZoneId;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class PruebaBackupsApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("America/Argentina/Buenos_Aires")));
		SpringApplication.run(PruebaBackupsApplication.class, args);
	}

}
