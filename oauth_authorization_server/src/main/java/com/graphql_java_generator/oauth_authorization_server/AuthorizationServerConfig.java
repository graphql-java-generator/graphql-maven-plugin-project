package com.graphql_java_generator.oauth_authorization_server;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

/**
 * In the configuration below, we are adding an in-memory client, providing a bean to generate a 2048-byte RSA key and
 * configuring a unique issuer URL as required by an authorization server.
 *
 * A sample query, to get an OAuth token:
 * 
 * <pre>
curl  -X POST "http://localhost:8181/oauth2/token?grant_type=client_credentials&client_id=clientId&client_secret=secret" --noproxy "*" -i
curl -u "userBasicAuth:pwdBasicAuth" -X POST "http://localhost:8181/oauth2/token?grant_type=client_credentials&client_id=clientId&client_secret=secret" --noproxy "*" -i
 * </pre>
 * 
 * Then, reuse the previous token in the next query:
 * 
 * <pre>
curl -i -X POST "http://localhost:8180/graphql" -H "Authorization: Bearer 8c8e4a5b-d903-4ed6-9738-6f7f364b87ec" --noproxy "*"
 * </pre>
 * 
 */
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {

	@Value("${oauth2.clientId}")
	String clientId;

	@Value("${oauth2.clientSecret}")
	String clientSecret;

	// @Bean
	// public RegisteredClientRepository registeredClientRepository() {
	// RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())//
	// .clientId(clientId)//
	// .clientSecret(clientSecret)//
	// .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
	// .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)//
	// .scope("ROLE_CLIENT")//
	// // .scope("ROLE_CLIENT2")//
	// .build();
	//
	// return new InMemoryRegisteredClientRepository(registeredClient);
	// }
	//
	// @Bean
	// public ProviderSettings providerSettings_NOTUSED() {
	// // Defaults are :
	// // settings.put(AUTHORIZATION_ENDPOINT, "/oauth2/authorize");
	// // settings.put(TOKEN_ENDPOINT, "/oauth2/token");
	// // settings.put(JWK_SET_ENDPOINT, "/oauth2/jwks");
	// // settings.put(TOKEN_REVOCATION_ENDPOINT, "/oauth2/revoke");
	// // settings.put(TOKEN_INTROSPECTION_ENDPOINT, "/oauth2/introspect");
	// // settings.put(OIDC_CLIENT_REGISTRATION_ENDPOINT, "/connect/register");
	//
	// return ProviderSettings.builder().build();
	// }

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
		return http.formLogin(Customizer.withDefaults()).build();
	}

	// @formatter:off
	@Bean
	public RegisteredClientRepository registeredClientRepository() {
		RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("messaging-client").clientSecret("{noop}secret")
				.clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.redirectUri("http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc")
				.redirectUri("http://127.0.0.1:8080/authorized").scope(OidcScopes.OPENID).scope("message.read")
				.scope("message.write")//
				// .clientSettings(clientSettings -> clientSettings.requireUserConsent(true))//
				.build();
		return new InMemoryRegisteredClientRepository(registeredClient);
	}
	// @formatter:on

	@Bean
	public JWKSource<SecurityContext> jwkSource() {
		RSAKey rsaKey = Jwks.generateRsa();
		JWKSet jwkSet = new JWKSet(rsaKey);
		return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
	}

	@Bean
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}

	@Bean
	public ProviderSettings providerSettings() {
		return ProviderSettings.builder().issuer("http://auth-server:9000").build();
	}

}