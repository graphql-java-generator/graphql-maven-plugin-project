/**
 * 
 */
package com.graphql_java_generator.client;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This class is the query executor : a generic class, reponsible for calling the GraphQL server, and return its
 * response as POJOs.
 * 
 * @author EtienneSF
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
	 * @param requestType
	 *            One of "query", "mutation" or "subscription"
	 * @param objectResponse
	 *            Defines what response is expected from the server. The {@link ObjectResponse#getFieldAlias()} method
	 *            returns the field of the query, that is: the query name.
	 * @param parameters
	 *            the input parameters for this query. If the query has no parameters, it may be null or an empty list.
	 * @return The response mapped to the code, generated from the GraphQl server. Or a wrapper for composite responses.
	 * @throws GraphQLRequestExecutionException
	 *             When an error occurs during the request execution, typically a network error, an error from the
	 *             GraphQL server or if the server response can't be parsed
	 * @throws IOException
	 */
	public <T> T execute(String requestType, ObjectResponse objectResponse, Map<String, Object> parameters,
			Class<T> valueType) throws GraphQLRequestExecutionException;

	/**
	 * Execution of the given simple GraphQL query, and return its response mapped in the relevant POJO. This method
	 * execute a single GraphQL query, not a multi-operational request.<BR/>
	 * With this method, there is no check that the query is valid, before calling the server. And it's up to the caller
	 * of this method, to properly insert (that is: in compliance with GraphQL grammar) the parameters for the query,
	 * and for any field that would have parameters.<BR/>
	 * <B>Note:</B> you can call this query directly. But the easiest way is to all the generated method from the
	 * generated QueryType relevant for you schema. This method will take care of the parameters for the query itself,
	 * in pure java.
	 * 
	 * @param <T>
	 *            The GraphQL type to map the response into
	 * @param graphqlQuery
	 *            A string which contains the query, in the GraphQL language. For instance: "{ hero { name } }"
	 * @param queryName
	 *            The name of the query. In this sample: "hero"
	 * @param valueType
	 *            The GraphQL type to map the response into
	 * @return The response mapped to the code, generated from the GraphQl server. Or a wrapper for composite responses.
	 * @throws GraphQLRequestExecutionException
	 * @throws IOException
	 */
	public <T> T execute(String graphqlQuery, Class<T> valueType) throws GraphQLRequestExecutionException, IOException;

}
