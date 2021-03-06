/**
 * 
 */
package org.allGraphQLCases.subscription;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphql_java_generator.client.SubscriptionCallback;

/**
 * This class will receive the items returned by the "subscribeToAList" subscription
 * 
 * @author etienne-sf
 */
public class SubscriptionCallbackListIntegerForTest implements SubscriptionCallback<List<Integer>> {

	/** The logger for this class */
	static protected Logger logger = LoggerFactory.getLogger(SubscriptionCallbackListIntegerForTest.class);

	final String clientName;
	public List<Integer> lastReceivedMessage = null;

	public SubscriptionCallbackListIntegerForTest(String clientName) {
		this.clientName = clientName;
	}

	@Override
	public void onConnect() {
		logger.debug("The subscription is connected (for {})", clientName);
	}

	@Override
	public void onMessage(List<Integer> t) {
		logger.debug("Received this list from the 'subscribeToAList' subscription: {} (for {})", t, clientName);
		lastReceivedMessage = t;
	}

	@Override
	public void onClose(int statusCode, String reason) {
		logger.debug("The subscription is closed (for {})", clientName);
	}

	@Override
	public void onError(Throwable cause) {
		logger.error("Oups! An error occurred: " + cause.getMessage());
	}

}
