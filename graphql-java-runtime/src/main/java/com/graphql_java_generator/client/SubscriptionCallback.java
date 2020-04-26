/**
 * 
 */
package com.graphql_java_generator.client;

import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

/**
 * This interface will receive the notification for each message that comes from a subscription. The message sent by the
 * server is mapped on the T class.
 * 
 * @author etienne-sf
 */
public interface SubscriptionCallback<T> {

	/**
	 * This method is called each time a message is sent by the server, for this subscription.
	 * 
	 * @param t
	 * @see {@link OnWebSocketMessage}
	 */
	public void onMessage(T t);

	/**
	 * A callback to make the program aware of the end of the subscription channel
	 * 
	 * @param statusCode
	 * @param reason
	 * @see {@link OnWebSocketClose}
	 */
	public void onClose(int statusCode, String reason);

	/**
	 * Whenever an error occurs, at any time of the subscription processing
	 * 
	 * @param cause
	 * @see {@link OnWebSocketError}
	 */
	public void onError(Throwable cause);

}
