/**
 * 
 */
package com.graphql_java_generator.mavenplugin.samples.simple.client.subscription;

import javax.websocket.ClientEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generated.graphql.Character;
import com.graphql_java_generator.client.SubscriptionCallback;

/**
 * @author etienne-sf
 */
// The class that'll receive the notification from the GraphQL subscription
@ClientEndpoint
public class CharacterSubscriptionCallback implements SubscriptionCallback<Character> {

	/** The logger for this instance */
	static protected Logger logger = LoggerFactory.getLogger(CharacterSubscriptionCallback.class);

	/** Indicates whether the Web Socket is connected or not */
	boolean connected = false;

	Character lastReceivedMessage = null;
	String lastReceivedClose = null;
	Throwable lastReceivedError = null;

	@Override
	public void onConnect() {
		connected = true;
	}

	@Override
	public void onMessage(Character t) {
		this.lastReceivedMessage = t;
		logger.debug("Received {} {}", t.getClass().getSimpleName(), t);
		SubscriptionIT.currentThread.interrupt();
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

}
