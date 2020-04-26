/**
 * 
 */
package com.graphql_java_generator.client;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.graphql_java_generator.client.request.AbstractGraphQLRequest;
import com.graphql_java_generator.client.request.ObjectResponse;
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
	 * Execution of the given simple GraphQL query, and return its response mapped in the relevant POJO. This method
	 * execute a single GraphQL query, not a multi-operational request.<BR/>
	 * The advantage of this method is that you can build all the {@link ObjectResponse} for your application in your
	 * constructor, or in whatever initialization code you have. Using this allows to be sure at startup that the syntax
	 * for all your GraphQL request is valid.
	 * 
	 * @param <T>
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

}
