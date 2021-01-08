/**
 * 
 */
package com.graphql_java_generator.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.request.AbstractGraphQLRequest;
import com.graphql_java_generator.client.response.JsonResponseWrapper;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.spring.client.GraphQLAutoConfiguration;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * This is the default implementation for the {@link QueryExecutor} This implementation has been added in version
 * 1.12.<BR/>
 * It is loaded by the {@link GraphQLAutoConfiguration} Spring auto-configuration class.
 * 
 * @since 1.12
 * @author etienne-sf
 */
public class QueryExecutorSpringReactiveImpl implements QueryExecutor {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(QueryExecutorSpringReactiveImpl.class);

	/**
	 * A <I>graphqlEndpoint</I> Spring bean, of type String, must be provided, with the URL of the GraphQL endpoint, for
	 * instance <I>https://my.serveur.com/graphql</I>
	 */
	String graphqlEndpoint;

	/**
	 * If the subscription is on a different endpoint than the main GraphQL endpoint, thant you can define a
	 * <I>graphqlSubscriptionEndpoint</I> Spring bean, of type String, with this specific URL, for instance
	 * <I>https://my.serveur.com/graphql/subscription</I>. For instance, Java servers suffer from a limitation which
	 * prevent to server both GET/POST HTTP verbs and WebSockets on the same URL.<BR/>
	 * If no bean <I>graphqlSubscriptionEndpoint</I> Spring bean is defined, then the <I>graphqlEndpoint</I> URL is also
	 * used for subscriptions (which is the standard case).
	 */
	String graphqlSubscriptionEndpoint;

	/**
	 * The Spring reactive {@link WebClient} that will execute the HTTP requests for GraphQL queries and mutations.
	 */
	WebClient webClient;

	/**
	 * The Spring reactive {@link WebSocketClient} web socket client, that will execute HTTP requests to build the web
	 * sockets, for GraphQL subscriptions.<BR/>
	 * This is mandatory if the application latter calls subscription. It may be null otherwise.
	 */
	WebSocketClient webSocketClient;

	/**
	 * The {@link ObjectMapper} that will read the json response, and map it to the correct java class, generated from
	 * the GraphQL type defined in the source GraphQL schema
	 */
	ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * This constructor may be called by Spring, once it has build a {@link WebClient} bean, or directly, in non Spring
	 * applications.
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
	@Autowired
	public QueryExecutorSpringReactiveImpl(String graphqlEndpoint, //
			@Autowired(required = false) String graphqlSubscriptionEndpoint, //
			WebClient webClient, //
			@Autowired(required = false) WebSocketClient webSocketClient) {
		this.graphqlEndpoint = graphqlEndpoint;
		this.graphqlSubscriptionEndpoint = graphqlSubscriptionEndpoint;
		this.webClient = webClient;
		this.webSocketClient = webSocketClient;
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
	public <R, T> SubscriptionClient execute(AbstractGraphQLRequest graphQLRequest, Map<String, Object> parameters,
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

		String request = graphQLRequest.buildRequest(parameters);
		logger.debug(GRAPHQL_MARKER, "Executing GraphQL subscription '{}' with request {}", subscriptionName, request);

		// Let's create and start the Web Socket

		GraphQLReactiveWebSocketHandler<R, T> webSocketHandler = new GraphQLReactiveWebSocketHandler<>(request,
				subscriptionName, subscriptionCallback, subscriptionType, messageType);
		logger.trace(GRAPHQL_MARKER, "Before execution of GraphQL subscription '{}' with request {}", subscriptionName,
				request);
		Disposable disposable = webSocketClient.execute(getWebSocketURI(), webSocketHandler)
				.subscribeOn(Schedulers.single())// Let's have a dedicated thread
				.subscribe();
		logger.trace(GRAPHQL_MARKER, "After execution of GraphQL subscription '{}' with request {}", subscriptionName,
				request);

		return new SubscriptionClientReactiveImpl(disposable, webSocketHandler.getSession());
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
