package com.graphql_java_generator.oauth_authorization_server;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * This classes configures the allowed accesses to get tokens. It actually contains no access control.<br/>
 * <br/>
 * This is for test only. DO NOT DO THAT IN PRODUCTION.
 * 
 * @author etienne-sf
 *
 */
@EnableWebSecurity
public class DefaultSecurityConfig {

	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http//
				.cors().and().csrf().disable()// DO NOT DO THAT IN PRODUCTION (this allows simple curl request to work:
												// useful for tests)
				// .authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().permitAll())
				.authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
		// .formLogin(withDefaults())
		;
		return http.build();
	}

	@Bean
	UserDetailsService users() {
		UserDetails user = User.withDefaultPasswordEncoder().username("userBasicAuth").password("pwdBasicAuth")
				.roles("USER").build();
		return new InMemoryUserDetailsManager(user);
	}
}
