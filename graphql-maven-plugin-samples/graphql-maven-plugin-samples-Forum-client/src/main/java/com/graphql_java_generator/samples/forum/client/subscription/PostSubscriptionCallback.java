/**
 * 
 */
package com.graphql_java_generator.samples.forum.client.subscription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphql_java_generator.client.SubscriptionCallback;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Post;

/**
 * @author etienne-sf
 */
// The class that'll receive the notification from the GraphQL subscription

public class PostSubscriptionCallback implements SubscriptionCallback<Post> {

	/** The logger for this class */
	static protected Logger logger = LoggerFactory.getLogger(PostSubscriptionCallback.class);

	/** Indicates whether the Web Socket is connected or not */
	boolean connected = false;

	Post lastReceivedMessage = null;
	String lastReceivedClose = null;
	Throwable lastReceivedError = null;

	@Override
	public void onConnect() {
		this.connected = true;
		System.out.println(
				"The 'subscribeToNewPostWithBindValues' subscription is now active (the web socket is connected)");
	}

	@Override
	public void onMessage(Post t) {
		this.lastReceivedMessage = t;
		// Do something useful with it
		logger.debug(
				"Received a notification from the 'subscribeToNewPostWithBindValues' subscription, for this post {} ",
				t);
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

}
