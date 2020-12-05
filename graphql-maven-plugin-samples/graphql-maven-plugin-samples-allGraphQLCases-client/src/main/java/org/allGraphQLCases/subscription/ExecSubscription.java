/**
 * 
 */
package org.allGraphQLCases.subscription;

import org.allGraphQLCases.Main;
import org.allGraphQLCases.client.util.TheSubscriptionTypeExecutor;

import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * Sample for the execution of a subscription
 * 
 * @author etienne-sf
 */
public class ExecSubscription {

	TheSubscriptionTypeExecutor subscriptionExecutor;
	SubscriptionCallbackListInteger callback = new SubscriptionCallbackListInteger();

	public ExecSubscription() {
		subscriptionExecutor = new TheSubscriptionTypeExecutor(Main.GRAPHQL_ENDPOINT + "/subscription");
	}

	public void exec() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		SubscriptionClient sub = subscriptionExecutor.subscribeToAList("", callback);

		// Let's wait 1 second, to receive some notifications
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		// Let's disconnect from the subscription
		sub.unsubscribe();
	}

}
