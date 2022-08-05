package com.RVR.apiGateway.apiGateway_RVR.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("api-gateway-rvr")
public class ApplicationPropertiesConfiguration {
    private String JWT_TOKEN_VALIDITY;
    private String secret;
    
	public String getJWT_TOKEN_VALIDITY() {
		return JWT_TOKEN_VALIDITY;
	}
	public void setJWT_TOKEN_VALIDITY(String jWT_TOKEN_VALIDITY) {
		JWT_TOKEN_VALIDITY = jWT_TOKEN_VALIDITY;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
    
    
}
