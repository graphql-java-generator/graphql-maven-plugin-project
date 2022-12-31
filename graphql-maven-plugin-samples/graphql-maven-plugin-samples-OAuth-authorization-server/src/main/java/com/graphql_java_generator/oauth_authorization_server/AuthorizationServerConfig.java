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
 * 
 * Below are two commands, that can retrieve an OAuth2 token:
 * 
 * <pre>
curl -u "clientId:secret"  -X POST "http://localhost:8181/oauth2/token" -H "Content-Type: application/x-www-form-urlencoded" -d "grant_type=client_credentials" --noproxy "*" -i
curl -u "clientId:secret" -X POST "http://localhost:8181/oauth2/token?grant_type=client_credentials" --noproxy "*" -i
 * </pre>
 * 
 * @author Joe Grandja
 * @since 0.0.1
 */
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {

	@Value("${oauth2.clientId}")
	String clientId;

	@Value("${oauth2.clientSecret}")
	String clientSecret;

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
		return http.formLogin(Customizer.withDefaults()).build();
	}

	// @formatter:off
	@Bean
	public RegisteredClientRepository registeredClientRepository() {
		RegisteredClient registeredClient = RegisteredClient//
				.withId(UUID.randomUUID().toString())//
				.clientId(clientId)//
				.clientSecret("{noop}" + clientSecret)
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)//
				.scope("message.read")//
				.scope("message.write")//
				.scope("ROLE_CLIENT").build();
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
	public ProviderSettings providerSettings(@Value("${server.port}") String portNumber) {
		return ProviderSettings.builder().issuer("http://localhost:" + portNumber).build();
	}

}
