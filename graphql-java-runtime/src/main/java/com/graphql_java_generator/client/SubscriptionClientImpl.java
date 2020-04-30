/**
 * 
 */
package com.graphql_java_generator.client;

import org.eclipse.jetty.websocket.client.WebSocketClient;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * Default implementation for the {@link SubscriptionClient} interface.
 * 
 * @author etienne-sf
 */
class SubscriptionClientImpl implements SubscriptionClient {

	/** The connected Web Socket */
	WebSocketClient client;

	SubscriptionClientImpl(WebSocketClient client) {
		this.client = client;
	}

	/** {@inheritDoc} */
	@Override
	public void unsubscribe() throws GraphQLRequestExecutionException {
		try {
			client.stop();
		} catch (Exception e) {
			throw new GraphQLRequestExecutionException(e.getMessage(), e);
		}
	}

}
