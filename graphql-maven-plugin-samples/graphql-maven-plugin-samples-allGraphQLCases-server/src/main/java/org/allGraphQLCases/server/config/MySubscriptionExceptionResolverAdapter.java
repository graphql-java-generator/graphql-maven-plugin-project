package org.allGraphQLCases.server.config;

import org.springframework.graphql.execution.SubscriptionExceptionResolverAdapter;
import org.springframework.stereotype.Component;

import graphql.GraphQLError;

/**
 * Implements the spring-graphql exception management, for Subscriptions
 * 
 * @author etienne-sf
 */
@Component
public class MySubscriptionExceptionResolverAdapter extends SubscriptionExceptionResolverAdapter {

	@Override
	protected GraphQLError resolveToSingleError(Throwable exception) {
		if (exception instanceof GraphQlException) {
			// As GraphQlException implements the GraphQLError interface, we can directly return it
			return (GraphQLError) exception;
		} else {
			return null;
		}
	}

}
