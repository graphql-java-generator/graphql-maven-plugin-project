/**
 * 
 */
package org.allGraphQLCases.demo;

import java.net.URI;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

import com.graphql_java_generator.client.OAuthTokenExtractor;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

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
	@Primary
	ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases(
			ReactiveClientRegistrationRepository clientRegistrations) {
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				clientRegistrations, new UnAuthenticatedServerOAuth2AuthorizedClientRepository());
		oauth.setDefaultClientRegistrationId("provider_test");
		return oauth;
	}

	@Bean
	@Primary
	public WebClient webClientAllGraphQLCases(String graphqlEndpointAllGraphQLCases, //
			CodecCustomizer defaultCodecCustomizer, //
			@Autowired(required = false) @Qualifier("httpClientAllGraphQLCases") HttpClient httpClientAllGraphQLCases,
			@Autowired(required = false) @Qualifier("serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases") ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases) {
		return WebClient.builder()//
				.baseUrl(graphqlEndpointAllGraphQLCases)//
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", graphqlEndpointAllGraphQLCases))
				.filter(serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases)//
				.build();
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
