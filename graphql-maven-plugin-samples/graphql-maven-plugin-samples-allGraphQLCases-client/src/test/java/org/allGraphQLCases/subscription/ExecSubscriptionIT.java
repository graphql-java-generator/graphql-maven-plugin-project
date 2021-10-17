package org.allGraphQLCases.subscription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.allGraphQLCases.SpringTestConfig;
import org.allGraphQLCases.client.util.TheSubscriptionTypeExecutorAllGraphQLCases;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.graphql_java_generator.client.GraphQLReactiveWebSocketHandler;
import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

//Adding "webEnvironment = SpringBootTest.WebEnvironment.NONE" avoid this error:
//"No qualifying bean of type 'ReactiveClientRegistrationRepository' available"
//More details here: https://stackoverflow.com/questions/62558552/error-when-using-enablewebfluxsecurity-in-springboot
@SpringBootTest(classes = SpringTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Execution(ExecutionMode.CONCURRENT)
public class ExecSubscriptionIT {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(GraphQLReactiveWebSocketHandler.class);

	@Autowired
	TheSubscriptionTypeExecutorAllGraphQLCases subscriptionExecutor;

	public static class SubscribeToAList implements Runnable {
		final TheSubscriptionTypeExecutorAllGraphQLCases subscriptionExecutor;
		final SubscriptionCallbackListIntegerForTest callback;
		final String clientName;

		SubscribeToAList(TheSubscriptionTypeExecutorAllGraphQLCases executor, String clientName) {
			this.subscriptionExecutor = executor;
			this.clientName = clientName;
			this.callback = new SubscriptionCallbackListIntegerForTest(clientName);
		}

		@Override
		public void run() {
			try {
				SubscriptionClient sub = subscriptionExecutor.subscribeToAList("", callback);

				// Let's wait a max of 20 second, until we receive some notifications
				callback.latchForMessageReception.await(20, TimeUnit.SECONDS);

				// Let's disconnect from the subscription
				sub.unsubscribe();
			} catch (GraphQLRequestExecutionException | GraphQLRequestPreparationException | InterruptedException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	public void test_subscribeToAList()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_subscribeToAList");

		// To test the issue 72, we create two clients for the subscription, and check that each of them properly
		// receives the notifications
		SubscribeToAList client1 = new SubscribeToAList(subscriptionExecutor, "client1");
		SubscribeToAList client2 = new SubscribeToAList(subscriptionExecutor, "client2");
		SubscribeToAList client3 = new SubscribeToAList(subscriptionExecutor, "client3");
		SubscribeToAList client4 = new SubscribeToAList(subscriptionExecutor, "client4");
		SubscribeToAList client5 = new SubscribeToAList(subscriptionExecutor, "client5");
		SubscribeToAList client6 = new SubscribeToAList(subscriptionExecutor, "client6");
		SubscribeToAList client7 = new SubscribeToAList(subscriptionExecutor, "client7");
		SubscribeToAList client8 = new SubscribeToAList(subscriptionExecutor, "client8");
		SubscribeToAList client9 = new SubscribeToAList(subscriptionExecutor, "client9");
		SubscribeToAList client10 = new SubscribeToAList(subscriptionExecutor, "client10");

		Thread thread1 = new Thread(client1);
		Thread thread2 = new Thread(client2);
		Thread thread3 = new Thread(client3);
		Thread thread4 = new Thread(client4);
		Thread thread5 = new Thread(client5);
		Thread thread6 = new Thread(client6);
		Thread thread7 = new Thread(client7);
		Thread thread8 = new Thread(client8);
		Thread thread9 = new Thread(client9);
		Thread thread10 = new Thread(client10);

		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();
		thread5.start();
		thread6.start();
		thread7.start();
		thread8.start();
		thread9.start();
		thread10.start();

		// Let's wait for the end of our two subscription client threads
		thread1.join();
		thread2.join();
		thread3.join();
		thread4.join();
		thread5.join();
		thread6.join();
		thread7.join();
		thread8.join();
		thread9.join();
		thread10.join();

		assertNotNull(client1.callback.lastReceivedMessage, "The client 1 should have received a message");
		assertNotNull(client2.callback.lastReceivedMessage, "The client 2 should have received a message");
		assertNotNull(client3.callback.lastReceivedMessage, "The client 3 should have received a message");
		assertNotNull(client4.callback.lastReceivedMessage, "The client 4 should have received a message");
		assertNotNull(client5.callback.lastReceivedMessage, "The client 5 should have received a message");
		assertNotNull(client6.callback.lastReceivedMessage, "The client 6 should have received a message");
		assertNotNull(client7.callback.lastReceivedMessage, "The client 7 should have received a message");
		assertNotNull(client8.callback.lastReceivedMessage, "The client 8 should have received a message");
		assertNotNull(client9.callback.lastReceivedMessage, "The client 9 should have received a message");
		assertNotNull(client10.callback.lastReceivedMessage, "The client 10 should have received a message");
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	public void test_subscribeToADate_issue53()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_subscribeToADate_issue53");

		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, 02, 01);// Month is 0-based, so this date is 2018, January the first
		Date date1 = cal.getTime();

		cal.clear();
		cal.set(2018, 02, 02);// Month is 0-based, so this date is 2018, January the second
		Date date2 = cal.getTime();

		// To test the issue 53, we create two clients for the subscription, and check that each of them properly
		// receives the notifications
		SubscribeToADate client1 = new SubscribeToADate(subscriptionExecutor, "client1", date1);
		SubscribeToADate client2 = new SubscribeToADate(subscriptionExecutor, "client2", date2);

		Thread thread1 = new Thread(client1);
		Thread thread2 = new Thread(client2);

		thread1.start();
		thread2.start();

		// Let's wait for the end of our two subscription client threads
		thread1.join();
		thread2.join();

		assertEquals(date1, client1.callback.lastReceivedMessage, "The client 1 should have received a message");
		assertEquals(date2, client2.callback.lastReceivedMessage, "The client 2 should have received a message");
	}

	/**
	 * Tests that the graphql-transport-ws 'complete' message is properly propagated from the server to the client, if
	 * it occurs
	 */
	@Execution(ExecutionMode.CONCURRENT)
	@Test
	public void test_subscribeToADate_serverComplete() {
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_subscribeToADate_serverComplete");

		fail("not yet implemented");
	}

	/**
	 * Tests that the graphql-transport-ws 'complete' message is properly propagated from the client to the server, if
	 * it occurs
	 */
	@Execution(ExecutionMode.CONCURRENT)
	@Test
	public void test_subscribeToADate_clientComplete() {
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_subscribeToADate_clientComplete");

		fail("not yet implemented");
	}

	/**
	 * Tests that an error in the subscription is properly sent back to the client, when it occurs during the
	 * subscription
	 */
	@Execution(ExecutionMode.CONCURRENT)
	@Test
	public void test_subscribeToADate_subscriptionError() {
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_subscribeToADate_subscriptionError");

		fail("not yet implemented");
	}

	/**
	 * Tests that an error in the subscription is properly sent back to the client, when it occurs after the
	 * subscription is active (typically when notifications are coming).
	 */
	@Execution(ExecutionMode.CONCURRENT)
	@Test
	public void test_subscribeToADate_nextError() {
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_subscribeToADate_nextError");

		fail("not yet implemented");
	}

	/**
	 * Tests that an error in the subscription is properly sent back to the client, when it occurs if the web socket got
	 * closed
	 */
	@Execution(ExecutionMode.CONCURRENT)
	@Test
	public void test_subscribeToADate_webSocketCloseError() {
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_subscribeToADate_webSocketCloseError");

		fail("not yet implemented");
	}
}
