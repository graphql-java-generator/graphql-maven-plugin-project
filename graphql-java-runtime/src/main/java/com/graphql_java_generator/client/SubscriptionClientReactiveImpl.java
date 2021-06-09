/**
 * 
 */
package com.graphql_java_generator.client;

import org.springframework.web.reactive.socket.CloseStatus;
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

	/**
	 * The {@link Disposable} That allows to close the underlying {@link Flux}, that receive the subscription
	 * notifications
	 */
	Disposable disposable;

	/** The connected {@link WebSocketSession} */
	WebSocketSession session;

	/**
	 * 
	 * @param disposable
	 *            The {@link Disposable} That allows to close the underlying {@link Flux}, that receive the subscription
	 *            notifications
	 * @param session
	 *            The connected {@link WebSocketSession}
	 */
	public SubscriptionClientReactiveImpl(Disposable disposable, WebSocketSession session) {
		this.disposable = disposable;
		this.session = session;
	}

	@Override
	public void unsubscribe() throws GraphQLRequestExecutionException {
		try {
			if (session != null) {
				session.close(CloseStatus.NORMAL);
				session = null;
			}
			if (disposable != null) {
				disposable.dispose();
				disposable = null;
			}
		} catch (Exception e) {
			throw new GraphQLRequestExecutionException(e.getMessage(), e);
		}
	}

	@Override
	public WebSocketSession getSession() {
		return session;
	}

}
