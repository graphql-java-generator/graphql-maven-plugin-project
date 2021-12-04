/**
 * 
 */
package com.graphql_java_generator.exception;

/**
 * Thrown when an internal error of the GraphQL generator occurs
 * 
 * @author etienne-sf
 */
public class GraphQLInternalErrorException extends GraphQLRequestPreparationException {

	private static final long serialVersionUID = 1L;

	public GraphQLInternalErrorException(String msg) {
		super(msg);
	}

	public GraphQLInternalErrorException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
