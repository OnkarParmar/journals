package com.teamteach.journals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude={SecurityAutoConfiguration.class})
public class JournalsApplication {

	public static void main(String[] args) {
		SpringApplication.run(JournalsApplication.class, args);
	}

}
