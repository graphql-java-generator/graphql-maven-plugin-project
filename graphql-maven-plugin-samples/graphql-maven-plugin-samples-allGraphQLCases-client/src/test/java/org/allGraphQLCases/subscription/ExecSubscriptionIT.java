package org.allGraphQLCases.subscription;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.allGraphQLCases.Main;
import org.allGraphQLCases.client.util.TheSubscriptionTypeExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@Execution(ExecutionMode.CONCURRENT)
public class ExecSubscriptionIT {

	TheSubscriptionTypeExecutor subscriptionExecutor;
	SubscriptionCallbackListIntegerForTest callback = new SubscriptionCallbackListIntegerForTest();

	@BeforeEach
	public void setup() {
		subscriptionExecutor = new TheSubscriptionTypeExecutor(Main.GRAPHQL_ENDPOINT + "/subscription");
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	public void test_subscribeToAList() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		SubscriptionClient sub = subscriptionExecutor.subscribeToAList("", callback);

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

		assertNotNull(callback.lastReceivedMessage, "We should have received a message");
	}
}
