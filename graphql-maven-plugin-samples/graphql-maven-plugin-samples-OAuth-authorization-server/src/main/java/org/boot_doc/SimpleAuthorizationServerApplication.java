/**
 * 
 */
package org.boot_doc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * <PRE>
curl clientid:clientpwd@localhost:8181/oauth/token -dgrant_type=client_credentials -dscope=any
 * </PRE>
 * 
 * @author etienne-sf
 * @see https://docs.spring.io/spring-security-oauth2-boot/docs/current/reference/html5/
 */
@EnableAuthorizationServer
@SpringBootApplication
public class SimpleAuthorizationServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleAuthorizationServerApplication.class, args);
	}

}
