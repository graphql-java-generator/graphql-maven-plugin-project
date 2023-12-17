package org.allGraphQLCases.subscription;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.allGraphQLCases.client.util.TheSubscriptionTypeExecutorAllGraphQLCases;

import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

public class SubscribeToADate implements Runnable {
	final TheSubscriptionTypeExecutorAllGraphQLCases subscriptionExecutor;
	final SubscriptionCallbackGeneric<Date> callback;
	final String clientName;
	final Date date;

	public SubscribeToADate(TheSubscriptionTypeExecutorAllGraphQLCases executor, String clientName, Date date) {
		this.subscriptionExecutor = executor;
		this.clientName = clientName;
		this.callback = new SubscriptionCallbackGeneric<>(clientName);
		this.date = date;
	}

	@Override
	public void run() {
		try {
			SubscriptionClient sub = this.subscriptionExecutor.issue53("", this.callback, this.date);

			// Let's wait a max of 20 second, until we receive some notifications
			// (20s will never occur... unless using the debugger to undebug some stuff)
			this.callback.latchForMessageReception.await(10, TimeUnit.SECONDS);

			// Let's disconnect from the subscription
			sub.unsubscribe();
		} catch (GraphQLRequestExecutionException | GraphQLRequestPreparationException | InterruptedException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}