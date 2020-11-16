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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.QueryExecutor;
import com.graphql_java_generator.client.QueryExecutorImpl;
import com.graphql_java_generator.client.request.AbstractGraphQLRequest;
import com.graphql_java_generator.client.response.JsonResponseWrapper;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

import reactor.core.publisher.Mono;

/**
 * This is the default implementation for the {@link QueryExecutor} This implementation has been added in version
 * 1.12.<BR/>
 * Dependencies to activate:
 * 
 * <PRE>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-websocket</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-messaging</artifactId>
		</dependence>
 * </PRE>
 * 
 * 
 * @since 1.12
 * @author etienne-sf
 */
public class QueryExecutorSpringImpl implements QueryExecutor {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(QueryExecutorSpringImpl.class);

	@Autowired
	String graphqlEndpoint;

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
	public QueryExecutorSpringImpl(WebClient webClient) {
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
		GraphQLWebSocketHandler webSocketHandler = new GraphQLWebSocketHandler<>(request, subscriptionName,
				subscriptionCallback, subscriptionType, messageType);
		client.doHandshake(webSocketHandler, getWebSocketURI(graphqlEndpoint) + "/subscription");

		// WebSocketStompClient stompClient = new WebSocketStompClient(client);
		// stompClient.setMessageConverter(new MappingJackson2MessageConverter());
		//
		// StompSessionHandler sessionHandler = new GraphQLSubscriptionStompClient<>(request, subscriptionName,
		// subscriptionCallback, subscriptionType, messageType);
		// stompClient.connect(graphqlEndpoint + "/subscription", sessionHandler);

		logger.trace(GRAPHQL_MARKER, "After execution of GraphQL subscription '{}' with request {}", subscriptionName,
				request);
		// The line below is an "anti-reactive" pattern. But this insure the caller that no error occurs when creating
		// the web socket.
		// TODO allow a real reactive use of the Spring implementation.
		// result.block();

		// Let's wait 10s max, until the connection is active
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
		//
		// // Too bad, the web socket connection would not be established
		// // Let's block, to retrieve the error.
		// result.block();
	}

	/**
	 * Retrieves the URI for the Web Socket, based on the GraphQL endpoint that has been given to the Constructor
	 * 
	 * @return
	 * @throws GraphQLRequestExecutionException
	 */
	public static URI getWebSocketURI(String graphqlEndpoint) throws GraphQLRequestExecutionException {
		if (graphqlEndpoint.startsWith("http:") || graphqlEndpoint.startsWith("https:")) {
			// We'll use the ws or the wss protocol. Let's just replace http by ws for that
			try {
				return new URI("ws" + graphqlEndpoint.substring(4));
			} catch (URISyntaxException e) {
				throw new GraphQLRequestExecutionException(
						"Error when trying to determine the Web Socket endpoint for GraphQL endpoint "
								+ graphqlEndpoint,
						e);
			}
		}
		throw new GraphQLRequestExecutionException(
				"non managed protocol for endpoint " + graphqlEndpoint + ". This method manages only http and https");
	}
}
