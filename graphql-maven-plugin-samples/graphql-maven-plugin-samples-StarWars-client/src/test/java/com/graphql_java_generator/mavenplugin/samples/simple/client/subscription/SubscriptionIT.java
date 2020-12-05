/**
 * 
 */
package com.graphql_java_generator.mavenplugin.samples.simple.client.subscription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;
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

import com.generated.graphql.Character;
import com.generated.graphql.GraphQLRequest;
import com.generated.graphql.Human;
import com.generated.graphql.MutationTypeExecutor;
import com.generated.graphql.SubscriptionTypeExecutor;
import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.mavenplugin.samples.SpringTestConfig;

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
	SubscriptionTypeExecutor subscriptionType;

	@Autowired
	MutationTypeExecutor mutationType;

	GraphQLRequest subscriptionRequest;
	GraphQLRequest createHumanRequest;

	public static Thread currentThread;

	@PostConstruct
	void init() throws GraphQLRequestPreparationException, KeyManagementException, NoSuchAlgorithmException {

		subscriptionRequest = subscriptionType.getNewCharacterGraphQLRequest("{id name ... on Human {homePlanet}}");
		createHumanRequest = mutationType.getCreateHumanGraphQLRequest("{id name homePlanet}");
		currentThread = Thread.currentThread();

		// We avoid SSL check in Web Sockets.
		System.setProperty("com.graphql-java-generator.websocket.nosslcheck", "true");
	}

	@Test
	void testSubscription() throws GraphQLRequestExecutionException, InterruptedException, ExecutionException {
		// Preparation
		CharacterSubscriptionCallback callback = new CharacterSubscriptionCallback();

		// Go, go, go
		logger.debug("Subscribing to the GraphQL subscription");
		SubscriptionClient client = subscriptionType.newCharacter(subscriptionRequest, callback);

		logger.debug("Creating the post, for which we should receice the notification");
		CompletableFuture<Character> createdPostASync = CompletableFuture.supplyAsync(() -> {
			try {
				// We need to wait a little, to be sure the subscription is done, before creating the post.
				// But we wait as little as possible
				for (int i = 0; i < 100; i += 1) {
					Thread.sleep(10);
					if (callback.connected)
						break;
				}
				return mutationType.createHuman(createHumanRequest, "new name", "new home planet");
			} catch (GraphQLRequestExecutionException | InterruptedException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		});

		// Let's wait 5 seconds max . We'll get interrupted before, if we receive a notification
		// (see the callback implementation in the CharacterSubscriptionCallback class)
		try {
			Thread.sleep(5 * 1000);
		} catch (InterruptedException e) {
			logger.debug("Got interrupted");
		}

		// Verification
		assertNull(callback.lastReceivedClose,
				"We should have received no close message (" + callback.lastReceivedClose + ")");
		assertNull(callback.lastReceivedError, "We should have received no error (" + callback.lastReceivedError + ")");
		assertNotNull(callback.lastReceivedMessage, "We should have received a post");

		Character createdCharacter = createdPostASync.get();
		assertTrue(createdCharacter instanceof Human);
		assertEquals(createdCharacter.getId(), callback.lastReceivedMessage.getId(), "Is it 'our' new Character?");
		assertEquals("new home planet", ((Human) callback.lastReceivedMessage).getHomePlanet(),
				"Check of the fragment behavior");

		// We must free the server resource at the end
		client.unsubscribe();

		logger.debug("Stopped listening");
	}

}
