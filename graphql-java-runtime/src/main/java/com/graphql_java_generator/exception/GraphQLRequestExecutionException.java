/**
 * 
 */
package com.graphql_java_generator.exception;

/**
 * Thrown when an error occurs during the request execution. This is typically, when Bind Variable are mission, while
 * executing a request.
 * 
 * @author etienne-sf
 */
public class GraphQLRequestExecutionException extends Exception {

	private static final long serialVersionUID = 1L;

	public GraphQLRequestExecutionException(String msg) {
		super(msg);
	}

	public GraphQLRequestExecutionException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
