/**
 * 
 */
package com.graphql_java_generator.exception;

import java.util.List;

import org.springframework.graphql.ResponseError;

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

	public GraphQLRequestExecutionException(String msg) {
		super(msg);
		errors = null;
	}

	public GraphQLRequestExecutionException(String msg, Throwable cause) {
		super(msg, cause);
		errors = null;
	}

	/**
	 * Generates a new instance, from a non empty and non null list of {@link GraphQLError}
	 * 
	 * @param errors
	 *            A list of GraphQL error messages
	 */
	public GraphQLRequestExecutionException(List<ResponseError> errors) {
		super(buildMessage(errors));
		this.errors = errors;
	}

	@Override
	public List<ResponseError> getErrors() {
		return errors;
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
			sb.append("Unknown error");
		} else {
			sb.append(errors.size());
			sb.append(" error(s) occurred: ");
			for (ResponseError error : errors) {
				if (!first)
					sb.append(", ");
				sb.append(error.getMessage());
			}
		}

		return sb.toString();
	}

}
