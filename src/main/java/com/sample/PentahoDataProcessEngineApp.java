

package com.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
//@EnableDiscoveryClient
public class PentahoDataProcessEngineApp {

	public static void main(String[] args) {
		// Look for data-service.yml or data-service.properties
		//System.setProperty("spring.config.name","data-server.properties");
		SpringApplication.run(PentahoDataProcessEngineApp.class, args);
	}
}
