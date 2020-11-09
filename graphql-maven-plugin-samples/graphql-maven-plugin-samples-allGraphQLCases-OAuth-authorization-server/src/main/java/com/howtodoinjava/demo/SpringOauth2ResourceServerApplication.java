package com.howtodoinjava.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 
 * To get the access token from the command line:
 * 
 * <PRE>
curl -X POST "http://localhost:8080/oauth/token" -d "grant_type=password&username=humptydumpty&password=123456" -u "clientapp:clientpwd"
 * </PRE>
 * 
 * The response is, for instance:
 * 
 * <PRE>
{"access_token":"e47db1c1-b1d6-4c93-91d4-44c606bf25ee","token_type":"bearer","refresh_token":"dcaa4f20-a35d-429c-8d2c-2613541916a4","expires_in":4999,"scope":"read_profile_info"}
 * </PRE>
 * 
 * 
 * @see https://aaronparecki.com/oauth-2-simplified/
 * @see https://howtodoinjava.com/spring-boot2/oauth2-auth-server/
 * @see https://www.javainuse.com/spring/springboot-oauth2-password-grant
 * 
 * @author etienne-sf
 */
@SpringBootApplication
public class SpringOauth2ResourceServerApplication extends WebSecurityConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(SpringOauth2ResourceServerApplication.class, args);
	}

}
