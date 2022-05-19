/**
 * 
 */
package org.forum.server.specific_code;

import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;

import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.schema.DataFetchingEnvironment;

/**
 * A custom {@link DataFetcherExceptionResolver} to demonstrate how to manage errors on server side. <br/>
 * It's only role is to transmit the exception message, so that we can check it on server side (this is an integration
 * test, that check proper exception management for the client).
 * 
 * @author etienne-sf
 */
public class CustomExceptionHandler extends DataFetcherExceptionResolverAdapter implements DataFetcherExceptionHandler {

	@Override
	protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
		return new ExceptionWhileDataFetching(null, ex, null);
	}

}
