/**
 * 
 */
package com.graphql_java_generator.samples.forum.client;

import java.io.IOException;

import javax.websocket.ClientEndpoint;
import javax.websocket.EncodeException;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import com.graphql_java_generator.client.SubscriptionCallback;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Post;

/**
 * @author etienne-sf
 */
// The class that'll receive the notification from the GraphQL subscription
@ClientEndpoint
public class PostSubscriptionCallback implements SubscriptionCallback<Post> {

	@OnOpen
	public void onOpen(final Session session) throws IOException, EncodeException {
		session.getBasicRemote().sendObject("Hello!");
	}

	@Override
	@OnMessage
	public void onMessage(Post t) {
		System.out.println(t);
	}

	@Override
	public void onClose(int statusCode, String reason) {
		System.out.println("onClose: " + statusCode + "-" + reason);
		ManualSubscriptionTest.currentThread.interrupt();
	}

	@Override
	public void onError(Throwable cause) {
		System.out.println("ERROR: " + cause);
		ManualSubscriptionTest.currentThread.interrupt();
	}
}
