package com.marshmellow.bolbolestan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BolbolestanApplication {

	public static void main(String[] args) {
		System.setProperty("server.port", "8080");
		SpringApplication.run(BolbolestanApplication.class, args);
	}

}
