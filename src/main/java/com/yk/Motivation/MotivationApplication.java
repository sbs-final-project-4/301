package com.yk.Motivation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MotivationApplication {

	public static void main(String[] args) {
		SpringApplication.run(MotivationApplication.class, args);
	}

}
