package com.RVR.apiGateway.apiGateway_RVR.configurations;

import java.util.List;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import reactor.core.publisher.Mono;

@Component
public class JWTVerificationFilter implements GatewayFilter {
	
	@Autowired
	JWTUtility jwtUtility;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JWTVerificationFilter.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();

		final List<String> apiEndpoints = List.of("/register","/login");

		Predicate<ServerHttpRequest> isApiSecured = r -> apiEndpoints.stream()
				.noneMatch(uri -> r.getURI().getPath().contains(uri));

		if (isApiSecured.test(request)) {
			if (!request.getHeaders().containsKey("Authorization")) {
				ServerHttpResponse response = (ServerHttpResponse) exchange.getResponse();
				response.setStatusCode(HttpStatus.UNAUTHORIZED);

				return ((ReactiveHttpOutputMessage) response).setComplete();
			}

			final String token = request.getHeaders().getOrEmpty("Authorization").get(0);
			Claims claims = null;
			ServerHttpResponse response = null;
			try {
				claims = jwtUtility.verifyTokenAndSendClaims(token);
			} catch (SignatureException ex) {
				response = (ServerHttpResponse) exchange.getResponse();
				response.setStatusCode(HttpStatus.UNAUTHORIZED);
				return ((ReactiveHttpOutputMessage) response).setComplete();
			} catch (MalformedJwtException ex) {
				response = (ServerHttpResponse) exchange.getResponse();
				response.setStatusCode(HttpStatus.NOT_ACCEPTABLE);
				return ((ReactiveHttpOutputMessage) response).setComplete();
			} catch (ExpiredJwtException ex) {
				response = (ServerHttpResponse) exchange.getResponse();
				response.setStatusCode(HttpStatus.REQUEST_TIMEOUT);
				return ((ReactiveHttpOutputMessage) response).setComplete();
			} catch (UnsupportedJwtException ex) {
				response = (ServerHttpResponse) exchange.getResponse();
				response.setStatusCode(HttpStatus.NOT_ACCEPTABLE);
				return ((ReactiveHttpOutputMessage) response).setComplete();
			} catch (IllegalArgumentException ex) {
				response = (ServerHttpResponse) exchange.getResponse();
				response.setStatusCode(HttpStatus.NO_CONTENT);
				return ((ReactiveHttpOutputMessage) response).setComplete();
			} catch (Exception e) {
				response = (ServerHttpResponse) exchange.getResponse();
				response.setStatusCode(HttpStatus.BAD_GATEWAY);
				return ((ReactiveHttpOutputMessage) response).setComplete();	
			}
			exchange.getRequest().mutate().header("id", String.valueOf(claims.get("id"))).build();
		}

		return chain.filter(exchange);
	}

}