package org.allGraphQLCases.subscription;

import java.util.Date;

import org.allGraphQLCases.client.util.TheSubscriptionTypeExecutorAllGraphQLCases;

import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

public class SubscribeToADate implements Runnable {
	final TheSubscriptionTypeExecutorAllGraphQLCases subscriptionExecutor;
	final SubscriptionCallbackToADate callback;
	final String clientName;
	final Date date;

	public SubscribeToADate(TheSubscriptionTypeExecutorAllGraphQLCases executor, String clientName, Date date) {
		this.subscriptionExecutor = executor;
		this.clientName = clientName;
		this.callback = new SubscriptionCallbackToADate(clientName);
		this.date = date;
	}

	@Override
	public void run() {
		try {
			SubscriptionClient sub = subscriptionExecutor.issue53("", callback, date);

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