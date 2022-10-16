/**
 * 
 */
package org.allGraphQLCases.minimal.spring_app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;

/**
 * The app configuration should be a separate class, so that all sprnig configuration stuff can be created, before the
 * application itself starts
 * 
 * @author etienne-sf
 */
@Configuration
public class SpringConfiguration {
	/**
	 * You can override the WebClient, to provide additional configuration. The best way to do this is to copy/paste the
	 * WebClient bean from the generated autoconfiguration, add the @Primary annotation, then update it according to
	 * your needs.
	 */
	// @Bean
	// @Primary
	// public WebClient webClientAllGraphQLCases(//
	// String graphqlEndpointAllGraphQLCases,
	// ServerOAuth2AuthorizedClientExchangeFilterFunction
	// serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases) {
	// return WebClient.builder()//
	// .baseUrl(graphqlEndpointAllGraphQLCases)//
	// .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
	// .defaultUriVariables(Collections.singletonMap("url", graphqlEndpointAllGraphQLCases))
	// .filter(serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases)//
	// .build();
	// }

	/**
	 * This beans is all that is needed to wire OAuth into the application, thanks to Spring Boot and some configuration
	 * lines in the resources/application.properties file
	 */
	@Bean
	ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases(
			ReactiveClientRegistrationRepository clientRegistrations) {
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				clientRegistrations, new UnAuthenticatedServerOAuth2AuthorizedClientRepository());
		oauth.setDefaultClientRegistrationId("provider_test");
		return oauth;
	}

}
