/**
 * 
 */
package com.graphql_java_generator.client;

import org.springframework.web.reactive.socket.WebSocketSession;

import com.graphql_java_generator.client.GraphQLReactiveWebSocketHandler.WebSocketSessionHandler;
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
	final WebSocketSessionHandler webSocketHandler;

	/** The unique id the identify each operation, as specified by the graphql-transport-ws protocol */
	final String uniqueIdOperation;

	/**
	 * 
	 * @param disposable
	 *            The {@link Disposable} That allows to close the underlying {@link Flux}, that receive the subscription
	 *            notifications
	 * @param webSocketSessionHandler
	 *            The connected {@link WebSocketSession}
	 */
	public SubscriptionClientReactiveImpl(String uniqueIdOperation, WebSocketSessionHandler webSocketSessionHandler) {
		this.uniqueIdOperation = uniqueIdOperation;
		this.webSocketHandler = webSocketSessionHandler;
	}

	@Override
	public void unsubscribe() throws GraphQLRequestExecutionException {
		webSocketHandler.unsubscribe(uniqueIdOperation);
	}

	@Override
	public WebSocketSession getSession() {
		return webSocketHandler.getSession();
	}

}
