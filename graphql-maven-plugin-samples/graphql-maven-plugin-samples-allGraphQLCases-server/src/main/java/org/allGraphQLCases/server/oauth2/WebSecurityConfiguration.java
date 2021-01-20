package org.allGraphQLCases.server.oauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.FixedAuthoritiesExtractor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 
 * @author etienne-sf
 * @see https://docs.spring.io/spring-security/site/docs/5.4.2/reference/html5/#servlet-authorization-filtersecurityinterceptor
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Value("${security.oauth2.resource.introspection-uri}")
	String introspectionUri;

	@Value("${security.oauth2.client.client-id}")
	String clientId;

	@Value("${security.oauth2.client.client-secret}")
	String clientSecret;

	FixedAuthoritiesExtractor s;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Check CORS and CSRF
		http//
			// Disabling CORS and CSRF makes POST on the graphql URL work properly. Double-check that before
			// entering in production
				.cors().and().csrf().disable()//
				.authorizeRequests(authz -> authz//
						.antMatchers(HttpMethod.GET, "/graphql/subscription").authenticated()// .access("hasRole('ROLE_CLIENT')")//
						.antMatchers(HttpMethod.POST, "/graphql").authenticated()// .access("hasRole('ROLE_CLIENT')")//
						.antMatchers(HttpMethod.GET, "/graphiql").permitAll()//
						// All other URL accesses are prohibited
						.anyRequest().denyAll())//
				.oauth2ResourceServer(oauth2 -> oauth2.opaqueToken(
						// When all lines below are commented, then a custom OpaqueTokenIntrospector is used. See
						// CustomAuthoritiesOpaqueTokenIntrospector
						token -> token//
								////////////////////////////////////////////////////
								// Lines below: taken from
								// https://docs.spring.io/spring-security/site/docs/current/reference/html5/#oauth2resourceserver-opaque-sansboot
								// Taken from boot auto configuration:
								// .oauth2ResourceServer(OAuth2ResourceServerConfigurer::opaqueToken)
								// Overriding boot auto configuration (But NimbusOpaqueTokenIntrospector can't
								// read Authorities from the token!)
								// .introspector(new NimbusOpaqueTokenIntrospector(introspectionUri, clientId,
								//////////////////////////////////////////////////// clientSecret))
								/////////////////////////////////////
								// Two lines below: taken from
								//////////////////////////////////////////////////// https://www.baeldung.com/spring-security-oauth-resource-server
								.introspectionUri(this.introspectionUri)//
								.introspectionClientCredentials(this.clientId, this.clientSecret)//
				)//
				);
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