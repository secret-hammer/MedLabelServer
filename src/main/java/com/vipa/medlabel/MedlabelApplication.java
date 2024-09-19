package com.vipa.medlabel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class MedlabelApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedlabelApplication.class, args);
	}

}
