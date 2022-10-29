package org.allGraphQLCases.server.oauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * 
 * @author etienne-sf
 * @see https://docs.spring.io/spring-security/site/docs/5.4.2/reference/html5/#servlet-authorization-filtersecurityinterceptor
 */
@EnableWebFluxSecurity
public class WebSecurityConfiguration {

	@Value("${spring.security.oauth2.resource.introspection-uri}")
	String introspectionUri;

	@Value("${spring.security.oauth2.client.client-id}")
	String clientId;

	@Value("${spring.security.oauth2.client.client-secret}")
	String clientSecret;

	@Bean
	SecurityWebFilterChain configure(ServerHttpSecurity http) throws Exception {
		return http//
					// Disabling CORS and CSRF makes POST on the graphql URL work properly. Double-check that before
					// entering in production
				.cors().and().csrf().disable()//
				.authorizeExchange().pathMatchers("/my/updated/graphql/path").authenticated()//
				.and().oauth2Login() // oauth2Client()
				// .access("hasRole('ROLE_CLIENT')")//
				.and().build();
	}

}