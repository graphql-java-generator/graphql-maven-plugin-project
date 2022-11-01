/**
 * 
 */
package org.allGraphQLCases.subscription;

import java.util.concurrent.CountDownLatch;

import org.allGraphQLCases.client.CTP_AllFieldCases_CTS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphql_java_generator.client.SubscriptionCallback;

/**
 * This class will receive the items returned by the "allGraphQLCasesInput" and "allGraphQLCasesParam" subscriptions
 * 
 * @author etienne-sf
 */
public class SubscriptionCallbackToAllFieldCases implements SubscriptionCallback<CTP_AllFieldCases_CTS> {

	/** The logger for this class */
	static protected Logger logger = LoggerFactory.getLogger(SubscriptionCallbackToAllFieldCases.class);

	final String clientName;
	public String closureReason = null;
	public CTP_AllFieldCases_CTS lastReceivedMessage = null;
	public Throwable lastReceivedError = null;

	/** A latch that will be freed when a the first notification arrives for this subscription */
	public CountDownLatch latchForMessageReception = new CountDownLatch(1);
	public CountDownLatch latchForClosure = new CountDownLatch(1);

	public SubscriptionCallbackToAllFieldCases(String clientName) {
		this.clientName = clientName;
	}

	@Override
	public void onConnect() {
		logger.debug("The subscription is connected (for {})", clientName);
	}

	@Override
	public void onMessage(CTP_AllFieldCases_CTS t) {
		logger.debug("Received this list from the 'subscribeToAList' subscription: {} (for {})", t, clientName);
		lastReceivedMessage = t;
		latchForMessageReception.countDown();
	}

	@Override
	public void onClose(int statusCode, String reason) {
		logger.debug("The subscription is closed (for {})", clientName);
		closureReason = reason;
		latchForClosure.countDown();
	}

	@Override
	public void onError(Throwable cause) {
		lastReceivedError = cause;
		logger.error("Oups! An error occurred: " + cause.getClass().getSimpleName() + ": " + cause.getMessage());
		latchForMessageReception.countDown();
	}

}
