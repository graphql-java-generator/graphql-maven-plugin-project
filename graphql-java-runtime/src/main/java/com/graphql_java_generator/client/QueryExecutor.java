/**
 * 
 */
package com.graphql_java_generator.client;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.graphql_java_generator.client.request.AbstractGraphQLRequest;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This class is the query executor : a generic class, reponsible for calling the GraphQL server, and return its
 * response as POJOs.
 * 
 * @author etienne-sf
 */
public interface QueryExecutor {

	Marker GRAPHQL_MARKER = MarkerFactory.getMarker("GRAPHQL");
	Marker GRAPHQL_QUERY_MARKER = MarkerFactory.getMarker("GRAPHQL_QUERY");
	Marker GRAPHQL_MUTATION_MARKER = MarkerFactory.getMarker("GRAPHQL_MUTATION");
	Marker GRAPHQL_SUBSCRIPTION_MARKER = MarkerFactory.getMarker("GRAPHQL_SUBSCRIPTION");

	/**
	 * Execution of the given <B>query</B> or <B>mutation</B> GraphQL request, and return its response mapped in the
	 * relevant POJO. This method executes a partial GraphQL query, or a full GraphQL request.
	 * 
	 * @param <T>
	 *            The type that must be returned by the query or mutation
	 * 
	 * @param graphQLRequest
	 *            Defines what response is expected from the server.
	 * @param parameters
	 *            the input parameters for this query. If the query has no parameters, it may be null or an empty list.
	 * @return The response mapped to the code, generated from the GraphQl server. Or a wrapper for composite responses.
	 * @throws GraphQLRequestExecutionException
	 *             When an error occurs during the request execution, typically a network error, an error from the
	 *             GraphQL server or if the server response can't be parsed
	 * @throws IOException
	 */
	public <T> T execute(AbstractGraphQLRequest graphQLRequest, Map<String, Object> parameters, Class<T> valueType)
			throws GraphQLRequestExecutionException;

	/**
	 * Execution of the given <B>subscription</B> GraphQL request, and return its response mapped in the relevant POJO.
	 * This method executes a partial GraphQL query, or a full GraphQL request.<BR/>
	 * <B>Note:</B> Don't forget to free the server's resources by calling the {@link WebSocketClient#stop()} method of
	 * the returned object.
	 * 
	 * @param <T>
	 *            The type that must be returned by the query or mutation
	 * 
	 * @param graphQLRequest
	 *            Defines what response is expected from the server.
	 * @param subscriptionCallback
	 *            The object that manages the Web Socket callback, when the request is a subscription. This object is
	 *            provided by the application. It contains the callback methods that allow it to receive the GraphQL
	 *            notifications it has subscribed to, and manage errors.
	 * @param parameters
	 *            the input parameters for this query. If the query has no parameters, it may be null or an empty list.
	 * @param t
	 *            The type of the POJO which should be returned. It must be the query or the mutation class, generated
	 *            by the plugin
	 * @return The Web Socket client. This client allows to stop the subscription, by executing its
	 *         {@link WebSocketClient#stop()} method.
	 * @throws GraphQLRequestExecutionException
	 *             When an error occurs during the request execution, typically a network error, an error from the
	 *             GraphQL server or if the server response can't be parsed
	 * @throws IOException
	 */
	public <T> WebSocketClient execute(AbstractGraphQLRequest graphQLRequest, Map<String, Object> parameters,
			SubscriptionCallback<T> subscriptionCallback, Class<T> t) throws GraphQLRequestExecutionException;

}
