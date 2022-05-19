/**
 * 
 */
package com.graphql_java_generator.client;

import org.springframework.web.reactive.socket.WebSocketSession;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;

/**
 * Default implementation for the {@link SubscriptionClient} interface.<br/>
 * Since 2.x release, this class is quite useless. A {@link Disposable} should be used instead. This class is kept for
 * compatibility with existing code.
 * 
 * @author etienne-sf
 */
public class SubscriptionClientReactiveImpl<T> implements SubscriptionClient {

	/** The {@link Disposable} obtained when subscribing to the {@link #flux} */
	final Disposable disposable;

	/**
	 * 
	 * @param disposable
	 *            The {@link Disposable} That allows to close the underlying {@link Flux}, that receive the subscription
	 *            notifications
	 * @param webSocketHandler
	 *            The connected {@link WebSocketSession}
	 */
	public SubscriptionClientReactiveImpl(Disposable disposable) {
		this.disposable = disposable;
	}

	@Override
	public void unsubscribe() throws GraphQLRequestExecutionException {
		disposable.dispose();
	}

}
