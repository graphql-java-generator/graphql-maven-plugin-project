/**
 * 
 */
package com.graphql_java_generator.samples.forum.client.subscription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.GraphQLRequest;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Member;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.MutationType;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Post;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.PostInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.SubscriptionType;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.TopicPostInput;

/**
 * This class executes integration tests for subscription, and can be used as a sample on how to use Subscription, based
 * on this plugin
 * 
 * @author etienne-sf
 */
class SubscriptionIT {

	/** The logger for this class */
	static protected Logger logger = LoggerFactory.getLogger(SubscriptionIT.class);

	SubscriptionType subscriptionType;
	MutationType mutationType;
	GraphQLRequest subscriptionRequest;
	GraphQLRequest createPostRequest;

	public static Thread currentThread;

	SubscriptionIT() throws GraphQLRequestPreparationException {
		subscriptionType = new SubscriptionType("http://localhost:8180/graphql/subscription");
		mutationType = new MutationType("http://localhost:8180/graphql");
		subscriptionRequest = subscriptionType
				.getSubscribeToNewPostGraphQLRequest("{id date author publiclyAvailable title content}");
		createPostRequest = mutationType
				.getCreatePostGraphQLRequest("{id date author{id} title content publiclyAvailable}");
		currentThread = Thread.currentThread();
	}

	@Test
	void testSubscription() throws GraphQLRequestExecutionException, InterruptedException, ExecutionException {
		// Preparation
		PostSubscriptionCallback<Post> postSubscriptionCallback = new PostSubscriptionCallback<>();
		Member author = new Member();
		author.setId("00000000-0000-0000-0000-000000000012");
		PostInput postInput = new PostInput();
		postInput.setTopicId("00000000-0000-0000-0000-000000000022");
		postInput.setInput(getTopicPostInput(author, "Some other content",
				new GregorianCalendar(2020, 11 - 1, 21).getTime(), false, "The good title for a post"));

		// Go, go, go
		logger.debug("Subscribing to the GraphQL subscription");
		SubscriptionClient client = subscriptionType.subscribeToNewPost(subscriptionRequest, postSubscriptionCallback,
				"Board name 1");

		logger.debug("Creating the post, for which we should receice the notification");
		CompletableFuture<Post> createdPostASync = CompletableFuture.supplyAsync(() -> {
			try {
				// We need to wait a little, to be sure the subscription is done, before creating the post.
				// But we wait as little as possible
				for (int i = 0; i < 100; i += 1) {
					Thread.sleep(10);
					if (postSubscriptionCallback.connected)
						break;
				}
				return mutationType.createPost(createPostRequest, postInput);
			} catch (GraphQLRequestExecutionException | InterruptedException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		});

		// Let's wait 5 seconds max . We'll get interrupted before, if we receive a notification
		// (see the callback implementation in the PostSubscriptionCallback class)
		try {
			Thread.sleep(5 * 1000);
		} catch (InterruptedException e) {
			logger.debug("Got interrupted");
		}

		// Verification
		assertNull(postSubscriptionCallback.lastReceivedClose,
				"We should have received no close message (" + postSubscriptionCallback.lastReceivedClose + ")");
		assertNull(postSubscriptionCallback.lastReceivedError,
				"We should have received no error (" + postSubscriptionCallback.lastReceivedError + ")");
		assertNotNull(postSubscriptionCallback.lastReceivedMessage, "We should have received a post");

		Post createdPost = createdPostASync.get();
		assertEquals(createdPost.getId(), postSubscriptionCallback.lastReceivedMessage.getId(),
				"Is it 'our' new Post?");
		assertEquals(new GregorianCalendar(2020, 11 - 1, 21).getTime(),
				postSubscriptionCallback.lastReceivedMessage.getDate(), "Check of a custom scalar date");

		// We must free the server resource at the end
		client.unsubscribe();

		logger.debug("Stopped listening");
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
