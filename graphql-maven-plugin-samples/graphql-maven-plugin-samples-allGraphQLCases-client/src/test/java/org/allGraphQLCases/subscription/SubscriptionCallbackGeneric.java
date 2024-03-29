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
public class SubscriptionCallbackGeneric<T> implements SubscriptionCallback<T> {

	/** The logger for this class */
	static protected Logger logger = LoggerFactory.getLogger(SubscriptionCallbackGeneric.class);

	final String clientName;
	public boolean hasReceveivedAMessage = false;// Becomes true as soon as a message is received
	public T lastReceivedMessage = null;// The value of the last message: should be true for the
	public Throwable lastExceptionReceived = null;
	public boolean closedHasBeenReceived = false;
	public String closureReason = null;

	/** A latch that will be freed when a the first notification arrives for this subscription */
	public CountDownLatch latchForCompleteReception = new CountDownLatch(1);
	public CountDownLatch latchForErrorReception = new CountDownLatch(1);
	public CountDownLatch latchForMessageReception = new CountDownLatch(1);

	public SubscriptionCallbackGeneric(String clientName) {
		this.clientName = clientName;
	}

	@Override
	public void onConnect() {
		logger.debug("The subscription is connected (for {})", this.clientName);
	}

	@Override
	public synchronized void onMessage(T t) {
		logger.debug("Received this message from subscription: {} (for {})", t, this.clientName);
		this.hasReceveivedAMessage = true;
		this.lastReceivedMessage = t;
		this.latchForMessageReception.countDown();
	}

	@Override
	public void onClose(int statusCode, String reason) {
		logger.debug("The subscription is closed (for {})", this.clientName);
		this.closedHasBeenReceived = true;
		this.closureReason = reason;
		this.latchForCompleteReception.countDown();
	}

	@Override
	public void onError(Throwable cause) {
		logger.error("Oups! An error occurred: " + cause.getMessage() + " (for " + this.clientName + ")");
		this.lastExceptionReceived = cause;
		this.latchForErrorReception.countDown();
		this.latchForMessageReception.countDown();
	}

}
