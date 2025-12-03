/**
 * 
 */
package org.forum.server.specific_code;

import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import graphql.GraphQLError;
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
	protected GraphQLError resolveToSingleError(@NonNull Throwable ex, @NonNull DataFetchingEnvironment env) {
		if (ex.getClass().equals(GraphQlException.class)) {
			return (GraphQLError) ex;
		} else {
			// Otherwise, it's not a know error. Let's stick to the default behavior
			return null;
		}
	}

}
