package com.mackena.Banking_Application_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
public class BankingApplicationBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingApplicationBackendApplication.class, args);
	}

}
