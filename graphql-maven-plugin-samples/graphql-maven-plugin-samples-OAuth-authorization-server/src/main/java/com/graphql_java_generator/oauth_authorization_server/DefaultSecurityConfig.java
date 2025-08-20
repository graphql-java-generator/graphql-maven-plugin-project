package com.graphql_java_generator.oauth_authorization_server;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Baeldung
 * @since 0.1.0
 */
@EnableWebSecurity
public class DefaultSecurityConfig {

	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		return http//
				.authorizeHttpRequests(auth -> auth//
						.requestMatchers("/helloWorld").anonymous()//
						.anyRequest().authenticated()//
				).formLogin(withDefaults())//
				.build();
	}

	@Bean
	UserDetailsService users() {
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		UserDetails user = User.builder()//
				.username("user1")//
				.password("password")//
				.passwordEncoder(encoder::encode)//
				.roles("USER")//
				.build();
		return new InMemoryUserDetailsManager(user);
	}

}
