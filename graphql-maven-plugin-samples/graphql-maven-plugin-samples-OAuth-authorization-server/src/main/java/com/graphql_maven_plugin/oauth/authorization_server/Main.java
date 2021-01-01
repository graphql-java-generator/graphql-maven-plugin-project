package com.graphql_maven_plugin.oauth.authorization_server;

import java.security.Principal;

import org.keycloak.services.managers.AuthenticationManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * To get the access token from the command line:
 * 
 * <PRE>
curl -X POST "http://localhost:8181/oauth/token" -i -d "grant_type=client_credentials" -u "theClientId:theClientSecret"
 * </PRE>
 * 
 * The response is, for instance:
 * 
 * <PRE>
{"access_token":"e47db1c1-b1d6-4c93-91d4-44c606bf25ee","token_type":"bearer","refresh_token":"dcaa4f20-a35d-429c-8d2c-2613541916a4","expires_in":4999,"scope":"read_profile_info"}
 * </PRE>
 * 
 * A GraphQL query could be:
 * 
 * <PRE>
curl -X POST http://localhost:8180/graphql -i -d "{\"query\":\"query{withoutParameters{name}}\",\"variables\":null,\"operationName\":null}" -H "authorization: Bearer aa9aa23f-7679-4513-b286-07ca583b2773" --noproxy "*"
 * </PRE>
 * 
 * 
 * @see https://aaronparecki.com/oauth-2-simplified/
 * @see https://howtodoinjava.com/spring-boot2/oauth2-auth-server/
 * @see https://www.javainuse.com/spring/springboot-oauth2-client-grant
 * @see https://www.javainuse.com/spring/springboot-oauth2-client-grant
 * @see https://www.baeldung.com/spring-security-oauth-resource-server
 * 
 *      Below: allows to use oAuth on the client side, with the Jersy client ?
 * @see https://www.baeldung.com/spring-security-social-login-jersey
 * 
 * @author etienne-sf
 */
@SpringBootApplication
@RestController
@EnableResourceServer
public class Main extends WebSecurityConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

	/**
	 * This method will be used to check if the user has a valid token to access the resource.
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping("/validateUser")
	public Principal user(Principal user) {
		return user;
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

}
