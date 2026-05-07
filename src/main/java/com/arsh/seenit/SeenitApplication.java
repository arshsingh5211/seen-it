package com.arsh.seenit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SeenitApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeenitApplication.class, args);
	}

}
