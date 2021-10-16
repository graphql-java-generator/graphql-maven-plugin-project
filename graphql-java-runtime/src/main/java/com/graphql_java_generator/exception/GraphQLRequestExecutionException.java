/**
 * 
 */
package com.graphql_java_generator.exception;

import java.util.Map;

import graphql.GraphQLError;

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

	/**
	 * Generates a new instance, from a non empty and non null list of {@link GraphQLError}
	 * 
	 * @param errors
	 *            A map of errors, a specified in the <A HREF="http://spec.graphql.org/June2018/#sec-Errors">GraphQL
	 *            specification</A>
	 */
	public GraphQLRequestExecutionException(Map<String, Object> errors) {
		super(buildMessage(errors));
	}

	/**
	 * 
	 * @param errors
	 *            A map of errors, a specified in the <A HREF="http://spec.graphql.org/June2018/#sec-Errors">GraphQL
	 *            specification</A>
	 * @return
	 */
	private static String buildMessage(Map<String, Object> errors) {
		@SuppressWarnings("unchecked")
		Map<String, Map<String, Object>> errorIterator = (Map<String, Map<String, Object>>) (Map<?, ?>) errors;

		StringBuilder sb = new StringBuilder();
		sb.append(errors.size());
		sb.append(" error(s) occurred: ");

		boolean first = true;

		for (Map<String, Object> err : errorIterator.values()) {
			if (!first)
				sb.append(", ");
			sb.append(err.get("message"));
		}

		return sb.toString();
	}

}
