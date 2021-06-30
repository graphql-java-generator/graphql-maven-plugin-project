/**
 * 
 */
package com.graphql_java_generator.spring.client;

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
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import reactor.netty.http.client.HttpClient;

/**
 * This classes allows to autoconfigure Spring, with a full default behavior, ready to use. This can be overridden by
 * Spring {@link Bean} or {@link Component}, in the application configuration.<BR/>
 * <B>Important notice:</B> This class must not be the target of component scanning. See <a href=
 * "https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-developing-auto-configuration">https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-developing-auto-configuration</a>
 * for more information
 * 
 * @author etienne-sf
 */
@Configuration
public class GraphQLAutoConfiguration {

	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(GraphQLAutoConfiguration.class);

	@Value(value = "${graphql.endpoint.url}")
	private String graphqlEndpointUrl;

	// @Value(value = "${graphql.endpoint2.url:'undefined'}")
	// private String graphqlEndpoint2Url;

	@Value("${graphql.endpoint.subscriptionUrl:${graphql.endpoint.url}}")
	@Deprecated
	private String graphqlEndpointSubscriptionUrl;

	/**
	 * This beans defines the GraphQL endpoint, as a {@link String}
	 * 
	 * 
	 * @return Returns the value of the <I>graphql.endpoint.url</I> application property.
	 * @see https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/reference/html/spring-boot-features.html#boot-features-external-config
	 */
	@Bean
	String graphqlEndpoint() {
		return graphqlEndpointUrl;
	}

	// @Bean
	// @ConditionalOnExpression("'${graphql.endpoint2.url:''undefined''}'!='undefined'")
	// String graphqlEndpoint2() {
	// return graphqlEndpoint2Url;
	// }

	/**
	 * This beans defines the GraphQL endpoint for subscriptions, as a {@link String}. If null, then the
	 * {@link #graphqlEndpoint()} url is used, which is the default.<BR/>
	 * If the subscription is exposed on a different url, then this bean can be used. This is the case for Java, which
	 * has a limitation which prevents to expose web socket (needed for subscription) on the same path that is
	 * accessible with GET or POST.
	 * 
	 * @return Returns the value of the <I>graphql.endpoint.subscriptionUrl</I> application property.
	 * @see https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/reference/html/spring-boot-features.html#boot-features-external-config
	 */
	@Bean
	@Deprecated
	String graphqlSubscriptionEndpoint() {
		return graphqlEndpointSubscriptionUrl;
	}

	/**
	 * The Spring reactive {@link WebClient} that will execute the HTTP requests for GraphQL queries and mutations.<BR/>
	 * This bean is only created if no such bean already exists
	 */
	@Bean
	@ConditionalOnMissingBean
	public WebClient webClient(String graphqlEndpoint, //
			@Autowired(required = false) HttpClient httpClient,
			@Autowired(required = false) ServerOAuth2AuthorizedClientExchangeFilterFunction oauthFilter) {
		return getWebClient(graphqlEndpoint, httpClient, oauthFilter);
	}

	/**
	 * The Spring reactive {@link WebClient} that will execute the HTTP requests for GraphQL queries and mutations.<BR/>
	 * This bean is only created if a second GraphQL endpoint has been provided in the `graphql.endpoint2.url`
	 * properties, and no such bean already exists
	 */
	// @Bean
	// @ConditionalOnMissingBean
	// @ConditionalOnExpression("'${graphql.endpoint2.url:''undefined''}'!='undefined'")
	// public WebClient webClient2(String graphqlEndpoint2, //
	// @Autowired(required = false) HttpClient httpClient2,
	// @Autowired(required = false) ServerOAuth2AuthorizedClientExchangeFilterFunction oauthFilter2) {
	// return getWebClient(graphqlEndpoint2, httpClient2, oauthFilter2);
	// }

	/**
	 * Builds a Spring reactive {@link WebClient}, from the specified parameters.<BR/>
	 * Note: this utility should be reused if you need to create your own {@link WebClient}, for instance to add your
	 * own filters to the {@link WebClient}
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
	 * The Spring reactive {@link WebSocketClient} web socket client, that will execute HTTP requests to build the web
	 * sockets, for GraphQL subscriptions.<BR/>
	 * This is mandatory if the application latter calls subscription. It may be null otherwise.
	 */
	@Bean
	@ConditionalOnMissingBean
	public WebSocketClient webSocketClient(@Autowired(required = false) HttpClient httpClient) {
		return getWebSocketClient(httpClient);
	}

	/**
	 * The Spring reactive {@link WebSocketClient} web socket client, that will execute HTTP requests to build the web
	 * sockets, for GraphQL subscriptions.<BR/>
	 * This is mandatory if the application latter calls subscription against a second GraphQL server (defined by the
	 * `graphql.endpoint2.url` configuration parameter). It may be null otherwise.
	 */
	// @Bean
	// @ConditionalOnMissingBean
	// @ConditionalOnExpression("'${graphql.endpoint2.url:''undefined''}'!='undefined'")
	// public WebSocketClient webSocketClient2(@Autowired(required = false) HttpClient httpClient2) {
	// return getWebSocketClient(httpClient2);
	// }

	/**
	 * Creates the Spring reactive {@link WebSocketClient} that will be used for subscriptions.
	 * 
	 * @param httpClient
	 * @return
	 */
	private WebSocketClient getWebSocketClient(HttpClient httpClient) {
		if (httpClient == null) {
			return new ReactorNettyWebSocketClient(HttpClient.create());
		} else {
			return new ReactorNettyWebSocketClient(httpClient);
		}
	}

}
