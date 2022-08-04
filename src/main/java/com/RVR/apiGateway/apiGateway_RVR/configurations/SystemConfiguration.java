package com.RVR.apiGateway.apiGateway_RVR.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemConfiguration {

	@Autowired
	JWTVerificationFilter filter;
	
	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes().route("limit-service", r -> r.path("/secure/**").filters(f -> f.filter(filter)).uri("http://localhost:8080")).build();
	}
	
}
