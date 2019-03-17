/**
 * 
 */
package graphql.java.client;

import java.util.List;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import graphql.java.client.request.InputParameter;
import graphql.java.client.request.ResponseDefinition;

/**
 * This class is the query executor : a generic class, reponsible for calling
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
	 * Execution of the given query
	 * 
	 * @param queryName
	 *            The name of the query, taken from the graphql schema
	 * @param parameters
	 *            the input parameters for this query. If the query has no parameters, it may be null or an empty list.
	 * @param reponseDef
	 *            _The_ specificity of graphql: the definition of the value, that the graphql should return
	 */
	public void execute(String queryName, List<InputParameter> parameters, ResponseDefinition responseDef);

}
