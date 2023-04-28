/**
 * 
 */
package com.graphql_java_generator.samples.forum.client.subscription;

import org.forum.generated.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphql_java_generator.client.SubscriptionCallback;

/**
 * @author etienne-sf
 */
// The class that'll receive the notification from the GraphQL subscription

public class PostSubscriptionCallback implements SubscriptionCallback<Post> {

	/** The logger for this class */
	static protected Logger logger = LoggerFactory.getLogger(PostSubscriptionCallback.class);

	/** Indicates whether the Web Socket is connected or not */
	boolean connected = false;

	int nbReceivedMessages = 0;
	Post lastReceivedMessage = null;
	String lastReceivedClose = null;
	Throwable lastReceivedError = null;

	@Override
	public void onConnect() {
		this.connected = true;
		logger.debug("'onConnect' received for the 'subscribeToNewPost' subscription");
	}

	@Override
	public void onMessage(Post t) {
		logger.debug("Received a notification from the 'subscribeToNewPost' subscription, for this post {} ", t);
		nbReceivedMessages += 1;
		lastReceivedMessage = t;

		// Do something useful with it
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