/**
 * 
 */
package com.graphql_java_generator.samples.forum.test.client.subscription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Member;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Post;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.PostInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.TopicPostInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.util.GraphQLRequest;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.util.MutationExecutor;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.util.SubscriptionExecutor;
import com.graphql_java_generator.samples.forum.client.subscription.PostSubscriptionCallback;
import com.graphql_java_generator.samples.forum.test.SpringTestConfig;

import jakarta.annotation.PostConstruct;

/**
 * This class executes integration tests for subscription, and can be used as a sample on how to use Subscription, based
 * on this plugin
 * 
 * @author etienne-sf
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SpringTestConfig.class })
@TestPropertySource("classpath:application.properties")
class SubscriptionIT {

	/** The logger for this class */
	static protected Logger logger = LoggerFactory.getLogger(SubscriptionIT.class);

	@Autowired
	SubscriptionExecutor subscriptionType;

	@Autowired
	MutationExecutor mutationType;

	GraphQLRequest subscriptionRequest;
	GraphQLRequest createPostRequest;
	PostSubscriptionCallback callback;

	public static Thread currentThread;

	public static class SubscriptionThread extends Thread {
		SubscriptionClient client;
		final SubscriptionIT subscriptionIT;

		SubscriptionThread(SubscriptionIT subscriptionIT) {
			this.subscriptionIT = subscriptionIT;
		}

		@Override
		public void run() {
			logger.debug("Subscribing to the GraphQL subscription");
			try {
				client = subscriptionIT.subscriptionType.subscribeToNewPost(subscriptionIT.subscriptionRequest,
						subscriptionIT.callback, "Board name 1");
			} catch (GraphQLRequestExecutionException e) {
				String msg = e.getMessage() + " while running SubscriptionThread";
				logger.error(msg);
				throw new RuntimeException(msg, e);
			}
		}

	}

	@PostConstruct
	public void init() throws GraphQLRequestPreparationException {
		subscriptionRequest = subscriptionType
				.getSubscribeToNewPostGraphQLRequest("{id date author publiclyAvailable title content}");
		createPostRequest = mutationType
				.getCreatePostGraphQLRequest("{id date author{id} title content publiclyAvailable}");
		currentThread = Thread.currentThread();
	}

	@Test
	void testSubscription() throws GraphQLRequestExecutionException, InterruptedException, ExecutionException {
		// Preparation
		logger.debug("--------------------------------------------------------------------------------------------");
		logger.info("Starting testSubscription");

		// Go, go, go

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// There may be bugs in server implementation, that don't were properly on the second subscription. Let's check
		// that by having two iterations of the same test

		execSubscriptionTest(1);
		execSubscriptionTest(2);

		logger.debug("Stopped listening");
	}

	/**
	 * @param iteration
	 * @throws InterruptedException
	 * @throws GraphQLRequestExecutionException
	 */
	private void execSubscriptionTest(int iteration) throws InterruptedException, GraphQLRequestExecutionException {
		callback = new PostSubscriptionCallback();
		Member author = new Member();
		author.setId("12");
		PostInput postInput = new PostInput();
		postInput.setTopicId("22");
		postInput.setInput(getTopicPostInput(author, "Some other content",
				new GregorianCalendar(2000, 11 - 1, 21).getTime(), false, "The good title for a post"));
		// To avoid some locks in this test, the subscription is done in another thread
		logger.debug("Actual start of the test (1)");
		SubscriptionThread subscriptionThread = new SubscriptionThread(this);
		// subscriptionThread.start();
		subscriptionThread.run();

		// We wait a little, just to be sure that the subscription is active on server side
		logger.debug("Before Thread.sleep() (iteration {})", iteration);
		Thread.sleep(500);

		Post createdPost = mutationType.createPost(createPostRequest, postInput);

		// Let's wait for the notifications (my PC is really slow, thanks to a permanently full scanning antivirus)
		callback.latchNewMessage.await(20, TimeUnit.SECONDS);// Time out of 20s: can be useful, when debugging

		// Verification
		logger.debug("Before checks", iteration);
		assertNull(callback.lastReceivedClose, "We should have received no close message (" + callback.lastReceivedClose
				+ ") for iteration " + iteration);
		assertNull(callback.lastReceivedError,
				"We should have received no error (" + callback.lastReceivedError + ") for iteration " + iteration);
		assertEquals(1, callback.nbReceivedMessages,
				"We should have received exactly one notification, for iteration " + iteration);
		assertNotNull(callback.lastReceivedMessage, "We should have received a post, for iteration " + iteration);

		assertEquals(createdPost.getId(), callback.lastReceivedMessage.getId(),
				"Is it 'our' new Post?, for iteration " + iteration);
		assertEquals(new GregorianCalendar(2000, 11 - 1, 21).getTime(), callback.lastReceivedMessage.getDate(),
				"Check of a custom scalar date, for iteration " + iteration);

		// We must free the server resource at the end
		subscriptionThread.client.unsubscribe();
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
