/**
 * 
 */
package org.allGraphQLCases.server.config;

import java.util.List;

import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

/**
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
		return ErrorType.ExecutionAborted;
	}

}
