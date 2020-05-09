/**
 * 
 */
package com.graphql_java_generator.samples.forum.client.subscription;

import java.util.ArrayList;
import java.util.List;

import com.graphql_java_generator.client.SubscriptionCallback;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Post;

/**
 * @author etienne-sf
 */
// The class that'll receive the notification from the GraphQL subscription
public class ManualPostSubscriptionCallback implements SubscriptionCallback<Post> {

	List<Post> receivedMessages = new ArrayList<>();
	List<String> receivedCloses = new ArrayList<>();
	List<Throwable> receivedErrors = new ArrayList<>();

	@Override
	public void onConnect() {
		System.out.println("Callback Connected");
	}

	@Override
	public void onMessage(Post t) {
		System.out.println("Received Post: " + t.toString());
		receivedMessages.add(t);
	}

	@Override
	public void onClose(int statusCode, String reason) {
		System.out.println("Received onClose: " + statusCode + "-" + reason);
		receivedCloses.add(statusCode + "-" + reason);
		ManualSubscriptionTest.currentThread.interrupt();
	}

	@Override
	public void onError(Throwable cause) {
		System.out.println("Received onError: " + cause.getMessage());
		receivedErrors.add(cause);
		ManualSubscriptionTest.currentThread.interrupt();
	}
}
