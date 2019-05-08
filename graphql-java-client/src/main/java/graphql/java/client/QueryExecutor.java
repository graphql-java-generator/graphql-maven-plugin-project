/**
 * 
 */
package graphql.java.client;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import graphql.java.client.request.InputParameter;
import graphql.java.client.request.ObjectResponseDef;
import graphql.java.client.response.GraphQLExecutionException;

/**
 * This class is the query executor : a generic class, reponsible for calling the GraphQL server, and return its
 * response as POJOs.
 * 
 * @author EtienneSF
 */
public interface QueryExecutor {

	public static final Marker GRAPHQL_MARKER = MarkerManager.getMarker("GRAPHQL");
	public static final Marker GRAPHQL_QUERY_MARKER = MarkerManager.getMarker("GRAPHQL_QUERY")
			.setParents(GRAPHQL_MARKER);
	public static final Marker GRAPHQL_MUTATION_MARKER = MarkerManager.getMarker("GRAPHQL_MUTATION")
			.setParents(GRAPHQL_MARKER);
	public static final Marker GRAPHQL_SUBSCRIPTION_MARKER = MarkerManager.getMarker("GRAPHQL_SUBSCRIPTION")
			.setParents(GRAPHQL_MARKER);

	/**
	 * Execution of the given simple GraphQL query, and return its response mapped in the relevant POJO. This method
	 * execute a single GraphQL query, not a multi-operational request.
	 * 
	 * @param <T>
	 * 
	 * @param objectResponseDef
	 *            Defines what response is expected from the server. The {@link ObjectResponseDef#getFieldAlias()} method
	 *            returns the field of the query, that is: the query name.
	 * @param parameters
	 *            the input parameters for this query. If the query has no parameters, it may be null or an empty list.
	 * @return The response mapped to the code, generated from the GraphQl server. Or a wrapper for composite responses.
	 * @throws GraphQLExecutionException
	 * @throws IOException
	 */
	public <T> T execute(ObjectResponseDef objectResponseDef, List<InputParameter> parameters, Class<T> valueType)
			throws GraphQLExecutionException, IOException;

}
