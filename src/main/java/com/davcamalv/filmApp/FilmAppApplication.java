package com.davcamalv.filmApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FilmAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilmAppApplication.class, args);
	}

}
