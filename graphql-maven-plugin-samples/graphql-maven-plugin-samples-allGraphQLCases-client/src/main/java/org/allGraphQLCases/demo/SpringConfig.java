/**
 * 
 */
package org.allGraphQLCases.demo;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.server.ServerWebExchange;

import com.graphql_java_generator.client.OAuthTokenExtractor;

import reactor.core.publisher.Mono;

/**
 * Spring configuration for this app. The main configuration is the oauth one.
 * 
 * @author etienne-sf
 */
@Configuration
public class SpringConfig {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(SpringConfig.class);

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

	/**
	 * This overrides the default one provided by the plugin, to add OAuth2 capacity to Web Socket connection.<br/>
	 * This bean is USELESS if you don't use web socket transport, that is in general: if you don't use subscriptions.
	 * 
	 * @param graphqlEndpointAllGraphQLCases
	 *            The endpoint that is protected by OAuth
	 * @param serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases
	 *            the {@link ExchangeFilterFunction} that adds the OAuth capability to the http {@link WebClient} for
	 *            this endpoint. It used by the {@link OAuthTokenExtractor} to retrieve a OAuth bearer token, that will
	 *            be used for WebSocket connections.
	 * @return
	 */
	@Bean
	@Qualifier("AllGraphQLCases")
	@Primary
	GraphQlClient webSocketGraphQlClientAllGraphQLCases(String graphqlEndpointAllGraphQLCases,
			@Qualifier("serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases") ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases) {

		logger.debug("Creating SpringConfig webSocketGraphQlClientAllGraphQLCases");

		// Creation of an OAuthTokenExtractor based on this OAuth ExchangeFilterFunction
		OAuthTokenExtractor oAuthTokenExtractor = new OAuthTokenExtractor(
				serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases);

		// The OAuth token must be checked at each execution. The spring WebSocketClient doesn't provide any way to
		// update the headers just before the request execution. So we override the WebSocketClient to add this
		// capability:
		ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient() {
			@Override
			public Mono<Void> execute(URI url, HttpHeaders requestHeaders, WebSocketHandler handler) {
				// Let's retrieve the valid OAuth token
				String authorizationHeaderValue = oAuthTokenExtractor.getAuthorizationHeaderValue();

				// Then we apply it to the given headers
				if (requestHeaders == null) {
					requestHeaders = new HttpHeaders();
				} else {
					requestHeaders.remove(OAuthTokenExtractor.AUTHORIZATION_HEADER_NAME);
				}
				logger.trace("Adding the bearer token to the Subscription websocket request");
				requestHeaders.add(OAuthTokenExtractor.AUTHORIZATION_HEADER_NAME, authorizationHeaderValue);

				// Then, let's execute the Web Socket request
				return super.execute(url, requestHeaders, handler);
			}
		};

		return WebSocketGraphQlClient.builder(graphqlEndpointAllGraphQLCases, client).build();
	}

}
