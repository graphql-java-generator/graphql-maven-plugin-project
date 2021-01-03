/**
 * 
 */
package org.boot_doc;

import java.security.KeyPair;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @author etienne-sf
 */
// @EnableAuthorizationServer Useless, as we're using JwkSetEndpoint and JwkSetEndpointConfiguration
@Import(AuthorizationServerEndpointsConfiguration.class)
@Configuration
public class JwkSetConfiguration extends AuthorizationServerConfigurerAdapter {

	AuthenticationManager authenticationManager;
	KeyPair keyPair;

	public JwkSetConfiguration(AuthenticationConfiguration authenticationConfiguration, KeyPair keyPair)
			throws Exception {

		this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
		this.keyPair = keyPair;
	}

	// ... client configuration, etc.

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
		// @formatter:off
		endpoints.authenticationManager(this.authenticationManager).accessTokenConverter(accessTokenConverter())
				.tokenStore(tokenStore());
		// @formatter:on
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setKeyPair(this.keyPair);
		return converter;
	}
}
