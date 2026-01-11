package com.graphql_java_generator.oauth_authorization_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This samples is based on this <a href=
 * "https://github.com/spring-projects/spring-security-samples/tree/main/servlet/spring-boot/java/oauth2/authorization-server">spring
 * security authorization server sample</a>.<br/>
 * It can be tested by this command, that should return an access token:
 * 
 * <pre>
 * curl -v clientId:secret@localhost:8181/oauth2/token -d "grant_type=client_credentials" -d "scope=message:read"
 * </pre>
 * 
 * @See https://github.com/spring-projects/spring-authorization-server/tree/main/samples/default-authorizationserver
 */
@SpringBootApplication
public class AuthorizationServerMain {

	public static void main(String[] args) {
		SpringApplication.run(AuthorizationServerMain.class, args);
	}

}
