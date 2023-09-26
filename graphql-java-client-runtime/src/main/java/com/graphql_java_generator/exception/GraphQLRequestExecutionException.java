/**
 * 
 */
package com.graphql_java_generator.exception;

import java.util.List;

import org.springframework.graphql.ResponseError;
import org.springframework.graphql.client.ClientGraphQlResponse;

import graphql.GraphQLError;

/**
 * Thrown when an error occurs during the request execution. This is typically when :
 * <UL>
 * <LI>One or more Bind Variable are missing</LI>
 * <LI>When an error occurred on server side. In this case (and only in this case), the {@link #getErrors()} method
 * returns non null value. This value is the server errors list.</LI>
 * </UL>
 * 
 * @author etienne-sf
 */
public class GraphQLRequestExecutionException extends Exception implements GraphQLRequestExecutionExceptionInterface {

	private static final long serialVersionUID = 1L;

	private final List<ResponseError> errors;
	private final Object data;
	private final ClientGraphQlResponse response;

	public GraphQLRequestExecutionException(String msg) {
		super(msg);
		this.errors = null;
		this.data = null;
		this.response = null;
	}

	public GraphQLRequestExecutionException(String msg, Throwable cause) {
		super(msg, cause);
		this.errors = null;
		this.data = null;
		this.response = null;
	}

	/**
	 * Generates a new instance, from a non empty and non null list of {@link GraphQLError}
	 * 
	 * @param errors
	 *            A list of GraphQL error messages
	 * @param data
	 *            the data returned by the server, parsed by the plugin into the generated POJO that match the GraphQL
	 *            schema. For instance, when executing a full query that requests to query's field, it can for instance
	 *            contain the valid response data for one, while there is an error for the second queried field.
	 */
	public GraphQLRequestExecutionException(List<ResponseError> errors, Object data, ClientGraphQlResponse response) {
		super(buildMessage(errors));
		this.errors = errors;
		this.data = data;
		this.response = response;
	}

	@Override
	public List<ResponseError> getErrors() {
		return this.errors;
	}

	@Override
	public Object getData() {
		return this.data;
	}

	@Override
	public ClientGraphQlResponse getResponse() {
		return this.response;
	}

	/**
	 * 
	 * @param errors
	 *            A non null and non empty list of errors, a specified in the
	 *            <A HREF="http://spec.graphql.org/June2018/#sec-Errors">GraphQL specification</A>
	 * @return
	 */
	private static String buildMessage(List<ResponseError> errors) {
		StringBuilder sb = new StringBuilder();

		boolean first = true;

		if (errors == null || errors.size() == 0) {
			sb.append("Unknown error"); //$NON-NLS-1$
		} else {
			sb.append(errors.size());
			sb.append(" error(s) occurred: "); //$NON-NLS-1$
			for (ResponseError error : errors) {
				if (!first)
					sb.append(", "); //$NON-NLS-1$
				sb.append(error.getMessage());
			}
		}

		return sb.toString();
	}

}
