/**
 * 
 */
package com.graphql_java_generator.client;

import java.util.Map;

import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.request.AbstractGraphQLRequest;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This method uses the
 * <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws</a>) protocol for all
 * requests (queries, mutations and subscriptions). That is: all requests are executed within the same web socket.
 * 
 * @author etienne-sf
 * @since 1.18
 */
public class RequestExecutionGraphQLTransportWSImpl extends RequestExecutionSpringReactiveImpl {

	public RequestExecutionGraphQLTransportWSImpl(String graphqlEndpoint, String graphqlSubscriptionEndpoint,
			WebClient webClient, WebSocketClient webSocketClient,
			ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunction,
			OAuthTokenExtractor oAuthTokenExtractor) {
		super(graphqlEndpoint, graphqlSubscriptionEndpoint, webClient, webSocketClient,
				serverOAuth2AuthorizedClientExchangeFilterFunction, oAuthTokenExtractor);
	}

	@Override
	public <R extends GraphQLRequestObject> R execute(AbstractGraphQLRequest graphQLRequest,
			Map<String, Object> parameters, Class<R> dataResponseType) throws GraphQLRequestExecutionException {

		// This method accepts only queries and mutations
		if (graphQLRequest.getRequestType().equals(RequestType.subscription))
			throw new GraphQLRequestExecutionException("This method may not be called for subscriptions");

		return webSocketHandler.executeQueryOrMutation(graphQLRequest, parameters, dataResponseType);
	}

}
