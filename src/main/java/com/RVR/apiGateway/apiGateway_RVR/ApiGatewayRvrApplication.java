package com.RVR.apiGateway.apiGateway_RVR;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayRvrApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayRvrApplication.class, args);
	}

}
