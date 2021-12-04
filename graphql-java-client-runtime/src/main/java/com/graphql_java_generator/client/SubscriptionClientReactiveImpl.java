/**
 * 
 */
package com.graphql_java_generator.client;

import org.springframework.web.reactive.socket.WebSocketSession;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;

/**
 * Default implementation for the {@link SubscriptionClient} interface.
 * 
 * @author etienne-sf
 */
public class SubscriptionClientReactiveImpl implements SubscriptionClient {

	/** The connected {@link WebSocketSession} */
	final GraphQLReactiveWebSocketHandler session;

	/** The unique id the identify each operation, as specified by the graphql-transport-ws protocol */
	final String uniqueIdOperation;

	/**
	 * 
	 * @param disposable
	 *            The {@link Disposable} That allows to close the underlying {@link Flux}, that receive the subscription
	 *            notifications
	 * @param webSocketHandler
	 *            The connected {@link WebSocketSession}
	 */
	public SubscriptionClientReactiveImpl(String uniqueIdOperation, GraphQLReactiveWebSocketHandler webSocketHandler) {
		this.uniqueIdOperation = uniqueIdOperation;
		this.session = webSocketHandler;
	}

	@Override
	public void unsubscribe() throws GraphQLRequestExecutionException {
		session.unsubscribe(uniqueIdOperation);
	}

	@Override
	public WebSocketSession getSession() {
		return session.getSession();
	}

}
