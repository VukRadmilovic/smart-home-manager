package com.ftn.uns.ac.rs.smarthomesockets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class SmartSocketsApplication {
	public static void main(String[] args) {
		SpringApplication.run(SmartSocketsApplication.class, args);
	}

}
