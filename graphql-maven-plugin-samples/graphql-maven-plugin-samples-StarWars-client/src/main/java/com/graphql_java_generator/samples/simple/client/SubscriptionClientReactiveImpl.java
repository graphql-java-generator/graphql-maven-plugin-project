/**
 * 
 */
package com.graphql_java_generator.samples.simple.client;

import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

import reactor.core.Disposable;

/**
 * Default implementation for the {@link SubscriptionClient} interface.
 * 
 * @author etienne-sf
 */
class SubscriptionClientReactiveImpl implements SubscriptionClient {

	/** The connected Web Socket */
	Disposable disposable;

	SubscriptionClientReactiveImpl(Disposable disposable) {
		this.disposable = disposable;
	}

	/** {@inheritDoc} */
	@Override
	public void unsubscribe() throws GraphQLRequestExecutionException {
		try {
			disposable.dispose();
		} catch (Exception e) {
			throw new GraphQLRequestExecutionException(e.getMessage(), e);
		}
	}

}
