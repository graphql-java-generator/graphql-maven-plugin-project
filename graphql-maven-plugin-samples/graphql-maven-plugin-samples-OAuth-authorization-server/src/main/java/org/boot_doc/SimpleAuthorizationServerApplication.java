/**
 * 
 */
package org.boot_doc;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * <PRE>
curl clientid:clientpwd@localhost:8181/oauth/token -dgrant_type=client_credentials -dscope=any
 * </PRE>
 * 
 * @author etienne-sf
 * @see https://aaronparecki.com/oauth-2-simplified
 * @see https://docs.spring.io/spring-security-oauth2-boot/docs/current/reference/html5/
 */
// @EnableAuthorizationServer
@SpringBootApplication
public class SimpleAuthorizationServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleAuthorizationServerApplication.class, args);
	}

	@Bean
	public KeyPair keyPairBean() throws NoSuchAlgorithmException {
		KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
		gen.initialize(2048);
		KeyPair keyPair = gen.generateKeyPair();
		return keyPair;
	}

}
