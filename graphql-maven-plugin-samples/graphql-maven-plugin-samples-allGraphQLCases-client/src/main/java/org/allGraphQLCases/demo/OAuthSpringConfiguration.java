/**
 * 
 */
package org.allGraphQLCases.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;

/**
 * @author etienne-sf
 */
@Configuration
public class OAuthSpringConfiguration {
	@Bean
	ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases(
			ReactiveClientRegistrationRepository clientRegistrations) {
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				clientRegistrations, new UnAuthenticatedServerOAuth2AuthorizedClientRepository());
		oauth.setDefaultClientRegistrationId("provider_test");
		return oauth;
	}

	@Bean
	ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunctionForum(
			ReactiveClientRegistrationRepository clientRegistrations) {
		return null;
	}
}
