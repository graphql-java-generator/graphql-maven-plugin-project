/**
 * 
 */
package com.graphql_java_generator.samples.forum;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.StandardWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.QueryExecutor;
import com.graphql_java_generator.client.QueryExecutorImpl;
import com.graphql_java_generator.client.request.AbstractGraphQLRequest;
import com.graphql_java_generator.client.response.JsonResponseWrapper;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

import reactor.core.publisher.Mono;

/**
 * This is the default implementation for the {@link QueryExecutor} This implementation has been added in version 1.12.
 * 
 * @since 1.12
 * @author etienne-sf
 */
@Component
public class QueryExecutorSpringReactiveImpl implements QueryExecutor {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(QueryExecutorSpringReactiveImpl.class);

	/**
	 * A <I>graphqlEndpoint</I> Spring bean, of type String, must be provided, with the URL of the GraphQL endpoint, for
	 * instance <I>https://my.serveur.com/graphql</I>
	 */
	@Autowired
	String graphqlEndpoint;

	/**
	 * If the subscription is on a different endpoint than the main GraphQL endpoint, thant you can define a
	 * <I>graphqlSubscriptionEndpoint</I> Spring bean, of type String, with this specific URL, for instance
	 * <I>https://my.serveur.com/graphql/subscription</I>. For instance, Java servers suffer from a limitation which
	 * prevent to server both GET/POST HTTP verbs and WebSockets on the same URL.<BR/>
	 * If no bean <I>graphqlSubscriptionEndpoint</I> Spring bean is defined, then the <I>graphqlEndpoint</I> URL is also
	 * used for subscriptions (which is the standard case).
	 */
	@Autowired(required = false)
	String graphqlSubscriptionEndpoint;

	WebClient webClient;

	/**
	 * The {@link ObjectMapper} that will read the json response, and map it to the correct java class, generated from
	 * the GraphQL type defined in the source GraphQL schema
	 */
	ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * This constructor may be called by Spring, once it has build a {@link WebClient} bean, or directly, in non Spring
	 * applications.
	 * 
	 * @param webClient
	 */
	@Autowired
	public QueryExecutorSpringReactiveImpl(WebClient webClient) {
		this.webClient = webClient;
	}

	@Override
	public <R> R execute(AbstractGraphQLRequest graphQLRequest, Map<String, Object> parameters,
			Class<R> dataResponseType) throws GraphQLRequestExecutionException {

		if (graphQLRequest.getRequestType().equals(RequestType.subscription))
			throw new GraphQLRequestExecutionException("This method may not be called for subscriptions");

		String jsonRequest = graphQLRequest.buildRequest(parameters);

		try {

			logger.trace(GRAPHQL_MARKER, "Executing GraphQL request: {}", jsonRequest);

			JsonResponseWrapper responseJson = webClient//
					.post()//
					.contentType(MediaType.APPLICATION_JSON)//
					.body(Mono.just(jsonRequest), String.class)//
					.accept(MediaType.APPLICATION_JSON)//
					.retrieve()//
					.bodyToMono(JsonResponseWrapper.class)//
					.block();

			return QueryExecutorImpl.parseDataFromGraphQLServerResponse(objectMapper, responseJson, dataResponseType);

		} catch (IOException e) {
			throw new GraphQLRequestExecutionException(
					"Error when executing query <" + jsonRequest + ">: " + e.getMessage(), e);
		}
	}

	@Override
	public <R, T> void execute(AbstractGraphQLRequest graphQLRequest, Map<String, Object> parameters,
			SubscriptionCallback<T> subscriptionCallback, String subscriptionName, Class<R> subscriptionType,
			Class<T> messageType) throws GraphQLRequestExecutionException {

		// This method accepts only subscription at a time (no query and no mutation)
		if (!graphQLRequest.getRequestType().equals(RequestType.subscription))
			throw new GraphQLRequestExecutionException("This method may be called only for subscriptions");

		// Subscription may be subscribed only once at a time, as this method allows only one subscriptionCallback
		if (graphQLRequest.getSubscription().getFields().size() != 1) {
			throw new GraphQLRequestExecutionException(
					"This method may be called only for one subscription at a time, but there was "
							+ graphQLRequest.getSubscription().getFields().size()
							+ " subscriptions in this GraphQLRequest");
		}

		// The subscription name must be the good one
		if (!graphQLRequest.getSubscription().getFields().get(0).getName().equals(subscriptionName)) {
			throw new GraphQLRequestExecutionException("The subscription provided in the GraphQLRequest is "
					+ graphQLRequest.getSubscription().getFields().get(0).getName() + " but it should be "
					+ subscriptionName);
		}

		// The returned type of this subscription must be the provided messageType
		if (!graphQLRequest.getSubscription().getFields().get(0).getClazz().equals(messageType)) {
			throw new GraphQLRequestExecutionException("This provided message type shoud be "
					+ graphQLRequest.getSubscription().getFields().get(0).getClazz().getName() + " but is "
					+ messageType.getName());
		}

		String request = graphQLRequest.buildRequest(parameters);
		logger.debug(GRAPHQL_MARKER, "Executing GraphQL subscription '{}' with request {}", subscriptionName, request);

		// Let's create and start the Web Socket
		WebSocketClient client = new StandardWebSocketClient();
		GraphQLReactiveWebSocketHandler<R, T> webSocketHandler = new GraphQLReactiveWebSocketHandler<>(request,
				subscriptionName, subscriptionCallback, subscriptionType, messageType);
		logger.trace(GRAPHQL_MARKER, "Before execution of GraphQL subscription '{}' with request {}", subscriptionName,
				request);
		client.execute(getWebSocketURI(), webSocketHandler).subscribe();
		logger.trace(GRAPHQL_MARKER, "After execution of GraphQL subscription '{}' with request {}", subscriptionName,
				request);

		// Let's let 10s max, for the connection to be active
		final int TIMEOUT = 10000;
		for (int i = 0; i < TIMEOUT / 10; i += 1) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
			if (webSocketHandler.session != null) {
				// Ok, we're connected. We're done
				return;
			}
		}

		throw new RuntimeException("The webSocketHandler is not active, after " + (TIMEOUT / 1000) + " seconds");
	}

	/**
	 * Retrieves the URI for the Web Socket, based on the GraphQL endpoint that has been given to the Constructor
	 * 
	 * @return
	 * @throws GraphQLRequestExecutionException
	 */
	public URI getWebSocketURI() throws GraphQLRequestExecutionException {
		String endpoint = (graphqlSubscriptionEndpoint != null) ? graphqlSubscriptionEndpoint : graphqlEndpoint;
		if (endpoint.startsWith("http:") || endpoint.startsWith("https:")) {
			// We'll use the ws or the wss protocol. Let's just replace http by ws for that
			try {
				return new URI("ws" + endpoint.substring(4));
			} catch (URISyntaxException e) {
				throw new GraphQLRequestExecutionException(
						"Error when trying to determine the Web Socket endpoint for GraphQL endpoint " + endpoint, e);
			}
		}
		throw new GraphQLRequestExecutionException(
				"non managed protocol for endpoint " + endpoint + ". This method manages only http and https");
	}
}
