/**
 * 
 */
package graphql.java.client.response;

import graphql.java.client.QueryExecutorImpl;

/**
 * Thrown when the client could not parse the GraphQL response. This response is sent when the client can't understand
 * the response, which is usually the client fault.<BR/>
 * To debug this, the client should be put in trace mode, and the raw response should be checked against the
 * {@link QueryExecutorImpl#parseResponse(String, String, graphql.java.client.request.ResponseDefinition)} method.
 * 
 * @author EtienneSF
 */
public class GraphQLExecutionException extends Exception {

	private static final long serialVersionUID = 1L;

	public GraphQLExecutionException(String msg) {
		super(msg);
	}

	public GraphQLExecutionException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
