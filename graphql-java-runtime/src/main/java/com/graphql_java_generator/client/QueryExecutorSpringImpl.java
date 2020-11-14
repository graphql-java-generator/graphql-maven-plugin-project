/**
 * 
 */
package com.graphql_java_generator.client;

import java.util.Map;

import com.graphql_java_generator.client.request.AbstractGraphQLRequest;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This is the default implementation for the {@link QueryExecutor} This implementation has been added in version 1.12.
 * 
 * @since 1.12
 * @author etienne-sf
 */
public class QueryExecutorSpringImpl implements QueryExecutor {

	@Override
	public <R> R execute(AbstractGraphQLRequest graphQLRequest, Map<String, Object> parameters,
			Class<R> dataResponseType) throws GraphQLRequestExecutionException {
	}

	@Override
	public <R, T> SubscriptionClient execute(AbstractGraphQLRequest graphQLRequest, Map<String, Object> parameters,
			SubscriptionCallback<T> subscriptionCallback, String subscriptionName, Class<R> subscriptionType,
			Class<T> messageType) throws GraphQLRequestExecutionException {

	}

}
