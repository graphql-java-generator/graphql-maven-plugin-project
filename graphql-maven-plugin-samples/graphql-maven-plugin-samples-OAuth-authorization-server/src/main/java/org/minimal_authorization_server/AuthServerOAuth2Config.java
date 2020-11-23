package org.minimal_authorization_server;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

@Configuration
@EnableAuthorizationServer
public class AuthServerOAuth2Config extends AuthorizationServerConfigurerAdapter {

	// @Autowired
	// @Qualifier("authenticationManagerBean")
	// private AuthenticationManager authenticationManager;

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory().withClient("sampleClientId").authorizedGrantTypes("implicit").scopes("read")
				.autoApprove(true).and().withClient("clientIdPassword").secret("secret")
				.authorizedGrantTypes("password", "authorization_code", "refresh_token").scopes("read");
	}

	// @Override
	// public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
	//
	// endpoints.tokenStore(tokenStore()).authenticationManager(authenticationManager);
	// }
	//
	// @Bean
	// public TokenStore tokenStore() {
	// return new JdbcTokenStore(dataSource());
	// }
}
