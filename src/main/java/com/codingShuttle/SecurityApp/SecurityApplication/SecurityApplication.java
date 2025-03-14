package com.codingShuttle.SecurityApp.SecurityApplication;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecurityApplication {

	static {
		Dotenv dotenv = Dotenv.configure()
				.directory("src/main/resources") // Specify the directory
				.filename("secrets.env")         // Load this file
				.load();;
		System.setProperty("GOOGLE_CLIENT_ID", dotenv.get("GOOGLE_CLIENT_ID"));
		System.setProperty("GOOGLE_CLIENT_SECRET", dotenv.get("GOOGLE_CLIENT_SECRET"));
	}
//hello
	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

}
