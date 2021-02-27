/**
 * 
 */
package com.graphql_java_generator.samples.forum.client.subscription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.SpringTestConfig;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.GraphQLRequest;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Member;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.MutationTypeExecutor;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Post;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.PostInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.SubscriptionTypeExecutor;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.TopicPostInput;

/**
 * This class executes integration tests for subscription, and can be used as a sample on how to use Subscription, based
 * on this plugin
 * 
 * @author etienne-sf
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SpringTestConfig.class })
@TestPropertySource("classpath:application.properties")
class SubscriptionNewTestIT {

	/** The logger for this class */
	static protected Logger logger = LoggerFactory.getLogger(SubscriptionIT.class);

	@Autowired
	SubscriptionTypeExecutor subscriptionType;

	@Autowired
	MutationTypeExecutor mutationType;

	GraphQLRequest subscriptionRequest;
	GraphQLRequest createPostRequest;

	public static Thread currentThread;

	@PostConstruct
	public void init() throws GraphQLRequestPreparationException {
		subscriptionRequest = subscriptionType
				.getSubscribeToNewPostGraphQLRequest("{id date author publiclyAvailable title content}");
		createPostRequest = mutationType
				.getCreatePostGraphQLRequest("{id date author{id} title content publiclyAvailable}");
		currentThread = Thread.currentThread();
	}

	/**
	 * In the <A HREF="https://github.com/graphql-java-generator/graphql-maven-plugin-project/issues/72">Issue 72</A>,
	 * woolclew found a bug, when two clients would subscribe. This test checks that this bug doesn't come again
	 * 
	 * @throws GraphQLRequestExecutionException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	void testSubscription_issue72() throws GraphQLRequestExecutionException, InterruptedException, ExecutionException {
		// Preparation
		logger.debug("==============================================================================================");
		logger.info("Starting testSubscription_issue72");

		Post createdPost;
		PostSubscriptionCallback callback1 = new PostSubscriptionCallback();
		PostSubscriptionCallback callback2 = new PostSubscriptionCallback();
		PostSubscriptionCallback callback3 = new PostSubscriptionCallback();

		Member author1 = new Member();
		author1.setId("12");
		PostInput postInput1 = new PostInput();
		postInput1.setTopicId("22");
		postInput1.setInput(getTopicPostInput(author1, "Some other content (1)",
				new GregorianCalendar(2001, 10 - 1, 20).getTime(), false, "The good title for a post (1)"));

		Member author2 = new Member();
		author2.setId("13");
		PostInput postInput2 = new PostInput();
		postInput2.setTopicId("23");
		postInput2.setInput(getTopicPostInput(author2, "Some other content (2)",
				new GregorianCalendar(2002, 9 - 1, 19).getTime(), false, "The good title for a post (2)"));

		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		logger.debug(
				"Creating dummy posts, to be sure we won't receive a notification, latter on, for this one, once subscribed");
		createdPost = mutationType.createPost(createPostRequest, postInput1);
		createdPost = mutationType.createPost(createPostRequest, postInput2);

		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Go, go, go
		logger.debug("--------------------------------------------------------------------------------------------");
		logger.debug("(client 1) Subscribing to the GraphQL subscription");
		SubscriptionClient client1 = subscriptionType.subscribeToNewPost(subscriptionRequest, callback1,
				"Board name 1");
		logger.debug("(client 2) Subscribing to the GraphQL subscription");
		SubscriptionClient client2 = subscriptionType.subscribeToNewPost(subscriptionRequest, callback2,
				"Board name 1");

		// Due to parallel treatments on the same computer during the IT tests, it may happen that the subscription is
		// not totally active yet. So we wait, to let the subscription by plainly active on both the client and the
		// server side.
		SubscriptionIT.waitForEvent(10, () -> {
			return callback1.connected && callback2.connected;
		}, "The first two subscriptions should be active (1&2)");

		// The subscription may be executed a little latter on server side, due to parallel jobs
		Thread.sleep(500); // Wait 0.5s

		logger.debug("Creating the post, for which we should receive the notification");
		createdPost = mutationType.createPost(createPostRequest, postInput1);

		// Let's wait for the notifications (my PC is really slow, thanks to a permanently full scanning antivirus)
		SubscriptionIT.waitForEvent(10, () -> {
			return callback1.lastReceivedMessage != null && callback2.lastReceivedMessage != null;
		}, "The notifications should arrive (1&2)");

		// Verification
		logger.trace("postInput1: {}", postInput1);
		logger.trace("createdPost: {}", createdPost);
		logger.trace("callback1: {}", callback1);
		logger.trace("callback2: {}", callback2);
		logger.trace("Checking callback1");
		checkNotification(callback1, createdPost, postInput1);
		logger.trace("Checking callback2");
		checkNotification(callback2, createdPost, postInput1);

		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Let's do it again (there was an issue that prevent a second notification to arrive)
		logger.debug("--------------------------------------------------------------------------------------------");
		logger.debug("(client 3) Subscribing to the GraphQL subscription");
		SubscriptionClient client3 = subscriptionType.subscribeToNewPost(subscriptionRequest, callback3,
				"Board name 1");
		SubscriptionIT.waitForEvent(4, () -> {
			return callback1.connected && callback2.connected && callback3.connected;
		}, "The three subscriptions should be active (1&2&3)");
		//
		callback1.nbReceivedMessages = 0;
		callback2.nbReceivedMessages = 0;
		callback1.lastReceivedMessage = null;
		callback2.lastReceivedMessage = null;

		createdPost = mutationType.createPost(createPostRequest, postInput2);

		// Let's wait for the notifications (my PC is really slow, thanks to a permanently full scanning antivirus)
		SubscriptionIT.waitForEvent(10, () -> {
			return callback1.lastReceivedMessage != null && callback2.lastReceivedMessage != null
					&& callback3.lastReceivedMessage != null;
		}, "The notifications should arrive (1&2&3)");

		// Verification
		logger.trace("Checking callback1");
		checkNotification(callback1, createdPost, postInput2);
		logger.trace("Checking callback2");
		checkNotification(callback2, createdPost, postInput2);
		logger.trace("Checking callback3");
		checkNotification(callback3, createdPost, postInput2);

		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		// We must free the server resource at the end
		client1.unsubscribe();
		client2.unsubscribe();
		client3.unsubscribe();

		logger.debug("Stopped listening (the IT test has properly finished)");
		logger.debug("--------------------------------------------------------------------------------------------");
	}

	private void checkNotification(PostSubscriptionCallback callback, Post createdPost, PostInput postInput) {
		assertNull(callback.lastReceivedClose,
				"We should have received no close message (" + callback.lastReceivedClose + ")");
		assertNull(callback.lastReceivedError, "We should have received no error (" + callback.lastReceivedError + ")");
		assertEquals(1, callback.nbReceivedMessages, "We should have received exactly one notification");
		assertNotNull(callback.lastReceivedMessage, "We should have received a post");

		assertEquals(createdPost.getId(), callback.lastReceivedMessage.getId(), "Is it 'our' new Post?");
		assertEquals(createdPost.getId(), callback.lastReceivedMessage.getId(), "Is it 'our' new Post?");
		assertEquals(postInput.getInput().getDate(), callback.lastReceivedMessage.getDate(),
				"Check of a custom scalar date");
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
