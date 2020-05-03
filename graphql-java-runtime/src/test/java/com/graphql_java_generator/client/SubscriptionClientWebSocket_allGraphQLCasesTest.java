package com.graphql_java_generator.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.graphql_java_generator.client.domain.allGraphQLCases.Droid;
import com.graphql_java_generator.client.domain.allGraphQLCases.Human;
import com.graphql_java_generator.client.domain.allGraphQLCases.TheSubscriptionType;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

class SubscriptionClientWebSocket_allGraphQLCasesTest {

	/**
	 * A test class that just stores the calls to the onXxx in the xxxs lists, to check that the callback methods are
	 * properly called
	 */
	class SubscriptionCallbackTestImpl implements SubscriptionCallback<Human> {
		/** messages will receive every message sent to the {@link #onMessage(TheSubscriptionType)} */
		List<Human> messages = new ArrayList<>();
		/** closes will receive an entry for each call to {@link #onClose(int, String)}, as int-String */
		List<String> closes = new ArrayList<>();
		/** errors will receive every call to {@link #onError(Throwable)} */
		List<Throwable> errors = new ArrayList<>();

		@Override
		public void onMessage(Human t) {
			messages.add(t);
		}

		@Override
		public void onClose(int statusCode, String reason) {
			closes.add(statusCode + "-" + reason);
		}

		@Override
		public void onError(Throwable cause) {
			errors.add(cause);
		}
	}// SubscriptionCallbackTestImpl

	final static String subscriptionName = "subscriptionName";
	SubscriptionCallbackTestImpl subscriptionCallback;
	SubscriptionClientWebSocket<TheSubscriptionType, Human> subscriptionClientWebSocket;

	@BeforeEach
	void setUp() {
		subscriptionCallback = new SubscriptionCallbackTestImpl();
		String subscriptionRequest = null;// No request execution in this test
		subscriptionClientWebSocket = new SubscriptionClientWebSocket<TheSubscriptionType, Human>(subscriptionRequest,
				"subscribeNewHumanForEpisode", subscriptionCallback, TheSubscriptionType.class, Human.class);

		// Preparation
		assertEquals(0, subscriptionCallback.messages.size(), "we start with no message");
		assertEquals(0, subscriptionCallback.closes.size(), "we start with nothing closed");
		assertEquals(0, subscriptionCallback.errors.size(), "we start with no error");
	}

	@Test
	void testOnMessage() throws GraphQLRequestExecutionException {

		// Go, go, go
		subscriptionClientWebSocket.onMessage("" //
				+ "{" //
				+ "    \"subscribeNewHumanForEpisode\": {" //
				+ "      \"id\": \"7ecfeafa-98e0-4072-922d-f94fa7d07aa0\"," //
				+ "      \"name\": \"human's name\"," //
				+ "      \"homePlanet\": \"a planet\"," //
				+ "      \"__typename\": \"Human\"," //
				+ "      \"friends\": ["//
				+ "        {"//
				+ "          \"id\": \"7ecfeafa-98e0-4072-922d-f94fa7d07aa1\"," //
				+ "          \"name\": \"friends 1\"," //
				+ "          \"homePlanet\": \"planet1\"," //
				+ "          \"__typename\": \"Human\"" //
				+ "        },"//
				+ "        {"//
				+ "          \"id\": \"7ecfeafa-98e0-4072-922d-f94fa7d07aa2\"," //
				+ "          \"name\": \"friends 2\"," //
				+ "          \"primaryFunction\": \"function2\"," //
				+ "          \"__typename\": \"Droid\"" //
				+ "        }"//
				+ "      ]" //
				+ "    }" //
				+ "}");

		// Verification
		assertEquals(1, subscriptionCallback.messages.size());
		assertEquals(0, subscriptionCallback.closes.size());
		assertEquals(0, subscriptionCallback.errors.size());

		Human h = subscriptionCallback.messages.get(0);
		assertEquals("7ecfeafa-98e0-4072-922d-f94fa7d07aa0", h.getId());
		assertEquals("human's name", h.getName());
		assertEquals("a planet", h.getHomePlanet());
		assertEquals(2, h.getFriends().size());
		//
		int i = 0;
		assertTrue(h.getFriends().get(i) instanceof Human);
		Human friend1 = (Human) h.getFriends().get(i);
		assertEquals("7ecfeafa-98e0-4072-922d-f94fa7d07aa1", friend1.getId());
		assertEquals("friends 1", friend1.getName());
		assertEquals("planet1", friend1.getHomePlanet());
		assertEquals(null, friend1.getFriends());
		//
		i += 1;
		assertTrue(h.getFriends().get(i) instanceof Droid);
		Droid friend2 = (Droid) h.getFriends().get(i);
		assertEquals("7ecfeafa-98e0-4072-922d-f94fa7d07aa2", friend2.getId());
		assertEquals("friends 2", friend2.getName());
		assertEquals("function2", friend2.getPrimaryFunction());
		assertEquals(null, friend2.getFriends());
	}

	@Test
	void testOnClose() {

		// Go, go, go
		subscriptionClientWebSocket.onClose(666, "no reason");

		// Verification
		assertEquals(0, subscriptionCallback.messages.size());
		assertEquals(1, subscriptionCallback.closes.size());
		assertEquals(0, subscriptionCallback.errors.size());

		assertEquals("666-no reason", subscriptionCallback.closes.get(0));
	}

	@Test
	void testOnError() {

		// Go, go, go
		subscriptionClientWebSocket.onError(new GraphQLRequestExecutionException("This is a test"));

		// Verification
		assertEquals(0, subscriptionCallback.messages.size());
		assertEquals(0, subscriptionCallback.closes.size());
		assertEquals(1, subscriptionCallback.errors.size());

		assertEquals(GraphQLRequestExecutionException.class.getName(),
				subscriptionCallback.errors.get(0).getClass().getName());
		assertEquals("This is a test", subscriptionCallback.errors.get(0).getMessage());
	}

}
