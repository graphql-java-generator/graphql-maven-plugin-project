package org.allGraphQLCases;

import java.net.URI;
import java.util.Collections;

import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.client2.util.MyQueryTypeExecutorAllGraphQLCases2;
import org.allGraphQLCases.demo.impl.PartialDirectQueries;
import org.forum.client.util.QueryExecutorForum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.graphql.client.ClientGraphQlRequest;
import org.springframework.graphql.client.ClientGraphQlResponse;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.GraphQlClientInterceptor;
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

import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.OAuthTokenExtractor;
import com.graphql_java_generator.client.graphqlrepository.EnableGraphQLRepositories;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
@SpringBootApplication
@ComponentScan(basePackageClasses = { GraphqlClientUtils.class, MyQueryTypeExecutorAllGraphQLCases.class,
		MyQueryTypeExecutorAllGraphQLCases2.class, QueryExecutorForum.class, PartialDirectQueries.class }, //
		excludeFilters = { //
				@Filter(type = FilterType.REGEX, pattern = "/SpringConfig"),
				@Filter(type = FilterType.REGEX, pattern = "/Main") })
@PropertySource("classpath:/application.properties")
@EnableGraphQLRepositories({ "org.allGraphQLCases.demo.impl", "org.allGraphQLCases.subscription.graphqlrepository",
		"org.allGraphQLCases.two_graphql_servers" })
@SuppressWarnings("deprecation")
public class SpringTestConfig {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(SpringTestConfig.class);

	static class MyInterceptor implements GraphQlClientInterceptor {
		private static Logger logger = LoggerFactory.getLogger(MyInterceptor.class);
		final private String beanSuffix;

		public MyInterceptor(String beanSuffix) {
			this.beanSuffix = beanSuffix;
		}

		@Override
		public Flux<ClientGraphQlResponse> interceptSubscription(ClientGraphQlRequest request,
				SubscriptionChain chain) {
			return chain.next(request).doOnNext(response -> this.interceptionSubscriptionResponse(response));
		}

		/** Interception of each message received on subscription */
		public void interceptionSubscriptionResponse(ClientGraphQlResponse response) {
			if (response.isValid()) {
				logger.debug("[subscription interception] Received a valid response for '{}': {}", beanSuffix,
						response.getData());
			} else {
				logger.debug("[subscription interception] Received a non valid response for '{}': {}", beanSuffix,
						response.getErrors());
			}
		}
	}

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
	ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunctionForum(
			ReactiveClientRegistrationRepository clientRegistrations) {
		return null;
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
	 * This overrides the default one provided by the plugin. It adds the OAuth token generation, thanks to the
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

		return WebSocketGraphQlClient.builder(graphqlEndpointAllGraphQLCases, client)
				.interceptor(new MyInterceptor("AllGraphQLCases")).build();
	}
}
