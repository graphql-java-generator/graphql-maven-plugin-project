/**
 * 
 */
package graphql.java.client.response;

import graphql.java.client.QueryExecutorImpl;

/**
 * Thrown when the client could not parse the GraphQL response. This response is sent when the client can't understand
 * the response, which is usually the client fault.<BR/>
 * To debug this, the client should be put in trace mode, and the raw response should be checked against the
 * {@link QueryExecutorImpl#parseResponse(String, String, graphql.java.client.request.ObjectResponseDef)} method.
 * 
 * @author EtienneSF
 */
public class GraphQLResponseParseException extends GraphQLExecutionException {

	private static final long serialVersionUID = 1L;

	public GraphQLResponseParseException(String msg) {
		super(msg);
	}

	public GraphQLResponseParseException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
