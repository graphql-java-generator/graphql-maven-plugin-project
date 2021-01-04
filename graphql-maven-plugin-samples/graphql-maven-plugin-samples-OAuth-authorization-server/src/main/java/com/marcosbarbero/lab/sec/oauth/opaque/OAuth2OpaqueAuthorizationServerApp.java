package com.marcosbarbero.lab.sec.oauth.opaque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * A sample query, to get an OAuth token:
 * 
 * <pre>
curl -u "clientId:secret" -X POST "http://localhost:8181/oauth/token?grant_type=password&username=user&password=pass" --noproxy "*" -i
 * </pre>
 * 
 * Then, reuse the previous token in the next query:
 * 
 * <pre>
curl -i -X POST "http://localhost:8180/graphql" -H "Authorization: Bearer 8c8e4a5b-d903-4ed6-9738-6f7f364b87ec" --noproxy "*"
 * </pre>
 * 
 * @author Marcos Barbero
 * @see https://blog.marcosbarbero.com/
 * @see https://blog.marcosbarbero.com/oauth2-centralized-authorization-opaque-jdbc-spring-boot2/
 */
@SpringBootApplication
public class OAuth2OpaqueAuthorizationServerApp {

	public static void main(String... args) {
		SpringApplication.run(OAuth2OpaqueAuthorizationServerApp.class, args);
	}

}
