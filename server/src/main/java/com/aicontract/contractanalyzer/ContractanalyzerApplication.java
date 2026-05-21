package com.aicontract.contractanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ContractanalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContractanalyzerApplication.class, args);
	}

}
