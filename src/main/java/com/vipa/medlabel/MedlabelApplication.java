package com.vipa.medlabel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.vipa.medlabel.repository.jpa")
@EnableMongoRepositories(basePackages = "com.vipa.medlabel.repository.mongo")
public class MedlabelApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedlabelApplication.class, args);
	}

}
