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

import org.forum.generated.Member;
import org.forum.generated.Post;
import org.forum.generated.PostInput;
import org.forum.generated.TopicPostInput;
import org.forum.generated.util.GraphQLRequest;
import org.forum.generated.util.MutationExecutor;
import org.forum.generated.util.SubscriptionExecutor;
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
		PostInput postInput = PostInput.builder()//
				.withTopicId("22")//
				.withInput(getTopicPostInput(author, "Some other content",
						new GregorianCalendar(2000, 11 - 1, 21).getTime(), false, "The good title for a post"))
				.build();

		// Go, go, go
		logger.debug("Subscribing to the GraphQL subscription");
		SubscriptionClient client = subscriptionType.subscribeToNewPost(subscriptionRequest, callback, "Board name 1");

		// We wait a little, just to be sure that the subscription is active on server side
		for (int i = 0; i < 100; i += 1) {
			Post createdPost = mutationType.createPost(createPostRequest, postInput);
			Thread.yield();
			Thread.sleep(10);
			if (callback.lastReceivedMessage != null) {
				// We've received a notification: we're done with waiting for a notification.
				break;
			}
		}

		// Verification
		assertNull(callback.lastReceivedClose,
				"We should have received no close message (" + callback.lastReceivedClose + ")");
		assertNull(callback.lastReceivedError, "We should have received no error (" + callback.lastReceivedError + ")");
		assertEquals(1, callback.nbReceivedMessages, "We should have received exactly one notification");
		assertNotNull(callback.lastReceivedMessage, "We should have received a post");

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
