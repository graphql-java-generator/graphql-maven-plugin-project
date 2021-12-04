/**
 * 
 */
package com.graphql_java_generator.domain.client.starwars;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import reactor.netty.http.client.HttpClient;

/**
 * This Spring {@link Configuration} class defines the Spring Bean for this GraphQL schema.
 * 
 * @author etienne-sf
 */
@Configuration
public class SpringConfigurationStarWars {

	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(SpringConfigurationStarWars.class);

	@Value(value = "${graphql.endpointStarWars.url}")
	private String graphqlEndpointStarWarsUrl;

	@Value("${graphql.endpointStarWars.subscriptionUrl:${graphql.endpointStarWars.url}}")
	@Deprecated
	private String graphqlEndpointStarWarsSubscriptionUrl;

	/**
	 * This beans defines the GraphQL endpoint for the current GraphQL schema, as a {@link String}
	 * 
	 * 
	 * @return Returns the value of the <I>graphql.endpointStarWars.url</I> application property.
	 * @see https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/reference/html/spring-boot-features.html#boot-features-external-config
	 */
	@Bean
	String graphqlEndpointStarWars() {
		return graphqlEndpointStarWarsUrl;
	}

	/**
	 * This beans defines the GraphQL endpoint for subscriptions for the StarWars server, as a {@link String}. If null,
	 * then the {@link #graphqlEndpoint()} url is used, which is the default.
	 * 
	 * @return Returns the value of the <I>graphql.endpoint.subscriptionUrl</I> application property.
	 * @see https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/reference/html/spring-boot-features.html#boot-features-external-config
	 */
	@Bean
	@Deprecated
	String graphqlSubscriptionEndpointStarWars() {
		return graphqlEndpointStarWarsSubscriptionUrl;
	}

	/**
	 * The Spring reactive {@link WebClient} that will execute the HTTP requests for GraphQL queries and mutations.<BR/>
	 * This bean is only created if no such bean already exists
	 */
	@Bean
	@ConditionalOnMissingBean
	public WebClient webClientStarWars(String graphqlEndpointStarWars, //
			@Autowired(required = false) HttpClient httpClient,
			@Autowired(required = false) ServerOAuth2AuthorizedClientExchangeFilterFunction oauthFilter) {
		return getWebClient(graphqlEndpointStarWars, httpClient, oauthFilter);
	}

	/**
	 * Builds a Spring reactive {@link WebClient}, from the specified parameters.<BR/>
	 * Note: this utility can be used if you need to create your own {@link WebClient}, for instance to add your own
	 * filters to the {@link WebClient}
	 * 
	 * @param graphqlEndpoint
	 * @param httpClient
	 * @param filters
	 *            Optional list of additional filters that will be added to the returned {@link WebClient}
	 * @return
	 */
	public static WebClient getWebClient(String graphqlEndpoint, HttpClient httpClient,
			ExchangeFilterFunction... filters) {
		Builder webClientBuilder = WebClient.builder()//
				.baseUrl(graphqlEndpoint)//
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", graphqlEndpoint));

		if (httpClient != null) {
			webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient));
		}
		if (filters != null) {
			for (ExchangeFilterFunction filter : filters) {
				if (filter != null) {
					webClientBuilder.filter(filter);
				}
			}
		}

		return webClientBuilder.build();
	}

	/**
	 * The Spring reactive {@link WebSocketClient} web socket client for the StarWars GraphQL schema, that will execute
	 * HTTP requests to build the web sockets, for GraphQL subscriptions.<BR/>
	 * This is mandatory if the application latter calls subscription. It may be null otherwise.
	 */
	@Bean
	@ConditionalOnMissingBean
	public WebSocketClient webSocketClientStarWars(@Autowired(required = false) HttpClient httpClient) {
		if (httpClient == null) {
			return new ReactorNettyWebSocketClient(HttpClient.create());
		} else {
			return new ReactorNettyWebSocketClient(httpClient);
		}
	}

}
