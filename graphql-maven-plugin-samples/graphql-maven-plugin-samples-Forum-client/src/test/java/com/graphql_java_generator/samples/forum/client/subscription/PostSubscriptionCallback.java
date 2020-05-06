/**
 * 
 */
package com.graphql_java_generator.samples.forum.client.subscription;

import javax.websocket.ClientEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphql_java_generator.client.SubscriptionCallback;

/**
 * @author etienne-sf
 */
// The class that'll receive the notification from the GraphQL subscription
@ClientEndpoint
public class PostSubscriptionCallback<T> implements SubscriptionCallback<T> {

	/** The logger for this class */
	static protected Logger logger = LoggerFactory.getLogger(PostSubscriptionCallback.class);

	T lastReceivedPost = null;
	String lastReceivedClose = null;
	Throwable lastReceivedError = null;

	@Override
	public void onMessage(T t) {
		this.lastReceivedPost = t;
		logger.debug("Received {} {}", t.getClass().getSimpleName(), t);
		SubscriptionIT.currentThread.interrupt();
	}

	@Override
	public void onClose(int statusCode, String reason) {
		lastReceivedClose = statusCode + "-" + reason;
		logger.debug("Received onClose: {}", lastReceivedClose);
	}

	@Override
	public void onError(Throwable cause) {
		lastReceivedError = cause;
		logger.debug("Received onError: {}", cause);
	}

}
