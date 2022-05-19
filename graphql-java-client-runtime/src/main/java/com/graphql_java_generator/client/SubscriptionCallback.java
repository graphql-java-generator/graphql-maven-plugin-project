/**
 * 
 */
package com.graphql_java_generator.client;

/**
 * This interface will receive the notification for each message that comes from a subscription. The message sent by the
 * server is mapped on the T class.
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
	 * This method is called once the subscription has been submitted to the GraphQL server. It's an information call:
	 * no special action is expected.<br/>
	 * Please note that, since 2.x version, the plugin has no access to the Web Socket connection information. So it is
	 * not possible to know, when the subscription is really active (that is: from when the event will actually be sent
	 * to the client)
	 */
	public void onConnect();

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
	 *            since 2.0 release, this parameter is always 0
	 * @param reason
	 *            since 2.0 release, this parameter is always null
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
