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
import java.util.concurrent.TimeUnit;

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
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.MutationExecutor;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Post;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.PostInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.SubscriptionExecutor;
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
class SubscriptionIT {

	/** The logger for this class */
	static protected Logger logger = LoggerFactory.getLogger(SubscriptionIT.class);

	@Autowired
	SubscriptionExecutor subscriptionType;

	@Autowired
	MutationExecutor mutationType;

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

	@Test
	void testSubscription() throws GraphQLRequestExecutionException, InterruptedException, ExecutionException {
		// Preparation
		logger.debug("--------------------------------------------------------------------------------------------");
		logger.info("Starting testSubscription");
		PostSubscriptionCallback callback = new PostSubscriptionCallback();
		Member author = new Member();
		author.setId("12");
		PostInput postInput = new PostInput();
		postInput.setTopicId("22");
		postInput.setInput(getTopicPostInput(author, "Some other content",
				new GregorianCalendar(2000, 11 - 1, 21).getTime(), false, "The good title for a post"));

		// Go, go, go
		logger.debug("Subscribing to the GraphQL subscription");
		SubscriptionClient client = subscriptionType.subscribeToNewPost(subscriptionRequest, callback, "Board name 1");

		// We wait a little, just to be sure that the subscription is active on server side
		Thread.sleep(500);

		Post createdPost = mutationType.createPost(createPostRequest, postInput);

		// Let's wait for the notifications (my PC is really slow, thanks to a permanently full scanning antivirus)
		callback.latchNewMessage.await(20, TimeUnit.SECONDS);// Time out of 20s: can be useful, when debugging

		// Verification
		assertNull(callback.lastReceivedClose,
				"We should have received no close message (" + callback.lastReceivedClose + ")");
		assertNull(callback.lastReceivedError, "We should have received no error (" + callback.lastReceivedError + ")");
		assertEquals(1, callback.nbReceivedMessages, "We should have received exactly one notification");
		assertNotNull(callback.lastReceivedMessage, "We should have received a post");

		assertEquals(createdPost.getId(), callback.lastReceivedMessage.getId(), "Is it 'our' new Post?");
		assertEquals(new GregorianCalendar(2000, 11 - 1, 21).getTime(), callback.lastReceivedMessage.getDate(),
				"Check of a custom scalar date");

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
