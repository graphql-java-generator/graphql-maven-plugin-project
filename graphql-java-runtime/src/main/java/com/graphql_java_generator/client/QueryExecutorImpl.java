/**
 * 
 */
package com.graphql_java_generator.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.request.AbstractGraphQLRequest;
import com.graphql_java_generator.client.response.JsonResponseWrapper;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This class is deprecated since version v1.12. It is based on the Jersey {@link Client}, but this client has a hard to
 * use the OAuth implementation. The default implementation of this implementation is now based on Spring<BR/>
 * This class is the query executor : a generic class, reponsible for calling the GraphQL server, for query, mutation
 * and subscription.<BR/>
 * It has one major parameter: the GraphQL endpoint. See the {@link #QueryExecutorImpl(String)} for more information.
 * 
 * @author etienne-sf
 */
@Deprecated
public class QueryExecutorImpl implements QueryExecutor {

	static {
		GRAPHQL_QUERY_MARKER.add(GRAPHQL_MARKER);
		GRAPHQL_MUTATION_MARKER.add(GRAPHQL_MARKER);
		GRAPHQL_SUBSCRIPTION_MARKER.add(GRAPHQL_MARKER);
	}

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(QueryExecutorImpl.class);

	/** The Jersey {@link Client}, used to execute the request toward the GraphQL server */
	Client client;
	/** The endpoint, given in the constructor */
	String graphqlEndpoint;
	/** Jackson {@link ObjectMapper}, used for serialisation / de-serialisation */
	ObjectMapper objectMapper;
	/** The Jersey {@link WebTarget}, used to execute the request toward the GraphQL server */
	WebTarget webTarget;

	/**
	 * This constructor expects the URI of the GraphQL server<BR/>
	 * For example: http://my.server.com/graphql or https://my.server.com/graphql
	 * 
	 * @param graphqlEndpoint
	 *            the http URI for the GraphQL endpoint
	 */
	public QueryExecutorImpl(String graphqlEndpoint) {
		this(graphqlEndpoint, ClientBuilder.newClient(), new ObjectMapper());
		this.graphqlEndpoint = graphqlEndpoint;
	}

	/**
	 * This method is deprecated since version v1.12. It is based on the Jersey {@link Client}, but this client has a
	 * hard to use the OAuth implementation. The default implementation of this implementation is now based on
	 * Spring<BR/>
	 * This constructor expects the URI of the GraphQL server. This constructor works only for https servers, not for
	 * http ones.<BR/>
	 * For example: https://my.server.com/graphql<BR/>
	 * It allows to specify the SSLContext and the HostnameVerifier. It is used in the integration test... to remove
	 * most of the control on https protocol, and allow connection to an https with a self-signed certificate.
	 * 
	 * @param graphqlEndpoint
	 *            the https URI for the GraphQL endpoint
	 * @param sslContext
	 * @param hostnameVerifier
	 */
	public QueryExecutorImpl(String graphqlEndpoint, SSLContext sslContext, HostnameVerifier hostnameVerifier) {
		if (graphqlEndpoint.startsWith("http:")) {
			throw new IllegalArgumentException(
					"This GraphQL endpoint is an http one. Please use the relevant Query/Mutation/Subscription constructor (without the SSLContext and HostnameVerifier parameters)");
		}

		this.client = ClientBuilder.newBuilder().sslContext(sslContext).hostnameVerifier(hostnameVerifier).build();
		this.graphqlEndpoint = graphqlEndpoint;
		this.objectMapper = new ObjectMapper();
		this.webTarget = client.target(graphqlEndpoint);
	}

	/**
	 * This method is deprecated since version v1.12. It is based on the Jersey {@link Client}, but this client has a
	 * hard to use the OAuth implementation. The default implementation of this implementation is now based on
	 * Spring<BR/>
	 * This constructor expects the URI of the GraphQL server and a configured JAX-RS client that gives the opportunity
	 * to customize the REST request<BR/>
	 * For example: http://my.server.com/graphql
	 *
	 * @param graphqlEndpoint
	 *            the http URI for the GraphQL endpoint
	 * @param client
	 *            {@link Client} javax.ws.rs.client.Client to support customization of the rest request
	 * @param objectMapper
	 *            {@link ObjectMapper} com.fasterxml.jackson.databind.ObjectMapper to support configurable mapping
	 */
	@Deprecated
	public QueryExecutorImpl(String graphqlEndpoint, Client client, ObjectMapper objectMapper) {
		this.client = client;
		this.graphqlEndpoint = graphqlEndpoint;
		this.objectMapper = objectMapper;
		this.webTarget = client.target(graphqlEndpoint);
	}

	/** {@inheritDoc} */
	@Override
	public <R> R execute(AbstractGraphQLRequest graphQLRequest, Map<String, Object> parameters,
			Class<R> dataResponseType) throws GraphQLRequestExecutionException {

		if (graphQLRequest.getRequestType().equals(RequestType.subscription))
			throw new GraphQLRequestExecutionException("This method may not be called for subscriptions");

		String jsonRequest = graphQLRequest.buildRequest(parameters);

		try {

			logger.trace(GRAPHQL_MARKER, "Executing GraphQL request: {}", jsonRequest);

			Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
			invocationBuilder.header("Accept", MediaType.APPLICATION_JSON);

			JsonResponseWrapper response = invocationBuilder
					.post(Entity.entity(jsonRequest, MediaType.APPLICATION_JSON), JsonResponseWrapper.class);

			return parseDataFromGraphQLServerResponse(objectMapper, response, dataResponseType);
		} catch (IOException e) {
			throw new GraphQLRequestExecutionException(
					"Error when executing query <" + jsonRequest + ">: " + e.getMessage(), e);
		}
	}

	/** {@inheritDoc} */
	@SuppressWarnings("deprecation")
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
		logger.trace(GRAPHQL_MARKER, "Executing GraphQL subscription '{}' with request {}", subscriptionName, request);

		// Let's create and start the Web Socket
		//
		// For internal test, we have a self-signed certificate. So we need to short cut certificate check.
		// DO NOT DO THAT IN PRODUCTION!
		boolean trustAll = (System.getProperty("com.graphql-java-generator.websocket.nosslcheck") != null);
		org.eclipse.jetty.util.ssl.SslContextFactory.Client sslContextFactory = new org.eclipse.jetty.util.ssl.SslContextFactory.Client(
				trustAll);
		HttpClient httpClient = new HttpClient(sslContextFactory);
		WebSocketClient client = new WebSocketClient(httpClient);
		SubscriptionClientWebSocket<R, T> subscriptionClientWebSocket = new SubscriptionClientWebSocket<R, T>(request,
				subscriptionName, subscriptionCallback, subscriptionType, messageType);
		URI uri = getWebSocketURI();
		try {
			client.start();
			ClientUpgradeRequest clientUpgradeRequest = new ClientUpgradeRequest();
			client.connect(subscriptionClientWebSocket, uri, clientUpgradeRequest);
			logger.debug("Connecting to {}", uri);
		} catch (Exception e) {
			String msg = "Error while opening the Web Socket connection to " + uri;
			logger.error(msg);
			throw new GraphQLRequestExecutionException(msg, e);
		}

		// Let's return the Web Socket client, so that the caller can stop it, when needed.
		return new SubscriptionClientImpl(client);
	}

	/**
	 * Retrieves the URI for the Web Socket, based on the GraphQL endpoint that has been given to the Constructor
	 * 
	 * @return
	 * @throws GraphQLRequestExecutionException
	 */
	URI getWebSocketURI() throws GraphQLRequestExecutionException {
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

	/**
	 * Extract the data from the {@link JsonResponseWrapper#data} json node, and return it as a T instance.
	 * 
	 * @param <T>
	 * @param response
	 *            The json response, read from the GraphQL response
	 * @param valueType
	 *            The expected T class
	 * @return
	 * @throws JsonProcessingException
	 * @throws GraphQLRequestExecutionException
	 */
	static <T> T parseDataFromGraphQLServerResponse(ObjectMapper objectMapper, JsonResponseWrapper response,
			Class<T> valueType) throws GraphQLRequestExecutionException, JsonProcessingException {
		if (logger.isTraceEnabled()) {
			logger.trace("Response data: {}", objectMapper.writeValueAsString(response.data));
			logger.trace("Response errors: {}", objectMapper.writeValueAsString(response.errors));
		}

		if (response.errors == null || response.errors.size() == 0) {
			// No errors. Let's parse the data
			return objectMapper.treeToValue(response.data, valueType);
		} else {
			int nbErrors = 0;
			String agregatedMessage = null;
			for (com.graphql_java_generator.client.response.Error error : response.errors) {
				String msg = error.toString();
				nbErrors += 1;
				logger.error(GRAPHQL_MARKER, msg);
				if (agregatedMessage == null) {
					agregatedMessage = msg;
				} else {
					agregatedMessage += ", ";
					agregatedMessage += msg;
				}
			}
			if (nbErrors == 0) {
				throw new GraphQLRequestExecutionException("An unknown error occured");
			} else {
				throw new GraphQLRequestExecutionException(nbErrors + " errors occured: " + agregatedMessage);
			}
		}
	}
}
