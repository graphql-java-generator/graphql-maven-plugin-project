package org.forum.server.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration to configure OAuth2 only for the /graphql path, and to disable CSRF.
 * 
 * @author etienne-sf
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http//
			// Disabling CORS and CSRF makes POST on the graphql URL work properly. Double-check that before
			// entering in production
				.cors().and().csrf().disable()//
				.authorizeHttpRequests(//
						authorize -> authorize//
								.requestMatchers("/graphql") /* .hasAuthority("ROLE_CLIENT") */ .authenticated()//
								.anyRequest().anonymous()
				// Other paths are not protected
				)//
				.oauth2ResourceServer().jwt();
		return http.build();
	}

}