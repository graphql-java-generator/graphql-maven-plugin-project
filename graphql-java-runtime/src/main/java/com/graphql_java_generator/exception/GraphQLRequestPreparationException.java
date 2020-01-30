/**
 * 
 */
package com.graphql_java_generator.exception;

/**
 * Thrown when an error occurs during the request preparation. This is typically, when the {@link Builder} is called
 * with invalid values (non existing fields, field from other objects...) or when a call to a query is attempted with a
 * ObjectResponse build for another GraphQL type (for client mode)<BR/>
 * 
 * @author EtienneSF
 */
public class GraphQLRequestPreparationException extends Exception {

	private static final long serialVersionUID = 1L;

	public GraphQLRequestPreparationException(String msg) {
		super(msg);
	}

	public GraphQLRequestPreparationException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
