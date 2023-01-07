/**
 * 
 */
package org.forum.server.specific_code;

import java.util.List;

import org.springframework.graphql.execution.ErrorType;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

/**
 * Instances of this classes will be sent back as INTERNAL_ERROR GraphQL errors, with their message
 * 
 * @author etienne-sf
 */
public class GraphQlException extends RuntimeException implements GraphQLError {

	private static final long serialVersionUID = 1L;

	public GraphQlException(String msg) {
		super(msg);
	}

	@Override
	public List<SourceLocation> getLocations() {
		return null;
	}

	@Override
	public ErrorClassification getErrorType() {
		return ErrorType.INTERNAL_ERROR;
	}

}
