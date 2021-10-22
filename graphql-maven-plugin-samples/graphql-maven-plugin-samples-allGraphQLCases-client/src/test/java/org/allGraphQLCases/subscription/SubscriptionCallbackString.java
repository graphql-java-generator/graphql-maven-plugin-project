/**
 * 
 */
package org.allGraphQLCases.subscription;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphql_java_generator.client.SubscriptionCallback;

/**
 * This class will receive the items returned by the "subscriptionTest" subscription
 * 
 * @author etienne-sf
 */
public class SubscriptionCallbackString implements SubscriptionCallback<String> {

	/** The logger for this class */
	static protected Logger logger = LoggerFactory.getLogger(SubscriptionCallbackString.class);

	final String clientName;
	public String lastReceivedMessage = null;
	public Throwable lastExceptionReceived = null;
	public boolean closedHasBeenReceived = false;

	/** A latch that will be freed when a the first notification arrives for this subscription */
	public CountDownLatch latchForCompleteReception = new CountDownLatch(1);
	public CountDownLatch latchForErrorReception = new CountDownLatch(1);
	public CountDownLatch latchForMessageReception = new CountDownLatch(1);

	public SubscriptionCallbackString(String clientName) {
		this.clientName = clientName;
	}

	@Override
	public void onConnect() {
		logger.debug("The subscription is connected (for {})", clientName);
	}

	@Override
	public void onMessage(String t) {
		logger.debug("Received this message from the 'subscriptionTest' subscription: {} (for {})", t, clientName);
		lastReceivedMessage = t;
		latchForMessageReception.countDown();
	}

	@Override
	public void onClose(int statusCode, String reason) {
		logger.debug("The subscription is closed (for {})", clientName);
		closedHasBeenReceived = true;
		latchForCompleteReception.countDown();
	}

	@Override
	public void onError(Throwable cause) {
		logger.error("Oups! An error occurred: " + cause.getMessage());
		lastExceptionReceived = cause;
		latchForErrorReception.countDown();
	}

}
