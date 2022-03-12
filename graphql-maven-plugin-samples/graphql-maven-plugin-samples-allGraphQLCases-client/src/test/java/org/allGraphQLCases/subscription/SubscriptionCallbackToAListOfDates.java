/**
 * 
 */
package org.allGraphQLCases.subscription;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphql_java_generator.client.SubscriptionCallback;

/**
 * This class will receive the items returned by the "subscribeToAList" subscription
 * 
 * @author etienne-sf
 */
public class SubscriptionCallbackToAListOfDates implements SubscriptionCallback<List<Date>> {

	/** The logger for this class */
	static protected Logger logger = LoggerFactory.getLogger(SubscriptionCallbackToAListOfDates.class);

	final String clientName;
	boolean hasReceveivedAMessage = false;
	public List<Date> lastReceivedMessage = null;
	public Throwable lastReceivedError = null;

	/** A latch that will be freed when a the first notification arrives for this subscription */
	public CountDownLatch latchForMessageReception = new CountDownLatch(1);

	public SubscriptionCallbackToAListOfDates(String clientName) {
		this.clientName = clientName;
	}

	@Override
	public void onConnect() {
		logger.debug("The subscription is connected (for {})", clientName);
	}

	@Override
	public void onMessage(List<Date> t) {
		logger.debug("Received this list from the 'subscribeToAList' subscription: {} (for {})", t, clientName);
		hasReceveivedAMessage = true;
		lastReceivedMessage = t;
		latchForMessageReception.countDown();
	}

	@Override
	public void onClose(int statusCode, String reason) {
		logger.debug("The subscription is closed (for {})", clientName);
	}

	@Override
	public void onError(Throwable cause) {
		lastReceivedError = cause;
		logger.error("Oups! An error occurred: " + cause.getMessage());
		latchForMessageReception.countDown();
	}

}
