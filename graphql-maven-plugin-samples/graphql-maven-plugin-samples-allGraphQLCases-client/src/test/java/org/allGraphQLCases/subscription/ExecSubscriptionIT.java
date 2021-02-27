package org.allGraphQLCases.subscription;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.allGraphQLCases.SpringTestConfig;
import org.allGraphQLCases.client.util.TheSubscriptionTypeExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@Execution(ExecutionMode.CONCURRENT)
public class ExecSubscriptionIT {

	ApplicationContext ctx;

	public static class SubscribeToAList implements Runnable {
		final TheSubscriptionTypeExecutor subscriptionExecutor;
		final SubscriptionCallbackListIntegerForTest callback;
		final String clientName;

		SubscribeToAList(TheSubscriptionTypeExecutor executor, String clientName) {
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
		ctx = new AnnotationConfigApplicationContext(SpringTestConfig.class);
		TheSubscriptionTypeExecutor subscriptionExecutor = ctx.getBean(TheSubscriptionTypeExecutor.class);

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
}
