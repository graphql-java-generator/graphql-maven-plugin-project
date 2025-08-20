/**
 * 
 */
package org.allGraphQLCases.minimal.spring_app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

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
	@Bean
	@SuppressWarnings("static-method")
	public WebClient webClient(ReactiveClientRegistrationRepository clientRegistrations) {
		ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder
				.builder().clientCredentials().build();

		ServerOAuth2AuthorizedClientRepository authorizedClientRepository = new ServerOAuth2AuthorizedClientRepository() {
			@Override
			public <T extends OAuth2AuthorizedClient> Mono<T> loadAuthorizedClient(String clientRegistrationId,
					Authentication principal, ServerWebExchange exchange) {
				return Mono.empty();
			}

			@Override
			public Mono<Void> saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal,
					ServerWebExchange exchange) {
				return Mono.empty();
			}

			@Override
			public Mono<Void> removeAuthorizedClient(String clientRegistrationId, Authentication principal,
					ServerWebExchange exchange) {
				return Mono.empty();
			}
		};

		DefaultReactiveOAuth2AuthorizedClientManager authorizedClientManager = new DefaultReactiveOAuth2AuthorizedClientManager(
				clientRegistrations, authorizedClientRepository);
		authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2 = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				authorizedClientManager);
		oauth2.setDefaultClientRegistrationId("provider_test");

		return WebClient.builder().filter(oauth2).build();
	}

}
