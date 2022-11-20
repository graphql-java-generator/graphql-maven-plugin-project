/**
 * 
 */
package org.allGraphQLCases.server.extensions;

import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;

/**
 * A Spring bean that register {@link DataFetcherExceptionResolverAdapter} to configure the way Java exceptions are
 * mapped to GraphQL errors.<br/>
 * This class is both a sample, and the support for an IT test that checks proper error management on client side, when
 * an error occurs on server side.
 * 
 * @author etienne-sf
 */
@Component
public class MyDataFetcherExceptionResolverAdapter extends DataFetcherExceptionResolverAdapter {

	@Override
	protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
		if (ex.getClass().equals(GraphQlException.class) && ex.getMessage().contains("This is an expected error")) {
			return GraphqlErrorBuilder.newError()//
					.errorType(ErrorType.INTERNAL_ERROR)//
					.message(ex.getMessage())//
					.build();
		}

		// Otherwise, it's not a know error. Let's stick to the default behavior
		return null;
	}

}
