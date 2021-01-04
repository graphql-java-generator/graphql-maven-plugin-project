package org.allGraphQLCases.server.oauth2;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 
 * @author etienne-sf
 * @see https://docs.spring.io/spring-security/site/docs/5.4.2/reference/html5/#servlet-authorization-filtersecurityinterceptor
 */
@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	// @Value("${security.oauth2.resource.introspection-uri}")
	String introspectionUri;

	// @Value("${security.oauth2.resource.introspection-client-id}")
	String clientId;

	// @Value("${security.oauth2.resource.introspection-client-secret}")
	String clientSecret;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Check CORS and CSRF
		http//
			// Disabling CORS and CSRF makes POST on the graphql URL work properly. Double-check that before
			// entering in production
				.cors().and().csrf().disable()//
				.authorizeRequests(authz -> authz//
						.antMatchers("/graphql").access("hasRole('ROLE_CLIENT')")//
						.antMatchers("/graphiql").permitAll()//
						// All other URL accesses are prohibited
						.anyRequest().denyAll())//
		// .oauth2ResourceServer(
		// oauth2 -> oauth2.opaqueToken(token -> token.introspectionUri(this.introspectionUri)
		// .introspectionClientCredentials(this.clientId, this.clientSecret)));
		;
	}

	public String getIntrospectionUri() {
		return introspectionUri;
	}

	public void setIntrospectionUri(String introspectionUri) {
		this.introspectionUri = introspectionUri;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

}