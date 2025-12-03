package org.forum.server.specific_code;

import org.springframework.graphql.execution.SubscriptionExceptionResolverAdapter;
import org.springframework.lang.NonNull;
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
	protected GraphQLError resolveToSingleError(@NonNull Throwable exception) {
		if (exception instanceof GraphQlException) {
			return (GraphQLError) exception;
		} else {
			return null;
		}
	}

}
