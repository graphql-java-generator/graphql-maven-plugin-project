/**
 * 
 */
package org.minimal_authorization_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * Not used </BR>
 * Thanks to <A HREF=
 * "https://www.baeldung.com/spring-boot-security-autoconfiguration">https://www.baeldung.com/spring-boot-security-autoconfiguration</A>
 * 
 * @author etienne-sf
 */
@SpringBootApplication // (exclude = { SecurityAutoConfiguration.class })
@EnableAuthorizationServer
public class SpringBootSecurityApplication {
	public static void main_NOTUSED(String[] args) {
		SpringApplication.run(SpringBootSecurityApplication.class, args);
	}
}
