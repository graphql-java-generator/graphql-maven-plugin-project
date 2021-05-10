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

	private static Logger logger = LoggerFactory.getLogger(GraphQLAutoConfiguration.class);

	@Value(value = "${graphql.endpoint.url}")
	private String graphqlEndpointUrl;

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
	 * The Spring reactive {@link WebClient} that will execute the HTTP requests for GraphQL queries and mutations.
	 */
	@Bean
	@ConditionalOnMissingBean
	public WebClient webClient(String graphqlEndpoint, @Autowired(required = false) HttpClient httpClient,
			@Autowired(required = false) ServerOAuth2AuthorizedClientExchangeFilterFunction oauthFilter) {
		Builder webClientBuilder = WebClient.builder()//
				.baseUrl(graphqlEndpoint)//
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", graphqlEndpoint));

		if (httpClient != null) {
			webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient));
		}
		if (oauthFilter != null) {
			webClientBuilder.filter(oauthFilter);
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
		if (httpClient == null) {
			return new ReactorNettyWebSocketClient(HttpClient.create());
		} else {
			return new ReactorNettyWebSocketClient(httpClient);
		}
	}

}
