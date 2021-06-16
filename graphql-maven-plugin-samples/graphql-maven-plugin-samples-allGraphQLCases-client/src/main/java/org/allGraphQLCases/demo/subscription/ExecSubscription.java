/**
 * 
 */
package org.allGraphQLCases.demo.subscription;

import org.allGraphQLCases.client.util.TheSubscriptionTypeExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * Sample for the execution of a subscription
 * 
 * @author etienne-sf
 */
@Component
public class ExecSubscription {

	@Autowired
	TheSubscriptionTypeExecutor subscriptionExecutor;

	SubscriptionCallbackListInteger callback = new SubscriptionCallbackListInteger();

	public void exec() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		SubscriptionClient sub = subscriptionExecutor.subscribeToAList("", callback);

		// Let's wait 1 second, to receive some notifications
		try {
			Thread.sleep(3000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		// Let's disconnect from the subscription
		System.out.println("Let's unsubscribe");
		sub.unsubscribe();
	}

}
