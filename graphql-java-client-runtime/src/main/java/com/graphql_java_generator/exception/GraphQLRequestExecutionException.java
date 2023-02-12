/**
 * 
 */
package com.graphql_java_generator.exception;

import java.util.ArrayList;
import java.util.List;

import com.graphql_java_generator.client.response.Error;

import graphql.GraphQLError;

/**
 * Thrown when an error occurs during the request execution. This is typically, when Bind Variable are missing while
 * executing a request, or when an error occurs on server side. In the later case, the {@link #getErrors()} method
 * allows to retrieve the error data returned by the GraphQL server, including the source location, and extension.
 * 
 * @author etienne-sf
 */
public class GraphQLRequestExecutionException extends Exception {

	private static final long serialVersionUID = 2L;

	/** A mandatory list of errors. This list is not null, but may be empty */
	final List<Error> errors;

	public GraphQLRequestExecutionException(String msg) {
		super(msg);
		errors = new ArrayList<>();
	}

	public GraphQLRequestExecutionException(String msg, Throwable cause) {
		super(msg, cause);
		errors = new ArrayList<>();
	}

	/**
	 * Generates a new instance, from a non empty and non null list of {@link GraphQLError}
	 * 
	 * @param errors
	 *            A list of GraphQL error messages
	 */
	public GraphQLRequestExecutionException(List<Error> errors) {
		this(null, errors);
	}

	public GraphQLRequestExecutionException(String msg, List<Error> errors) {
		super(buildMessage(null, errors));
		this.errors = (errors == null) ? new ArrayList<>() : errors;
	}

	/**
	 * Returns the list of the errors returned by the GraphQL server.
	 * 
	 * @return Each {@link Error} is an instance of {@link GraphQLError}, with additional utility method to retrieve the
	 *         content of the extension field.
	 */
	public List<Error> getErrors() {
		return errors;
	}

	/**
	 * 
	 * @param errorsParam
	 *            A non null and non empty list of errors, a specified in the
	 *            <A HREF="http://spec.graphql.org/June2018/#sec-Errors">GraphQL specification</A>
	 * @return
	 */
	private static String buildMessage(String msg, List<Error> errorsParam) {
		StringBuilder sb = new StringBuilder();

		if (msg != null) {
			sb.append(msg).append(": ");
		}

		if (errorsParam == null || errorsParam.size() == 0) {
			sb.append("Unknown error");
		} else {
			sb.append(errorsParam.size());
			sb.append(" error(s) occurred: ");
			boolean first = true;
			for (Error error : errorsParam) {
				if (!first)
					sb.append(", ");
				sb.append(error.getMessage());
			}
		}

		return sb.toString();
	}

}
