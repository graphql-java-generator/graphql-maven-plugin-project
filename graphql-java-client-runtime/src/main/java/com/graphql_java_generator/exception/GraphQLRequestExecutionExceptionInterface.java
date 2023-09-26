package com.graphql_java_generator.exception;

import java.util.List;

import org.springframework.graphql.ResponseError;
import org.springframework.graphql.client.ClientGraphQlResponse;

public interface GraphQLRequestExecutionExceptionInterface {

	/** Allows to retrieve the list of error that have been received from the GraphQL server */
	public List<ResponseError> getErrors();

	/**
	 * Retrieves the data part of the response: it may contain valid data, even if when the server sent errors back, for
	 * instance if multiple fields of a query or a mutation are requested
	 */
	public Object getData();

	/**
	 * Get the full server response, for further investigation.
	 * 
	 * @return
	 */
	public ClientGraphQlResponse getResponse();

}