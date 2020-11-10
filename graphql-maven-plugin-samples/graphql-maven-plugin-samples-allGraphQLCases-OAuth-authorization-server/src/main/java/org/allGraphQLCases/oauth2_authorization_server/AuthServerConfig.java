package org.allGraphQLCases.oauth2_authorization_server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

/**
 * This Spring configuration class enables and configures an OAuth authorization server. It creates and returns our JSON
 * web tokens when the client properly authenticates.
 * 
 * @see https://dzone.com/articles/build-an-oauth-20-authorization-server-with-spring
 */
@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

	@Value("${user.oauth.clientId}")
	private String ClientID;

	@Value("${user.oauth.clientSecret}")
	private String ClientSecret;

	@Value("${user.oauth.redirectUris}")
	private String RedirectURLs;

	private final PasswordEncoder passwordEncoder;

	public AuthServerConfig(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()//
				.withClient(ClientID)//
				.secret(passwordEncoder.encode(ClientSecret))//
				.authorizedGrantTypes("password", "authorization_code")//
				.scopes("user_info").autoApprove(true)//
				.redirectUris(RedirectURLs);
	}
}
