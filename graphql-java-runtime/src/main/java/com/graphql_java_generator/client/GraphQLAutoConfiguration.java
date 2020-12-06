/**
 * 
 */
package com.graphql_java_generator.client;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import reactor.netty.http.client.HttpClient;

/**
 * This classes allows to autoconfigure Spring, with a full default behavior, ready to use. This can be overridden by
 * Spring {@link Bean} or {@link Component}, in the application configuration
 * 
 * @author etienne-sf
 */
@Configuration
public class GraphQLAutoConfiguration {

	@Value("${graphql.endpoint.url}")
	private String graphqlEndpoint;
	@Value("${graphql.endpoint.subscriptionUrl}")
	private String graphqlSubscriptionEndpoint;

	/**
	 * This beans defines the GraphQL endpoint, as a {@link String}
	 * 
	 * 
	 * @return Returns the value of the <I>graphql.endpoint.url</I> application property.
	 * @see https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/reference/html/spring-boot-features.html#boot-features-external-config
	 */
	@Bean
	String graphqlEndpoint() {
		return graphqlEndpoint;
	}

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
	String graphqlSubscriptionEndpoint() {
		return graphqlSubscriptionEndpoint;
	}

	/**
	 * This bean provides a default implementation of the {@link QueryExecutor}. It will be used to execute
	 * query/mutation/subscription against the GraphQL server.<BR/>
	 * Applications can provides there own Spring bean of this type. In which case, this default implementation is not
	 * used. applications.
	 * 
	 * @param graphqlEndpoint
	 *            A <I>graphqlEndpoint</I> Spring bean, of type String, must be provided, with the URL of the GraphQL
	 *            endpoint, for instance <I>https://my.serveur.com/graphql</I>
	 * @param graphqlSubscriptionEndpoint
	 *            If the subscription is on a different endpoint than the main GraphQL endpoint, thant you can define a
	 *            <I>graphqlSubscriptionEndpoint</I> Spring bean, of type String, with this specific URL, for instance
	 *            <I>https://my.serveur.com/graphql/subscription</I>. For instance, Java servers suffer from a
	 *            limitation which prevent to server both GET/POST HTTP verbs and WebSockets on the same URL.<BR/>
	 *            If no bean <I>graphqlSubscriptionEndpoint</I> Spring bean is defined, then the <I>graphqlEndpoint</I>
	 *            URL is also used for subscriptions (which is the standard case).
	 * @param webClient
	 *            The Spring reactive {@link WebClient} that will execute the HTTP requests for GraphQL queries and
	 *            mutations.
	 * @param webSocketClient
	 *            The Spring reactive {@link WebSocketClient} web socket client, that will execute HTTP requests to
	 *            build the web sockets, for GraphQL subscriptions.<BR/>
	 *            This is mandatory if the application latter calls subscription. It may be null otherwise.
	 */
	@Bean
	@ConditionalOnMissingBean
	QueryExecutor queryExecutor(//
			String graphqlEndpoint, //
			@Autowired(required = false) String graphqlSubscriptionEndpoint, //
			WebClient webClient, //
			@Autowired(required = false) WebSocketClient webSocketClient) {
		return new QueryExecutorSpringReactiveImpl(graphqlEndpoint, graphqlSubscriptionEndpoint, webClient,
				webSocketClient);
	}

	/**
	 * The Spring reactive {@link WebClient} that will execute the HTTP requests for GraphQL queries and mutations.
	 */
	@Bean
	@ConditionalOnMissingBean
	WebClient webClient(String graphqlEndpoint, @Autowired(required = false) HttpClient httpClient) {
		Builder webClientBuilder = WebClient.builder()//
				.baseUrl(graphqlEndpoint)//
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", graphqlEndpoint));

		if (httpClient != null) {
			webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient));
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
	WebSocketClient webSocketClient(@Autowired(required = false) HttpClient httpClient) {
		if (httpClient == null) {
			return new ReactorNettyWebSocketClient(HttpClient.create());
		} else {
			return new ReactorNettyWebSocketClient(httpClient);
		}
	}
}
