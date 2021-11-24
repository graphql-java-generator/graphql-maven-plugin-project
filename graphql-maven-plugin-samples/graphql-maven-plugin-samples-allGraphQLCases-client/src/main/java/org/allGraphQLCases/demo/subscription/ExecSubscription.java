/**
 * 
 */
package org.allGraphQLCases.demo.subscription;

import java.util.concurrent.TimeUnit;

import org.allGraphQLCases.client.util.TheSubscriptionTypeExecutorAllGraphQLCases;
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
	TheSubscriptionTypeExecutorAllGraphQLCases subscriptionExecutor;

	SubscriptionCallbackListInteger callback = new SubscriptionCallbackListInteger();

	public void exec()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		SubscriptionClient sub = subscriptionExecutor.subscribeToAList("", callback);

		// Let's wait 10 seconds max, to receive some notifications (the latch is freed when the callback receives 10
		// notifications)
		callback.latchFor10Notifications.await(10, TimeUnit.SECONDS);

		// Let's disconnect from the subscription
		System.out.println("Let's unsubscribe");
		sub.unsubscribe();
	}

}
