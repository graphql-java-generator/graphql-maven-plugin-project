/**
 * 
 */
package org.allGraphQLCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BooleanSupplier;

import org.allGraphQLCases.AliasesIT.HumanSubscriptionCallback;
import org.allGraphQLCases.client.CEP_EnumWithReservedJavaKeywordAsValues_CES;
import org.allGraphQLCases.client.MyQueryTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.client.TheSubscriptionTypeExecutorAllGraphQLCases;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.graphql_java_generator.client.SubscriptionCallback;
import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * Various tests to check queries and subscription that returns enums, as the spring-graphql needs specific code in
 * Controller's to manage enum values that are java keywords
 * 
 * @author etienne-sf
 */
// Adding "webEnvironment = SpringBootTest.WebEnvironment.NONE" avoid this error:
// "No qualifying bean of type 'ReactiveClientRegistrationRepository' available"
// More details here: https://stackoverflow.com/questions/62558552/error-when-using-enablewebfluxsecurity-in-springboot
@SpringBootTest(classes = SpringTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Execution(ExecutionMode.CONCURRENT)
public class EnumIT {

	/** The logger for this class */
	static protected Logger logger = LoggerFactory.getLogger(EnumIT.class);

	@Autowired
	MyQueryTypeExecutorAllGraphQLCases queryType;
	@Autowired
	TheSubscriptionTypeExecutorAllGraphQLCases subscriptionExecutor;

	public static class SubscriptionCallbackImpl<T> implements SubscriptionCallback<T> {
		private static Logger logger = LoggerFactory.getLogger(HumanSubscriptionCallback.class);

		public boolean connected = false;
		public List<T> messages = new ArrayList<>();
		public Throwable lastError;

		@Override
		public void onConnect() {
			this.connected = true;
			logger.debug("Subscription connected");
		}

		@Override
		public void onMessage(T t) {
			this.messages.add(t);
			logger.debug("Message received: {}", t);
		}

		@Override
		public void onClose(int statusCode, String reason) {
			logger.debug("Subscription closed");
		}

		@Override
		public void onError(Throwable error) {
			this.lastError = error;
			logger.error("Subscription on error: {}", error);
		}
	}

	@Test
	void testQueriesThatReturnEnum() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		assertNull(this.queryType.returnEnum(""));
		assertEquals(CEP_EnumWithReservedJavaKeywordAsValues_CES._assert, this.queryType.returnMandatoryEnum(""));
		assertNull(this.queryType.returnListOfEnums(""));
		compareList(//
				Arrays.asList(//
						Arrays.asList(CEP_EnumWithReservedJavaKeywordAsValues_CES._boolean,
								(CEP_EnumWithReservedJavaKeywordAsValues_CES) null,
								CEP_EnumWithReservedJavaKeywordAsValues_CES._break), //
						null, //
						Arrays.asList(CEP_EnumWithReservedJavaKeywordAsValues_CES._default, null,
								CEP_EnumWithReservedJavaKeywordAsValues_CES._implements)), //
				this.queryType.returnListOfListOfEnums(""), //
				"returnListOfListOfEnums");
		assertNull(this.queryType.returnListOfMandatoryEnums(""));
		compareList(//
				Arrays.asList(CEP_EnumWithReservedJavaKeywordAsValues_CES._boolean, null,
						CEP_EnumWithReservedJavaKeywordAsValues_CES._break),
				this.queryType.returnMandatoryListOfEnums(""), //
				"returnMandatoryListOfEnums");
		compareList(//
				Arrays.asList(CEP_EnumWithReservedJavaKeywordAsValues_CES._byte,
						CEP_EnumWithReservedJavaKeywordAsValues_CES._case),
				this.queryType.returnMandatoryListOfMandatoryEnums(""), //
				"returnMandatoryListOfMandatoryEnums");
	}

	@SuppressWarnings("unchecked")
	@Test
	void testSubscriptionsThatReturnEnum()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {

		// enumWithReservedJavaKeywordAsValues
		SubscriptionCallbackImpl<CEP_EnumWithReservedJavaKeywordAsValues_CES> callback1 = new SubscriptionCallbackImpl<>();
		execSubscription(this.subscriptionExecutor.enumWithReservedJavaKeywordAsValues("", callback1), callback1);
		//
		assertEquals(CEP_EnumWithReservedJavaKeywordAsValues_CES._instanceof, callback1.messages.get(0));
		assertNull(callback1.messages.get(1));

		// listOfEnumWithReservedJavaKeywordAsValues
		SubscriptionCallbackImpl<List<CEP_EnumWithReservedJavaKeywordAsValues_CES>> callback2 = new SubscriptionCallbackImpl<>();
		execSubscription(this.subscriptionExecutor.listOfEnumWithReservedJavaKeywordAsValues("", callback2), callback2);
		//
		assertIterableEquals(//
				Arrays.asList(//
						CEP_EnumWithReservedJavaKeywordAsValues_CES._int,
						CEP_EnumWithReservedJavaKeywordAsValues_CES._interface,
						CEP_EnumWithReservedJavaKeywordAsValues_CES._long, //
						null), //
				callback2.messages.get(0));
		assertNull(callback2.messages.get(1));

		// returnEnum
		SubscriptionCallbackImpl<CEP_EnumWithReservedJavaKeywordAsValues_CES> callback3 = new SubscriptionCallbackImpl<>();
		assertNull(execSubscription(this.subscriptionExecutor.returnEnum("", callback3), callback3));

		// returnMandatoryEnum
		SubscriptionCallbackImpl<CEP_EnumWithReservedJavaKeywordAsValues_CES> callback4 = new SubscriptionCallbackImpl<>();
		assertEquals(//
				CEP_EnumWithReservedJavaKeywordAsValues_CES._assert, //
				execSubscription(this.subscriptionExecutor.returnMandatoryEnum("", callback4,
						CEP_EnumWithReservedJavaKeywordAsValues_CES._assert), callback4));

		// returnListOfEnums
		SubscriptionCallbackImpl<List<CEP_EnumWithReservedJavaKeywordAsValues_CES>> callback5 = new SubscriptionCallbackImpl<>();
		assertNull(execSubscription(this.subscriptionExecutor.returnListOfEnums("", callback5), callback5));

		// returnListOfMandatoryEnums
		SubscriptionCallbackImpl<List<CEP_EnumWithReservedJavaKeywordAsValues_CES>> callback6 = new SubscriptionCallbackImpl<>();
		assertNull(execSubscription(this.subscriptionExecutor.returnListOfMandatoryEnums("", callback6), callback6));

		// returnMandatoryListOfEnums
		SubscriptionCallbackImpl<List<CEP_EnumWithReservedJavaKeywordAsValues_CES>> callback7 = new SubscriptionCallbackImpl<>();
		compareList(//
				Arrays.asList(CEP_EnumWithReservedJavaKeywordAsValues_CES._boolean, null,
						CEP_EnumWithReservedJavaKeywordAsValues_CES._break),
				(List<CEP_EnumWithReservedJavaKeywordAsValues_CES>) execSubscription(
						this.subscriptionExecutor.returnMandatoryListOfEnums("", callback7), callback7), //
				"returnMandatoryListOfEnums");

		// returnListOfListOfEnums
		SubscriptionCallbackImpl<List<List<CEP_EnumWithReservedJavaKeywordAsValues_CES>>> callback8 = new SubscriptionCallbackImpl<>();
		compareList(//
				Arrays.asList(//
						Arrays.asList(CEP_EnumWithReservedJavaKeywordAsValues_CES._boolean, null,
								CEP_EnumWithReservedJavaKeywordAsValues_CES._break), //
						null, //
						Arrays.asList(CEP_EnumWithReservedJavaKeywordAsValues_CES._default, null,
								CEP_EnumWithReservedJavaKeywordAsValues_CES._implements)), //
				(List<List<CEP_EnumWithReservedJavaKeywordAsValues_CES>>) execSubscription(
						this.subscriptionExecutor.returnListOfListOfEnums("", callback8), callback8), //
				"returnListOfListOfEnums");

		// returnMandatoryListOfMandatoryEnums
		SubscriptionCallbackImpl<List<CEP_EnumWithReservedJavaKeywordAsValues_CES>> callback9 = new SubscriptionCallbackImpl<>();
		compareList(//
				Arrays.asList(//
						CEP_EnumWithReservedJavaKeywordAsValues_CES._byte, //
						CEP_EnumWithReservedJavaKeywordAsValues_CES._case),
				(List<CEP_EnumWithReservedJavaKeywordAsValues_CES>) execSubscription(
						this.subscriptionExecutor.returnMandatoryListOfMandatoryEnums("", callback9), callback9), //
				"returnMandatoryListOfMandatoryEnums");
	}

	/**
	 * @param expected1
	 * @param actual1
	 * @param test
	 */
	private void compareList(List<?> expected1, List<?> actual1, String test) {
		assertEquals(expected1.size(), actual1.size());
		for (int i = 0; i < expected1.size(); i += 1) {
			if (expected1.get(i) instanceof List) {
				assertInstanceOf(List.class, actual1.get(i), test + "[" + i + "] instanceof");
				compareList((List<?>) expected1.get(i), (List<?>) actual1.get(i), test + "[" + i + "]");
			} else {
				assertEquals(expected1.get(i), actual1.get(i), "[" + test + "] Comparison of item " + i);
			}
		} // for
	}

	/**
	 * Wait for as long as the given delay, but will return as soon as the test is ok. If the given delay expires, then
	 * this method fails
	 * 
	 * @param nbSeconds
	 * @param test
	 * @param expectedEvent
	 */
	public static void waitForEvent(int nbSeconds, BooleanSupplier test, String expectedEvent) {
		logger.debug("Starting to wait for '{}'", expectedEvent);
		int increment = 20;
		for (int i = 0; i < nbSeconds * 1000 / increment; i += 1) {
			if (test.getAsBoolean()) {
				// The condition is met. Let's return to the caller.
				logger.debug("Finished waiting for '{}' (the condition is met)", expectedEvent);
				return;
			}
			try {
				Thread.sleep(increment);
			} catch (InterruptedException e) {
				logger.trace("got interrupted");
			}
		}

		// Too bad...
		String msg = "The delay has expired, when waiting for '" + expectedEvent + "'";
		logger.error(msg);
		fail(msg);
	}

	/**
	 * - Wait until two messages are received from the subscription,<br/>
	 * - Unsubscribe from it <br/>
	 * - Returns the first notification received for this subscription.
	 * 
	 * @param sub
	 *            The {@link SubscriptionClient} generated by the execution of the subscription to test
	 * @throws GraphQLRequestExecutionException
	 * @throws GraphQLRequestPreparationException
	 * @throws InterruptedException
	 */
	Object execSubscription(SubscriptionClient sub, SubscriptionCallbackImpl<?> callback)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {

		// Let's wait a max of 10 seconds, until we receive some notifications
		waitForEvent(10, () -> {
			return callback.messages.size() >= 2 || callback.lastError != null;
		}, "Waiting for the subscription to receive the notification");

		// Let's disconnect from the subscription
		sub.unsubscribe();

		// Verification
		if (callback.lastError != null) {
			fail("The subscription raised this error: " + callback.lastError);
		}

		return callback.messages.get(0);
	}
}