/**
 * 
 */
package com.graphql_java_generator.exception;

import java.util.List;

import org.springframework.graphql.ResponseError;
import org.springframework.graphql.client.ClientGraphQlResponse;

/**
 * This class is the unchecked exception version of the {@link GraphQLRequestExecutionException}. It is used in reactive
 * implementation to propagate an exception that would occur during the request execution, typically an (or more) error
 * returned by the server.
 * 
 * @author etienne-sf
 */
public class GraphQLRequestExecutionUncheckedException extends RuntimeException
		implements GraphQLRequestExecutionExceptionInterface {

	private static final long serialVersionUID = 1L;

	final private GraphQLRequestExecutionException graphQLRequestExecutionException;

	public GraphQLRequestExecutionUncheckedException(GraphQLRequestExecutionException e) {
		super(e.getMessage(), e);
		this.graphQLRequestExecutionException = e;
	}

	/** Retrieve the checked exception that is the source of this exception */
	public GraphQLRequestExecutionException getGraphQLRequestExecutionException() {
		return this.graphQLRequestExecutionException;
	}

	@Override
	public List<ResponseError> getErrors() {
		return this.graphQLRequestExecutionException.getErrors();
	}

	@Override
	public Object getData() {
		return this.graphQLRequestExecutionException.getData();
	}

	@Override
	public ClientGraphQlResponse getResponse() {
		return this.graphQLRequestExecutionException.getResponse();
	}
}
