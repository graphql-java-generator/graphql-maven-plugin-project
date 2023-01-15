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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
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
@Execution(ExecutionMode.CONCURRENT)
class SubscriptionIT {

	/** The logger for this class */
	static protected Logger logger = LoggerFactory.getLogger(SubscriptionIT.class);

	@Autowired
	SubscriptionTypeExecutor subscriptionType;

	@Autowired
	MutationTypeExecutor mutationType;

	GraphQLRequest subscriptionRequest;
	GraphQLRequest createHumanRequest;

	SubscriptionClient client;

	@PostConstruct
	void init() throws GraphQLRequestPreparationException, KeyManagementException, NoSuchAlgorithmException {

		subscriptionRequest = subscriptionType.getNewCharacterGraphQLRequest("{id name ... on Human {homePlanet}}");
		createHumanRequest = mutationType.getCreateHumanGraphQLRequest("{id name homePlanet}");

		// We avoid SSL check in Web Sockets.
		System.setProperty("com.graphql-java-generator.websocket.nosslcheck", "true");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testSubscription() throws GraphQLRequestExecutionException, InterruptedException, ExecutionException {
		// Preparation
		CharacterSubscriptionCallback callback = new CharacterSubscriptionCallback();

		// For this test, we want to check a unique subscription notification that is the result of an action in this
		// thread. To be sure that everything is ok, we subscribe in another thread, and block this thread one second so
		// that the subscription is active. We can then create a Human, and check that we properly receive its creation
		// notification from the server.
		logger.debug("Subscribing to the GraphQL subscription");
		new Thread() {
			@Override
			public void run() {
				try {
					client = subscriptionType.newCharacter(subscriptionRequest, callback);
				} catch (GraphQLRequestExecutionException e) {
					callback.lastReceivedError = e;
				}
			}
		}.start();

		// Let's wait 1 seconds max, that the subscription is properly connected
		Thread.sleep(5000);// Wait 1s

		logger.debug("Creating the human, for which we should receive the notification");
		Character createdCharacter = mutationType.createHuman(createHumanRequest, "new name", "new home planet");

		// Let's wait for the notifications (my PC is really slow, thanks to a permanently full scanning antivirus)
		callback.latchForMessageReception.await(20, TimeUnit.SECONDS);// Time out of 20s: can be useful, when debugging

		// Verification
		assertNull(callback.lastReceivedClose,
				"We should have received no close message (" + callback.lastReceivedClose + ")");
		assertNull(callback.lastReceivedError, "We should have received no error (" + callback.lastReceivedError + ")");
		assertNotNull(callback.lastReceivedMessage, "We should have received a post");

		assertTrue(createdCharacter instanceof Human);
		assertEquals(createdCharacter.getId(), callback.lastReceivedMessage.getId(), "Is it 'our' new Character?");
		assertEquals("new home planet", ((Human) callback.lastReceivedMessage).getHomePlanet(),
				"Check of the fragment behavior");

		// We must free the server resource at the end
		client.unsubscribe();

		logger.debug("Stopped listening");
	}

}
