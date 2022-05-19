/**
 * 
 */
package com.graphql_java_generator.client;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.graphql_java_generator.client.request.AbstractGraphQLRequest;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This is the default implementation for the {@link RequestExecution} This implementation has been added in version
 * 1.12.<BR/>
 * It is loaded by the {@link SpringConfiguration} Spring configuration class, that is generated with the client code.
 * 
 * @since 1.12
 * @author etienne-sf
 */
@Component
@Deprecated
public class RequestExecutionSpringReactiveImpl implements RequestExecution {

	@Override
	public <R extends GraphQLRequestObject> R execute(AbstractGraphQLRequest graphQLRequest,
			Map<String, Object> parameters, Class<R> dataResponseType) throws GraphQLRequestExecutionException {
		return graphQLRequest.exec(dataResponseType, parameters);
	}

	@Override
	public <R, T> SubscriptionClient execute(AbstractGraphQLRequest graphQLRequest, Map<String, Object> parameters,
			SubscriptionCallback<T> subscriptionCallback, Class<R> subscriptionType, Class<T> messageType)
			throws GraphQLRequestExecutionException {
		return graphQLRequest.exec(parameters, subscriptionCallback, subscriptionType, messageType);
	}

}
