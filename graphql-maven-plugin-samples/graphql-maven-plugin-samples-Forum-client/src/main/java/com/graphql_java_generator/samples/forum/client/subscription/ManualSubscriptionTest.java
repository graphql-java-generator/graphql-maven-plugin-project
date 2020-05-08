/**
 * 
 */
package com.graphql_java_generator.samples.forum.client.subscription;

import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.SubscriptionType;

/**
 * This class allows to manually test subscription. It subscribes to the GraphQL subscription subscribeToNewPost defined
 * in the forum GraphQL schema.<BR/>
 * Then:
 * <UL>
 * <LI>Start the Forum server (by executing its generated GraphQLMain java class)</LI>
 * <LI>Execute this class</LI>
 * <LI>Create Posts by using graphiql, available at this URL:
 * <A HREF="http://localhost:8180/graphiql">http://localhost:8180/graphiql</A> once you start the Forum server</A></LI>
 * </UL>
 * And you'll see the newly created posts beeing displayed by this class, as it subscribed to the subscribeToNewPost
 * GraphQL subscription.
 * 
 * 
 * @author etienne-sf
 */
public class ManualSubscriptionTest {

	SubscriptionType subscriptionType = new SubscriptionType("http://localhost:8180/graphql/subscription");
	public static Thread currentThread;

	public static void main(String... args) throws Exception {
		new ManualSubscriptionTest().exec();
	}

	ManualSubscriptionTest() throws GraphQLRequestPreparationException {
		currentThread = Thread.currentThread();
	}

	private void exec() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		System.out.println("Subscribing to the GraphQL subscription");
		SubscriptionClient client = subscriptionType.subscribeToNewPost(
				"{id date author publiclyAvailable title content}", new ManualPostSubscriptionCallback(),
				"Board name 1");

		// Let's wait 10 minutes (600 seconds), so that we display the received notifications during this time
		try {
			Thread.sleep(600 * 1000);
		} catch (InterruptedException e) {
			System.out.println("Got interrupted");
		}

		client.unsubscribe();
		System.out.println("Stopped listening");
	}

}
