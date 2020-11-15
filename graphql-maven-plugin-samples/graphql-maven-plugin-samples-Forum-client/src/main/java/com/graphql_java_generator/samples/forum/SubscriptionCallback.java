/**
 * 
 */
package com.graphql_java_generator.samples.forum;

import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

/**
 * This interface will receive the notification for the connecton, and then for each message that comes from a
 * subscription. The message sent by the server is mapped on the T class.<BR/>
 * <B>Important notice</B>: the implementation should store the session that is received by the
 * {@link #onConnect(WebSocketSession)} method. This allows to later call the {@link WebSocketSession#close()} method.
 * This will close the web socket, and release its resources, when no further processing of the subscription is needed.
 * 
 * @param <T>
 *            The java class that maps to the GraphQL type returned by the subscription.<BR/>
 *            For instance, for the subscription the T parameter should be <I>Human</I>
 * 
 *            <PRE>
 *            subscribeNewHumanForEpisode(episode: Episode!): Human!
 *            </PRE>
 * 
 * @author etienne-sf
 */
public interface SubscriptionCallback<T> {

	/**
	 * This method is called once the subscription has been submitted to the GraphQL server. <BR/>
	 * <B>Important notice</B>: the implementation should store this session, to be able to call the
	 * {@link WebSocketSession#close()} method. This will close the web socket, and release its resources, when no
	 * further processing of the subscription is needed.
	 * 
	 * @param session
	 *            The newly created {@link WebSocketSession}
	 */
	public void onConnect(WebSocketSession session);

	/**
	 * This method is called each time a message is sent by the server, for this subscription. It's an information call:
	 * no special action is expected.
	 * 
	 * @param t
	 * @see {@link OnWebSocketMessage}
	 */
	public void onMessage(T t);

	/**
	 * A callback to make the program aware of the end of the subscription channel. It's an information call: no special
	 * action is expected.
	 * 
	 * @param statusCode
	 * @param reason
	 * @see {@link OnWebSocketClose}
	 */
	public void onClose(int statusCode, String reason);

	/**
	 * Whenever an error occurs, at any time of the subscription processing. It's an information call: no special action
	 * is expected.
	 * 
	 * @param cause
	 * @see {@link OnWebSocketError}
	 */
	public void onError(Throwable cause);

}
