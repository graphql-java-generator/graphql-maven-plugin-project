package com.graphql_java_generator.samples.forum.test;

import java.net.URI;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.OAuthTokenExtractor;
import com.graphql_java_generator.client.graphqlrepository.EnableGraphQLRepositories;
import com.graphql_java_generator.samples.forum.client.DirectQueriesWithFieldInputParameters;
import com.graphql_java_generator.samples.forum.client.graphql.PartialPreparedRequests;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Query;

import reactor.core.publisher.Mono;

@TestConfiguration
@SpringBootApplication
@ComponentScan(basePackageClasses = { SpringTestConfig.class, GraphqlClientUtils.class, Query.class,
		PartialPreparedRequests.class, DirectQueriesWithFieldInputParameters.class })
@EnableGraphQLRepositories({ "com.graphql_java_generator.samples.forum.client.graphql" })
public class SpringTestConfig {

	private static Logger logger = LoggerFactory.getLogger(SpringTestConfig.class);

	@Bean
	@Primary
	ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunction(
			ReactiveClientRegistrationRepository clientRegistrations) {
		InMemoryReactiveOAuth2AuthorizedClientService clientService = new InMemoryReactiveOAuth2AuthorizedClientService(
				clientRegistrations);
		AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
				clientRegistrations, clientService);
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				authorizedClientManager);
		oauth.setDefaultClientRegistrationId("provider_test"); // Defines our custom OAuth2 provider
		return oauth;
	}

	@Bean
	@Primary
	public WebClient webClient(//
			ReactiveClientRegistrationRepository clientRegistrations, //
			String graphqlEndpoint, //
			// @Qualifier("defaultCodecCustomizer") CodecCustomizer defaultCodecCustomizer, //
			@Autowired ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunction) {

		return WebClient.builder()//
				.baseUrl(graphqlEndpoint)//
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", graphqlEndpoint))//
				// .codecs(configurer -> configurer.defaultCodecs()) //
				.filter(serverOAuth2AuthorizedClientExchangeFilterFunction)//
				.build();
	}

	/**
	 * This overrides the default one provided by the plugin, to add OAuth2 capacity to Web Socket connection.<br/>
	 * This bean is USELESS if you don't use web socket transport, that is in general: if you don't use subscriptions.
	 *
	 * @param graphqlEndpoint
	 *            The endpoint that is protected by OAuth
	 * @param serverOAuth2AuthorizedClientExchangeFilterFunction
	 *            the {@link ExchangeFilterFunction} that adds the OAuth capability to the http {@link WebClient} for
	 *            this endpoint. It used by the {@link OAuthTokenExtractor} to retrieve a OAuth bearer token, that will
	 *            be used for WebSocket connections.
	 * @return
	 */
	@Bean
	@Primary
	GraphQlClient webSocketGraphQlClient(String graphqlEndpoint,
			@Qualifier("serverOAuth2AuthorizedClientExchangeFilterFunction") ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunction) {
		logger.debug("Creating SpringConfig webSocketGraphQlClient");

		// Creation of an OAuthTokenExtractor based on this OAuth ExchangeFilterFunction
		OAuthTokenExtractor oAuthTokenExtractor = new OAuthTokenExtractor(
				serverOAuth2AuthorizedClientExchangeFilterFunction);

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

		return WebSocketGraphQlClient.builder(graphqlEndpoint, client).build();
	}
}
