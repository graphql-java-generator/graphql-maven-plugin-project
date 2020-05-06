/**
 * 
 */
package com.graphql_java_generator.samples.forum.client.subscription;

import java.util.ArrayList;
import java.util.List;

import javax.websocket.ClientEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphql_java_generator.client.SubscriptionCallback;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Post;

/**
 * @author etienne-sf
 */
// The class that'll receive the notification from the GraphQL subscription
@ClientEndpoint
public class ManualPostSubscriptionCallback implements SubscriptionCallback<Post> {
	/** The logger for this instance */
	static protected Logger logger = LoggerFactory.getLogger(ManualPostSubscriptionCallback.class);

	List<Post> receivedMessages = new ArrayList<>();
	List<String> receivedCloses = new ArrayList<>();
	List<Throwable> receivedErrors = new ArrayList<>();

	@Override
	public void onConnect() {
		// No action
	}

	@Override
	public void onMessage(Post t) {
		logger.debug("Received Post: {}", t.toString());
		receivedMessages.add(t);

		// We want the test to be as quick as possible. So we interrupt the main thread, as soon as we receive the test
		// message
		ManualSubscriptionTest.currentThread.interrupt();
	}

	@Override
	public void onClose(int statusCode, String reason) {
		logger.debug("Received onClose: {}", statusCode + "-" + reason);
		receivedCloses.add(statusCode + "-" + reason);
		ManualSubscriptionTest.currentThread.interrupt();
	}

	@Override
	public void onError(Throwable cause) {
		logger.debug("Received onError: {}" + cause.getMessage());
		receivedErrors.add(cause);
		ManualSubscriptionTest.currentThread.interrupt();
	}
}
