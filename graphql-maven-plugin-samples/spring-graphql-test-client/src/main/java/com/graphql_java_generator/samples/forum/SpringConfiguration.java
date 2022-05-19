/**
 * 
 */
package com.graphql_java_generator.samples.forum;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

/**
 * @author etienne-sf
 *
 */
@Configuration
public class SpringConfiguration {
	/**
	 * As we have subscriptions in this sample, we must create a {@link GraphQlClient} that is a
	 * {@link WebSocketGraphQlClient}
	 */
	@Bean
	GraphQlClient graphQlClient(String graphqlEndpoint) {
		WebSocketClient client = new ReactorNettyWebSocketClient();
		return WebSocketGraphQlClient.builder(graphqlEndpoint, client).build();
	}
}
