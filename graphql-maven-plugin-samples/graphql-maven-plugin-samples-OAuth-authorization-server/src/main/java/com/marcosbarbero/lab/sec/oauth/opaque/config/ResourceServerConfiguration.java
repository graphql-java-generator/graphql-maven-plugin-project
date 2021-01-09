package com.marcosbarbero.lab.sec.oauth.opaque.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

	private final TokenStore tokenStore;

	public ResourceServerConfiguration(final TokenStore tokenStore) {
		this.tokenStore = tokenStore;
	}

	@Override
	public void configure(final ResourceServerSecurityConfigurer resources) {
		resources.tokenStore(tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		// We need to let an open access to this Hello World URL, so that the maven build can check when the OAuth
		// authorization server is properly started
		http.authorizeRequests(authz -> authz.antMatchers("/helloworld.html").permitAll());
	}

}
