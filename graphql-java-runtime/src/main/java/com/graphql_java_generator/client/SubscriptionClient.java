/**
 * 
 */
package com.graphql_java_generator.client;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * Now useless, with the Spring reactive implementation. All interactions with the Web Socket are managed by the
 * {@link SubscriptionCallback}.<BR/>
 * This interface allows the application to interact with the subscription, once it has subscribed to it. The currently
 * only available action is
 * 
 * @author etienne-sf
 */
@Deprecated
public interface SubscriptionClient {

	/**
	 * Allows the client application to unsubscribe from a previously subscribed subscription. No more notification will
	 * be sent for this subscription. <BR/>
	 * This will free resources on both the client and the server.
	 * 
	 * @throws GraphQLRequestExecutionException
	 * 
	 */
	void unsubscribe() throws GraphQLRequestExecutionException;

}
