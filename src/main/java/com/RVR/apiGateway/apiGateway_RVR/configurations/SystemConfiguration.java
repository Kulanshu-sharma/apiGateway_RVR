package com.RVR.apiGateway.apiGateway_RVR.configurations;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.CrossOrigin;

import reactor.core.publisher.Mono;

@Configuration
@CrossOrigin(origins="*")
public class SystemConfiguration {

	@Autowired
	JWTVerificationFilter filter;
	@Autowired
	JWTUtility jwtUtility;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JWTVerificationFilter.class);
	
	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes().route("client-service", r -> r.path("/client/**").filters(f -> f.filter(filter)).uri("lb://CLIENT-SERVICE"))
				               .route("api-cluster-node", r -> r.path("/apiCluster/**").filters(f -> f.filter(filter)).uri("lb://API-CLUSTER-NODE"))
				               .route("user-service", r -> r.path("/user/**").filters(f -> f.filter(filter)).uri("lb://USER-SERVICE"))
				               .build();
	}
	
	@Bean
	@Order(-1)
	public GlobalFilter postFilter() {
		return (exchange, chain) -> {
			System.out.println("Used for License Key in Future");
			return chain.filter(exchange).then(Mono.fromRunnable(() -> { 
				List<String> claims = exchange.getResponse().getHeaders().get("userData");
				if(claims==null) {
					LOGGER.error("No Claims/Payload data is coming from the inner microservice to Api Gateway Post Filter");
					//exchange.getResponse().setStatusCode(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
				}
				else {
					Map<String, Object> data = (new BasicJsonParser()).parseMap(claims.get(0));
					data.put("tokenId",UUID.randomUUID().toString());
					String token = jwtUtility.doGenerateToken(data,"Guest");
					exchange.getResponse().getHeaders().set(HttpHeaders.AUTHORIZATION,token);
					exchange.getResponse().getHeaders().set("Access-Control-Allow-Origin","*");
					exchange.getResponse().getHeaders().set("Access-Control-Expose-Headers",HttpHeaders.AUTHORIZATION);
				}
			}));
		};
	}

}
