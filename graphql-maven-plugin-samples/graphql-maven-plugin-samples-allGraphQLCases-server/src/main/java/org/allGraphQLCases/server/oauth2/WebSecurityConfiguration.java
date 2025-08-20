package org.allGraphQLCases.server.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 
 * @author etienne-sf
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfiguration {

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_"); // Prefix for the roles
		grantedAuthoritiesConverter.setAuthoritiesClaimName("roles"); // Name of the claim in the JWT

		JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
		jwtConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
		return jwtConverter;
	}

	@Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http//
			// Disabling CORS and CSRF makes POST on the graphql URL work properly. Double-check that before
			// entering in production
				.csrf(csrf -> csrf.disable())//
				.cors(cors -> cors.disable())//
				.authorizeHttpRequests(auth -> auth//
						.requestMatchers("/my/updated/graphql/path").authenticated()//
						// .hasAuthority("ROLE_CLIENT")
						.requestMatchers("/helloworld.html").permitAll()//
						.requestMatchers("/error").permitAll()//
				// .anyRequest().permitAll()
				)//
				.oauth2ResourceServer(
						oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
		return http.build();
	}

}