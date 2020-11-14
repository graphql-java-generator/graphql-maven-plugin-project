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
 * This class is the query executor : a generic class, responsible for calling the GraphQL server, and return its
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
	 * @param <R>
	 *            The class that is generated from the query or the mutation definition in the GraphQL schema
	 * 
	 * @param graphQLRequest
	 *            Defines what response is expected from the server.
	 * @param parameters
	 *            the input parameters for this query. If the query has no parameters, it may be null or an empty list.
	 * @param dataResponseType
	 *            The class generated for the query or the mutation type. The data tag of the GraphQL server response
	 *            will be mapped into an instance of this class.
	 * @return The response mapped to the code, generated from the GraphQl server. Or a wrapper for composite responses.
	 * @throws GraphQLRequestExecutionException
	 *             When an error occurs during the request execution, typically a network error, an error from the
	 *             GraphQL server or if the server response can't be parsed
	 * @throws IOException
	 */
	public <R> R execute(AbstractGraphQLRequest graphQLRequest, Map<String, Object> parameters,
			Class<R> dataResponseType) throws GraphQLRequestExecutionException;

	/**
	 * Executes the given <B>subscription</B> GraphQL request, and returns the relevant {@link WebSocketClient}. The
	 * given <I>subscriptionCallback</I> will receive the notifications that have been subscribed by this subscription.
	 * Only one Subscription may be executed at a time: it may be a partial Request (always limited to one query), or a
	 * full request that contains only one subscription.<BR/>
	 * <B>Note:</B> Don't forget to free the server's resources by calling the {@link WebSocketClient#stop()} method of
	 * the returned object.
	 * 
	 * @param <R>
	 *            The class that is generated from the subscription definition in the GraphQL schema. It contains one
	 *            attribute, for each available subscription. The data tag of the GraphQL server response will be mapped
	 *            into an instance of this class.
	 * @param <T>
	 *            The type that must be returned by the query or mutation: it's the class that maps to the GraphQL type
	 *            returned by this subscription.
	 * @param graphQLRequest
	 *            Defines what response is expected from the server.
	 * @param parameters
	 *            The input parameters for this subscription. If the query has no parameters, it may be null or an empty
	 *            list.
	 * @param subscriptionCallback
	 *            The object that will be called each time a message is received, or an error on the subscription
	 *            occurs. This object is provided by the application.
	 * @param subscriptionName
	 *            The name of the subscription that should be subscribed by this method call. It will be used to check
	 *            that the correct GraphQLRequest has been provided by the caller.
	 * @param subscriptionType
	 *            The R class
	 * @param messageType
	 *            The T class
	 * @return The Subscription client. It allows to stop the subscription, by executing its
	 *         {@link SubscriptionClient#unsubscribe()} method. This will stop the incoming notification flow, and will
	 *         free resources on both the client and the server.
	 * @throws GraphQLRequestExecutionException
	 *             When an error occurs during the request execution, typically a network error, an error from the
	 *             GraphQL server or if the server response can't be parsed
	 * @throws IOException
	 */
	public <R, T> SubscriptionClient execute(AbstractGraphQLRequest graphQLRequest, Map<String, Object> parameters,
			SubscriptionCallback<T> subscriptionCallback, String subscriptionName, Class<R> subscriptionType,
			Class<T> messageType) throws GraphQLRequestExecutionException;

}
