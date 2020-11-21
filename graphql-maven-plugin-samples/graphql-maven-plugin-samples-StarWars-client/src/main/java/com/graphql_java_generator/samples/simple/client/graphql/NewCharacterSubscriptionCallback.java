/**
 * 
 */
package com.graphql_java_generator.samples.simple.client.graphql;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.generated.graphql.Character;
import com.graphql_java_generator.samples.simple.client.SubscriptionCallback;

/**
 * @author etienne-sf
 */
// The class that'll receive the notification from the GraphQL subscription

public class NewCharacterSubscriptionCallback implements SubscriptionCallback<Character> {

	/** The logger for this class */
	static protected Logger logger = LoggerFactory.getLogger(NewCharacterSubscriptionCallback.class);

	/** The web socket session. Allows to close the web socket */
	WebSocketSession session;

	/** Indicates whether the Web Socket is connected or not */
	boolean connected = false;

	Character lastReceivedMessage = null;
	String lastReceivedClose = null;
	Throwable lastReceivedError = null;

	@Override
	public void onConnect(WebSocketSession session) {
		this.session = session;
		this.connected = true;
		System.out.println(
				"The 'subscribeToNewPostWithBindValues' subscription is now active (the web socket is connected)");
	}

	@Override
	public void onMessage(Character t) {
		this.lastReceivedMessage = t;
		// Do something useful with it
		System.out.println(
				"Received a notification from the 'subscribeToNewPostWithBindValues' subscription, for this post: "
						+ t.toString());
	}

	@Override
	public void onClose(int statusCode, String reason) {
		connected = false;
		lastReceivedClose = statusCode + "-" + reason;
		logger.debug("Received onClose: {}", lastReceivedClose);
	}

	@Override
	public void onError(Throwable cause) {
		connected = false;
		lastReceivedError = cause;
		logger.debug("Received onError: {}", cause);
	}

	/**
	 * Closes the web socket. This also ends the subscription: no more messages will be received.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (session != null) {
			session.close();
			session = null;
		}
	}

}
