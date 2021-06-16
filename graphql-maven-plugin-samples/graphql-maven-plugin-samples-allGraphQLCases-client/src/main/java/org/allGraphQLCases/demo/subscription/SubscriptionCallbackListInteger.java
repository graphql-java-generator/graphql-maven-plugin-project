/**
 * 
 */
package org.allGraphQLCases.demo.subscription;

import java.util.List;

import com.graphql_java_generator.client.SubscriptionCallback;

/**
 * This class will receive the items returned by the "subscribeToAList" subscription
 * 
 * @author etienne-sf
 */
public class SubscriptionCallbackListInteger implements SubscriptionCallback<List<Integer>> {

	@Override
	public void onConnect() {
		System.out.println("The subscription is connected");
	}

	@Override
	public void onMessage(List<Integer> t) {
		System.out.println("Received this list from the 'subscribeToAList' subscription: " + t);
	}

	@Override
	public void onClose(int statusCode, String reason) {
		System.out.println("The subscription is closed");
	}

	@Override
	public void onError(Throwable cause) {
		System.out.println("Oups! An error occurred: " + cause.getMessage());
	}

}
