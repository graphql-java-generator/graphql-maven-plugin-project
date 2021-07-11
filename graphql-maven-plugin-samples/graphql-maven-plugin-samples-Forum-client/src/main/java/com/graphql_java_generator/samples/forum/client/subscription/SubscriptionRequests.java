package com.graphql_java_generator.samples.forum.client.subscription;

import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.GraphQLRequest;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Member;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.MutationTypeExecutor;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Post;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.PostInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.SubscriptionTypeExecutor;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.TopicPostInput;

@Component
public class SubscriptionRequests {

	@Autowired
	MutationTypeExecutor mutationTypeExecutor;
	@Autowired
	SubscriptionTypeExecutor subscriptionTypeExecutor;

	/** The constructor used, when this class is loaded as a Spring bean (useless if there is no other constructor) */
	@Autowired
	public SubscriptionRequests() {
		// No action.
	}

	/**
	 * The constructor to use, when not in a Spring context. To remove, when in a Spring app
	 * 
	 * @param url
	 *            The url for the GraphQL endpoint
	 * @param urlSubscription
	 *            The url for the GraphQL subscription endpoint (may be different, when the server is under java)
	 */
	public SubscriptionRequests(String url) {
		mutationTypeExecutor = new MutationTypeExecutor(url);
		subscriptionTypeExecutor = new SubscriptionTypeExecutor(url);
	}

	public void execSubscription()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, IOException {
		// Preparation
		GraphQLRequest subscriptionRequest = subscriptionTypeExecutor
				.getSubscribeToNewPostGraphQLRequest("{id date author publiclyAvailable title content}");
		GraphQLRequest createPostRequest = mutationTypeExecutor
				.getCreatePostGraphQLRequest("{id date author{id} title content publiclyAvailable}");

		PostSubscriptionCallback postSubscriptionCallback = new PostSubscriptionCallback();
		Member author = new Member();
		author.setId("12");
		PostInput postInput = new PostInput();
		postInput.setTopicId("22");
		postInput.setInput(getTopicPostInput(author, "Some other content",
				new GregorianCalendar(2020, 11 - 1, 21).getTime(), false, "The good title for a post"));

		// Go, go, go
		System.out.println("Submitting the 'subscribeToNewPostWithBindValues' GraphQL subscription");
		SubscriptionClient subscriptionClient = subscriptionTypeExecutor.subscribeToNewPost(subscriptionRequest,
				postSubscriptionCallback, "Board name 1");

		// For this test, we need to be sure that the subscription is active, before creating the post (that we will
		// receive a notification about). 3s: that's long, but my PC is so slow from time to time... :(
		final int TIMEOUT1 = 3000;
		for (int i = 0; i < TIMEOUT1 / 10; i += 1) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
			if (postSubscriptionCallback.connected) {
				break;
			}
		}

		// Let's check that everything is ready
		if (!postSubscriptionCallback.connected) {
			throw new RuntimeException("The subscription should be active");
		}

		System.out.println(
				"Creating a post (for which we expect a notification) from this postInput: " + postInput.toString());
		mutationTypeExecutor.createPost(createPostRequest, postInput);

		//////////////////////////////////////////////////////////////////////////////////////
		// Let's check that we've received the expected notification, for this post creation

		// Let's wait 10 seconds max for the post creation notification (from the subscription). We'll get interrupted
		// before, as soon as we receive the notification
		// (see the callback implementation in the PostSubscriptionCallback class)
		Post notifiedPost = null;
		// Let's wait 10s max, until the connection is active
		final int TIMEOUT2 = 10;
		for (int i = 0; i < TIMEOUT2 * 1000 / 10; i += 1) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
			if ((notifiedPost = postSubscriptionCallback.lastReceivedMessage) != null) {
				// Ok, we're connected. We're done
				break;
			}
		}
		if (notifiedPost == null) {
			throw new RuntimeException("The notification for the post creation was not received");
		}

		// We need to free the server resources, at the end
		subscriptionClient.unsubscribe();
	}

	private TopicPostInput getTopicPostInput(Member author, String content, Date date, boolean publiclyAvailable,
			String title) {
		TopicPostInput input = new TopicPostInput();
		input.setAuthorId(author.getId());
		input.setContent(content);
		input.setDate(date);
		input.setPubliclyAvailable(publiclyAvailable);
		input.setTitle(title);

		return input;
	}
}
