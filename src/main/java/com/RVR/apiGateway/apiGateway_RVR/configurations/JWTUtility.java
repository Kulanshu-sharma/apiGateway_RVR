package com.RVR.apiGateway.apiGateway_RVR.configurations;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTUtility {
	
	@Autowired
	ApplicationPropertiesConfiguration applicationPropertiesConfiguration;

	private String doGenerateToken(Map<String, Object> claims, String subject) {

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + applicationPropertiesConfiguration.getJWT_TOKEN_VALIDITY() * 1000))
				.signWith(SignatureAlgorithm.HS512, applicationPropertiesConfiguration.getSecret()).compact();
	}
}
