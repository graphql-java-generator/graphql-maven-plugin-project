package com.graphql_java_generator.samples.forum.client.subscription;

import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

import org.forum.generated.Member;
import org.forum.generated.Post;
import org.forum.generated.PostInput;
import org.forum.generated.TopicPostInput;
import org.forum.generated.util.GraphQLRequest;
import org.forum.generated.util.MutationExecutor;
import org.forum.generated.util.SubscriptionExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@Component
public class SubscriptionRequests {

	@Autowired
	MutationExecutor mutationTypeExecutor;
	@Autowired
	SubscriptionExecutor subscriptionTypeExecutor;

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

		//////////////////////////////////////////////////////////////////////////////////////
		// Let's check that we've received the expected notification, for this post creation

		// Let's wait 10 seconds max for the post creation notification (from the subscription). We'll get interrupted
		// before, as soon as we receive the notification
		// (see the callback implementation in the PostSubscriptionCallback class)
		Post notifiedPost = null;
		// Let's wait 10s max, until the connection is active
		final int TIMEOUT2 = 10;// In seconds
		final int STEP = 50;// In ms
		for (int i = 0; i < TIMEOUT2 * 1000 / STEP; i += 1) {
			try {
				Thread.sleep(STEP);

				// Let's create a post, and store it in a map: we'll display the created post that matches the first
				// received notification (as we can't guess when the subscription is really active on server side)
				mutationTypeExecutor.createPost(createPostRequest, postInput);
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
