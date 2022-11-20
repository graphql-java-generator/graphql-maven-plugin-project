/**
 * 
 */
package org.allGraphQLCases.server.extensions;

import java.util.List;

import org.springframework.graphql.execution.ErrorType;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

/**
 * @author etienne-sf
 *
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
		return ErrorType.BAD_REQUEST;
	}

}
