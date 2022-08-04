package com.RVR.apiGateway.apiGateway_RVR.configurations;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

@Component
public class JWTUtility {
	
	@Autowired
	ApplicationPropertiesConfiguration applicationPropertiesConfiguration;

	
	public Claims verifyTokenAndSendClaims(String token) throws Exception {
		return Jwts.parser()
				   .setSigningKey(applicationPropertiesConfiguration.getSecret())
				   .parseClaimsJws(token)
				   .getBody();
	}
	
	public String getUsernameFromToken(String token) throws Exception {
		return getClaimFromToken(token, Claims::getSubject);
	}
	
	public Date getExpirationDateFromToken(String token) throws Exception {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws Exception {
		final Claims claims = verifyTokenAndSendClaims(token);
		return claimsResolver.apply(claims);
	}
	
	public String generateToken(String userDetails) {
		Map<String, Object> claims = new HashMap<String,Object>();
		return doGenerateToken(claims, userDetails);
	}
	
	public Boolean isTokenExpired(String token) throws Exception {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}
	
	private String doGenerateToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + applicationPropertiesConfiguration.getJWT_TOKEN_VALIDITY() * 1000))
				.signWith(SignatureAlgorithm.HS512, applicationPropertiesConfiguration.getSecret()).compact();
	}
	
	
	
	
}
