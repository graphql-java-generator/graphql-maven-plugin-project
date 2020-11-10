package org.allGraphQLCases.oauth2_authorization_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * Currently unused.
 * 
 * @see https://dzone.com/articles/build-an-oauth-20-authorization-server-with-spring
 */
@SpringBootApplication
@EnableResourceServer
public class AuthorizationServerApplication {

	public static void main_NOTUSED(String[] args) {
		SpringApplication.run(AuthorizationServerApplication.class, args);
	}

}