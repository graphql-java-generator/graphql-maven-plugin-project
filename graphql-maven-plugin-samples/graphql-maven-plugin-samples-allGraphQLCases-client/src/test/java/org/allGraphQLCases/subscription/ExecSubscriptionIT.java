package org.allGraphQLCases.subscription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Calendar;
import java.util.Date;

import org.allGraphQLCases.SpringTestConfig;
import org.allGraphQLCases.client.util.TheSubscriptionTypeExecutorAllGraphQLCases;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

//Adding "webEnvironment = SpringBootTest.WebEnvironment.NONE" avoid this error:
//"No qualifying bean of type 'ReactiveClientRegistrationRepository' available"
//More details here: https://stackoverflow.com/questions/62558552/error-when-using-enablewebfluxsecurity-in-springboot
@SpringBootTest(classes = SpringTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Execution(ExecutionMode.CONCURRENT)
public class ExecSubscriptionIT {

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

				try {
					Thread.sleep(500); // Wait 0.5 second, so that other thread is ready
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}

				// Let's wait a max of 1 second, until we receive some notifications
				try {
					for (int i = 1; i < 10; i += 1) {
						if (callback.lastReceivedMessage != null)
							break;
						Thread.sleep(100); // Wait 0.1 second
					} // for
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}

				// Let's disconnect from the subscription
				sub.unsubscribe();
			} catch (GraphQLRequestExecutionException |

					GraphQLRequestPreparationException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	public void test_subscribeToAList()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {

		// To test the issue 72, we create two clients for the subscription, and check that each of them properly
		// receives the notifications
		SubscribeToAList client1 = new SubscribeToAList(subscriptionExecutor, "client1");
		SubscribeToAList client2 = new SubscribeToAList(subscriptionExecutor, "client2");

		Thread thread1 = new Thread(client1);
		Thread thread2 = new Thread(client2);

		thread1.start();
		thread2.start();

		// Let's wait for the end of our two subscription client threads
		thread1.join();
		thread2.join();

		assertNotNull(client1.callback.lastReceivedMessage, "The client 1 should have received a message");
		assertNotNull(client2.callback.lastReceivedMessage, "The client 2 should have received a message");
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	public void test_subscribeToADate_issue53()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {

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
}
