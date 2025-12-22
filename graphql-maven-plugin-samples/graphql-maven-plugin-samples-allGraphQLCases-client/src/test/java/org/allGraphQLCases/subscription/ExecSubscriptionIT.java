package org.allGraphQLCases.subscription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.allGraphQLCases.SpringTestConfig;
import org.allGraphQLCases.client.CEP_EnumWithReservedJavaKeywordAsValues_CES;
import org.allGraphQLCases.client.CEP_Episode_CES;
import org.allGraphQLCases.client.CINP_InputWithJson_CINS;
import org.allGraphQLCases.client.CINP_SubscriptionTestParam_CINS;
import org.allGraphQLCases.client.CTP_TypeWithJson_CTS;
import org.allGraphQLCases.client.util.TheSubscriptionTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.client2.TheSubscriptionTypeExecutorAllGraphQLCases2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.client.GraphQlTransportException;

import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

//Adding "webEnvironment = SpringBootTest.WebEnvironment.NONE" avoid this error:
//"No qualifying bean of type 'ReactiveClientRegistrationRepository' available"
//More details here: https://stackoverflow.com/questions/62558552/error-when-using-enablewebfluxsecurity-in-springboot
@SpringBootTest(classes = SpringTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Execution(ExecutionMode.CONCURRENT)
public class ExecSubscriptionIT {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(ExecSubscriptionIT.class);

	@Autowired
	TheSubscriptionTypeExecutorAllGraphQLCases subscriptionExecutor;

	@Autowired
	TheSubscriptionTypeExecutorAllGraphQLCases2 subscriptionExecutor2;

	public static class SubscribeToAList implements Runnable {
		final TheSubscriptionTypeExecutorAllGraphQLCases subscriptionExecutor;
		final SubscriptionCallbackGeneric<List<Integer>> callback;
		final String clientName;

		SubscribeToAList(TheSubscriptionTypeExecutorAllGraphQLCases executor, String clientName) {
			subscriptionExecutor = executor;
			this.clientName = clientName;
			callback = new SubscriptionCallbackGeneric<>(clientName);
		}

		@Override
		public void run() {
			try {
				SubscriptionClient sub = subscriptionExecutor.subscribeToAList("", callback);

				// Let's wait a max of 30 second, until we receive some notifications
				// (80s will never occur... unless using the debugger to undebug some stuff)
				callback.latchForMessageReception.await(80, TimeUnit.SECONDS);

				// Let's disconnect from the subscription
				sub.unsubscribe();
			} catch (GraphQLRequestExecutionException | GraphQLRequestPreparationException | InterruptedException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	public void test_multiSubscribersToAList()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		final int NB_THREADS = 10;
		List<SubscribeToAList> subs = new ArrayList<>(NB_THREADS);
		List<Thread> threads = new ArrayList<>(NB_THREADS);

		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_multiSubscribersToAList");

		// To test the issue 72, we create NB_THREADS clients for the subscription, and check that each of them properly
		// receives the relevant notifications
		for (int i = 0; i < NB_THREADS; i += 1) {
			SubscribeToAList sub = new SubscribeToAList(subscriptionExecutor, "client" + i);
			subs.add(sub);
			threads.add(new Thread(sub));
		}

		// We start the thread only now, so that all these threads execute as possible in the same time
		for (Thread thread : threads) {
			thread.start();
		}

		logger.debug("All {} threads have been started", threads.size());

		// Let's wait for the end of all our subscription client threads
		for (Thread thread : threads) {
			thread.join();
		}

		logger.debug("All {} threads have finished", threads.size());

		// Let's check that each thread received a message
		for (SubscribeToAList sub : subs) {
			logger.debug("  Thread {}, lastReceivedMessage is {}", sub.clientName, sub.callback.lastReceivedMessage);
			assertNotNull(sub.callback.lastReceivedMessage,
					"The " + sub.clientName + " should have received a message");
		}

		//////////////////////////////////////////////////////////////////////////////////////////////////
		// No other message should come for these subscriptions. Let's check that.

		// Some messages may have been sent during the unsubscription process.
		// So we accept all messages in the next second, and then we clear the lastReceivedMessage of each client
		Thread.sleep(1000);
		for (SubscribeToAList sub : subs) {
			sub.callback.lastReceivedMessage = null;
		}

		// No other messages should come now. As the server was sending 10 messages each second, waiting 1 second is
		// enough to be sure that each subscription is properly unsubscribed
		Thread.sleep(1000);
		for (SubscribeToAList sub : subs) {
			assertNull(sub.callback.lastReceivedMessage,
					"The " + sub.clientName + " should not have received a message after having unsubscribed");
		}
	}

	@Disabled
	@Test
	// @Execution(ExecutionMode.CONCURRENT)
	public void test_withTwoWebSockets() {
		fail("not yet implemented");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	public void test_subscribeToADate_issue53()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_subscribeToADate_issue53");

		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, 02, 01);// Month is 0-based, so this date is 2018, January the first
		Date date1 = cal.getTime();

		cal.clear();
		cal.set(2018, 02, 02);// Month is 0-based, so this date is 2018, January the second
		Date date2 = cal.getTime();

		// To test the issue 53, we create two clients for the subscription, and check that each of them properly
		// receives the notifications
		SubscribeToADate client1 = new SubscribeToADate(subscriptionExecutor, "client1", date1);
		SubscribeToADate client2 = new SubscribeToADate(subscriptionExecutor, "client2", date2);

		Thread thread1 = new Thread(client1);
		Thread thread2 = new Thread(client2);

		thread1.start();
		thread2.start();

		// Let's wait for the end of our two subscription client threads
		thread1.join();
		thread2.join();

		assertEquals(date1, client1.callback.lastReceivedMessage, "The client 1 should have received a message");
		assertEquals(date2, client2.callback.lastReceivedMessage, "The client 2 should have received a message");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	public void test_subscribeToANullableString()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_subscribeToANullableString");

		SubscriptionCallbackGeneric<String> callback = new SubscriptionCallbackGeneric<>(
				"test_subscribeToANullableString");
		SubscriptionClient sub = subscriptionExecutor.subscriptionWithNullResponse("", callback);

		// Let's wait a max of 20 second, until we receive some notifications
		// (20s will never occur... unless using the debugger to undebug some stuff)
		callback.latchForMessageReception.await(20, TimeUnit.SECONDS);

		// Let's disconnect from the subscription
		sub.unsubscribe();

		assertNull(callback.lastExceptionReceived,
				"We should have received no exception (if any, the received exception is: "
						+ ((callback.lastExceptionReceived == null) ? null
								: callback.lastExceptionReceived.getClass().getName())
						+ ": " + ((callback.lastExceptionReceived == null) ? null
								: callback.lastExceptionReceived.getMessage()));
		assertTrue(callback.hasReceveivedAMessage, "We should have received a message");
		assertNull(callback.lastReceivedMessage, "The message should be null");
	}

	/** Tests a subscription that returns a list of Custom Scalars */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	public void test_subscribeToAListOfDates()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_subscribeToAListOfDate");

		SubscriptionCallbackGeneric<List<Date>> callback = new SubscriptionCallbackGeneric<>(
				"test_subscribeToAListOfDate");
		SubscriptionClient sub = subscriptionExecutor.subscribeToAListOfScalars("", callback);

		// Let's wait a max of 20 second, until we receive some notifications
		// (20s will never occur... unless using the debugger to undebug some stuff)
		callback.latchForMessageReception.await(20, TimeUnit.SECONDS);

		// Let's disconnect from the subscription
		sub.unsubscribe();

		assertTrue(callback.hasReceveivedAMessage, "We should have received a message");
		assertNotNull(callback.lastReceivedMessage, "The message should be null");
		// Each message contains two dates
		assertEquals(2, callback.lastReceivedMessage.size());
		assertNotNull(callback.lastReceivedMessage.get(0), "date0 is not null");
		assertNotNull(callback.lastReceivedMessage.get(1), "date1 is not null");
		assertTrue(callback.lastReceivedMessage.get(0) instanceof Date, "date0 is an instance of Date");
		assertTrue(callback.lastReceivedMessage.get(1) instanceof Date, "date1 is an instance of Date");
	}

	/**
	 * Tests that the graphql-transport-ws 'complete' message is properly propagated from the server to the client, if
	 * it occurs
	 * 
	 * @throws GraphQLRequestPreparationException
	 * @throws GraphQLRequestExecutionException
	 * @throws InterruptedException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	public void test_subscriptionTest_serverComplete()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_subscribeToADate_serverComplete");

		CINP_SubscriptionTestParam_CINS param = getSubscriptionTestParam();
		param.setCompleteAfterFirstNotification(true);
		SubscriptionCallbackGeneric<String> callback = new SubscriptionCallbackGeneric<>(
				"test_subscribeToADate_serverComplete");
		subscriptionExecutor.subscriptionTest("", callback, param);
		// Let's wait a max of 20 second, until we receive some notifications
		// (20s will never occur... unless using the debugger to undebug some stuff)
		callback.latchForMessageReception.await(20, TimeUnit.SECONDS);

		// Let's wait for our first message
		assertNotNull(callback.lastReceivedMessage, "we must have received a message");

		// Then we wait for the subscription completion, which should be sent by the server
		callback.latchForCompleteReception.await(20, TimeUnit.SECONDS);

		// The subscription should be closed
		assertTrue(callback.closedHasBeenReceived,
				"The subscription should have received a 'complete' message from the server");
	}

	/**
	 * Tests that the graphql-transport-ws 'complete' message is properly propagated from the client to the server, if
	 * it occurs
	 * 
	 * @throws GraphQLRequestPreparationException
	 * @throws GraphQLRequestExecutionException
	 * @throws InterruptedException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	public void test_subscriptionTest_clientComplete()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_subscribeToADate_clientComplete");

		CINP_SubscriptionTestParam_CINS param = getSubscriptionTestParam();
		SubscriptionCallbackGeneric<String> callback = new SubscriptionCallbackGeneric<>(
				"test_subscribeToADate_clientComplete");
		SubscriptionClient sub = subscriptionExecutor.subscriptionTest("", callback, param);
		// Let's wait a max of 20 second, until we receive some notifications
		// (20s will never occur... unless using the debugger to undebug some stuff)
		boolean success = callback.latchForMessageReception.await(20, TimeUnit.SECONDS);

		// Let's wait for our first message
		logger.debug("latchForMessageReception has been freed for client with status {}", success);
		assertNotNull(callback.lastReceivedMessage, "we must have received a message");

		// Let's unsubscribe from this subscription
		sub.unsubscribe();

		// Now, we wait for 1s to be sure that the server has executed this unsubscription
		Thread.sleep(1000);
		callback.lastReceivedMessage = null;

		///////////////// Now, we wait another second: no message should be sent by the server
		Thread.sleep(1000);
		assertNull(callback.lastReceivedMessage, "no more message should be sent by the server");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_connectionError()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		Date date = new Calendar.Builder().setDate(2018, 02, 01).build().getTime();
		SubscriptionCallbackGeneric<Date> callback = new SubscriptionCallbackGeneric<>("test_connectionError");
		SubscriptionClient sub = subscriptionExecutor2.issue53("", callback, date);
		callback.latchForMessageReception.await(20, TimeUnit.SECONDS);
		assertInstanceOf(GraphQlTransportException.class, callback.lastExceptionReceived);
		assertTrue(callback.lastExceptionReceived.getMessage().contains("Connection refused"),
				"The received error message is: " + callback.lastExceptionReceived.getMessage());

		sub.unsubscribe();
	}

	/**
	 * Tests that an error in the subscription is properly sent back to the client, when it occurs during the
	 * subscription
	 * 
	 * @throws GraphQLRequestPreparationException
	 * @throws GraphQLRequestExecutionException
	 * @throws InterruptedException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	public void test_subscribeToADate_subscriptionError()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_subscribeToADate_subscriptionError");

		CINP_SubscriptionTestParam_CINS param = getSubscriptionTestParam();
		param.setErrorOnSubscription(true);
		SubscriptionCallbackGeneric<String> callback = new SubscriptionCallbackGeneric<>(
				"test_subscribeToADate_subscriptionError");
		SubscriptionClient sub = subscriptionExecutor.subscriptionTest("", callback, param);
		// Let's wait a max of 20 second, until we receive an exception
		// (20s will never occur... unless using the debugger to undebug some stuff)
		callback.latchForErrorReception.await(20, TimeUnit.SECONDS);

		// Let's test this exception
		assertNotNull(callback.lastExceptionReceived, "we should have received an exception");
		assertTrue(
				callback.lastExceptionReceived.getMessage()
						.contains("Oups, the subscriber asked for an error during the subscription"),
				"The received error message is: " + callback.lastExceptionReceived.getMessage());

		// Let's unsubscribe from this subscription
		sub.unsubscribe();
	}

	/**
	 * Tests that an error in the subscription is properly sent back to the client, when it occurs after the
	 * subscription is active (typically when notifications are coming).
	 * 
	 * @throws GraphQLRequestPreparationException
	 * @throws GraphQLRequestExecutionException
	 * @throws InterruptedException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	public void test_subscriptionTest_nextError()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_subscriptionTest_nextError");

		CINP_SubscriptionTestParam_CINS param = getSubscriptionTestParam();
		param.setErrorOnNext(true);
		SubscriptionCallbackGeneric<String> callback = new SubscriptionCallbackGeneric<>(
				"test_subscribeToADate_nextError");
		SubscriptionClient sub = subscriptionExecutor.subscriptionTest("", callback, param);
		// Let's wait a max of 20 second, until we receive an exception
		// (20s will never occur... unless using the debugger to undebug some stuff)
		callback.latchForErrorReception.await(20, TimeUnit.SECONDS);

		// Let's test this exception
		assertNotNull(callback.lastExceptionReceived, "we must have received an exception");
		assertTrue(
				callback.lastExceptionReceived.getMessage()
						.contains("Oups, the subscriber asked for an error for each next message"),
				"The received error message is: " + callback.lastExceptionReceived.getMessage());

		// Let's unsubscribe from this subscription
		sub.unsubscribe();
	}

	/**
	 * Tests that an error in the subscription is properly sent back to the client, when it occurs if the web socket got
	 * closed
	 * 
	 * @throws GraphQLRequestPreparationException
	 * @throws GraphQLRequestExecutionException
	 * @throws InterruptedException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	public void test_subscribeToAString_webSocketCloseError()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_subscribeToAString_webSocketCloseError");

		CINP_SubscriptionTestParam_CINS param = getSubscriptionTestParam();
		param.setCloseWebSocketBeforeFirstNotification(true);
		SubscriptionCallbackGeneric<String> callback = new SubscriptionCallbackGeneric<>(
				"test_subscribeToADate_webSocketCloseError");
		SubscriptionClient sub = subscriptionExecutor.subscriptionTest("", callback, param);
		// Let's wait a max of 20 second, until we receive an exception
		// (20s will never occur... unless using the debugger to undebug some stuff)
		callback.latchForErrorReception.await(20, TimeUnit.SECONDS);

		// Let's test this exception
		assertNotNull(callback.lastExceptionReceived, "we must have received an exception");
		assertTrue(callback.lastExceptionReceived.getMessage().contains(
				"message=Oups, the subscriber asked that the web socket get disconnected before the first notification"));
		assertTrue(callback.lastExceptionReceived.getMessage().contains("classification=ExecutionAborted"),
				"The error message is: " + callback.lastExceptionReceived.getMessage());

		// Let's unsubscribe from this subscription
		sub.unsubscribe();
	}

	/**
	 * Issue 139
	 * 
	 * @throws GraphQLRequestExecutionException
	 * @throws GraphQLRequestPreparationException
	 * @throws InterruptedException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_subscribeToAEnumWithReservedJavaKeywordAsValues()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_subscribeToAEnumWithReservedJavaKeywordAsValues");

		SubscriptionCallbackGeneric<CEP_EnumWithReservedJavaKeywordAsValues_CES> callback = new SubscriptionCallbackGeneric<>(
				"test_subscribeToAEnumWithReservedJavaKeywordAsValues");
		SubscriptionClient sub = subscriptionExecutor.enumWithReservedJavaKeywordAsValues("", callback);
		// Let's wait a max of 20 second, until we receive an exception
		// (20s will never occur... unless using the debugger to undebug some stuff)
		callback.latchForMessageReception.await(20, TimeUnit.SECONDS);

		// Let's test this exception
		assertNull(callback.lastExceptionReceived, "we must have received no exception");
		assertNotNull(callback.lastReceivedMessage, "we must have received a message");
		assertEquals(CEP_EnumWithReservedJavaKeywordAsValues_CES._instanceof, callback.lastReceivedMessage,
				"All messages are the 'if' value of the enum");

		// Let's unsubscribe from this subscription
		sub.unsubscribe();
	}

	/**
	 * Issue 139
	 * 
	 * @throws GraphQLRequestExecutionException
	 * @throws GraphQLRequestPreparationException
	 * @throws InterruptedException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_subscribeToAListOfEnumsWithReservedJavaKeywordAsValues()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_subscribeToAEnumWithReservedJavaKeywordAsValues");

		SubscriptionCallbackGeneric<List<CEP_EnumWithReservedJavaKeywordAsValues_CES>> callback = new SubscriptionCallbackGeneric<>(
				"test_subscribeToAListOfEnumsWithReservedJavaKeywordAsValues");
		SubscriptionClient sub = subscriptionExecutor.listOfEnumWithReservedJavaKeywordAsValues("", callback);
		// Let's wait a max of 20 second, until we receive an exception
		// (20s will never occur... unless using the debugger to undebug some stuff)
		callback.latchForMessageReception.await(20, TimeUnit.SECONDS);

		// Let's test this exception
		assertNull(callback.lastExceptionReceived, "we must have received no exception");
		assertNotNull(callback.lastReceivedMessage, "we must have received a message");
		assertEquals(4, callback.lastReceivedMessage.size(),
				"each received notifiation should contain a list of 4 items");
		assertEquals(CEP_EnumWithReservedJavaKeywordAsValues_CES._int, callback.lastReceivedMessage.get(0),
				"First item should be the 'int' value of the enum");
		assertEquals(CEP_EnumWithReservedJavaKeywordAsValues_CES._interface, callback.lastReceivedMessage.get(1),
				"Second item should be the 'interface' value of the enum");
		assertEquals(CEP_EnumWithReservedJavaKeywordAsValues_CES._long, callback.lastReceivedMessage.get(2),
				"Third item should be the 'long' value of the enum");
		assertNull(callback.lastReceivedMessage.get(3), "Fourth item is null");

		// Let's unsubscribe from this subscription
		sub.unsubscribe();
	}

	/**
	 * Issue 205: Json scalar not properly managed
	 * 
	 * @return
	 * @throws GraphQLRequestPreparationException
	 * @throws GraphQLRequestExecutionException
	 * @throws InterruptedException
	 * @throws JacksonException
	 * @throws DatabindException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test205json() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException,
			InterruptedException, DatabindException, JacksonException {

		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test205json");

		ObjectNode json = new ObjectMapper().readValue(
				"{\"field\":\"value\", \"subObject\": {\"field2\" : [1,2,3], \"field3\" : [1.1,22.2,3.3]} ,  \"booleans\" : [true , false]}",
				ObjectNode.class);

		SubscriptionCallbackGeneric<ObjectNode> callback = new SubscriptionCallbackGeneric<>("test205json");
		SubscriptionClient sub = subscriptionExecutor.json("", callback, json);

		// Let's wait a max of 20 second, until we receive some notifications
		// (20s will never occur... unless using the debugger to undebug some stuff)
		callback.latchForMessageReception.await(20, TimeUnit.SECONDS);

		// Let's disconnect from the subscription
		sub.unsubscribe();

		assertTrue(callback.hasReceveivedAMessage, "We should have received a message");
		assertEquals(
				"{\"field\":\"value\",\"subObject\":{\"field2\":[1,2,3],\"field3\":[1.1,22.2,3.3]},\"booleans\":[true,false]}",
				callback.lastReceivedMessage.toString());
	}

	/**
	 * Issue 205: Json scalar not properly managed. The Object doesn't work better (before the 205's correction)
	 * 
	 * @return
	 * @throws JacksonException
	 * @throws DatabindException
	 * @throws GraphQLRequestPreparationException
	 * @throws GraphQLRequestExecutionException
	 * @throws InterruptedException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test205jsons() throws DatabindException, JacksonException, GraphQLRequestExecutionException,
			GraphQLRequestPreparationException, InterruptedException {
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test205jsons");

		ObjectNode json = new ObjectMapper().readValue(
				"{\"field\":\"value\", \"subObject\": {\"field2\" : [1,2,3], \"field3\" : [1.1,22.2,3.3]} ,  \"booleans\" : [true , false]}",
				ObjectNode.class);

		SubscriptionCallbackGeneric<List<ObjectNode>> callback = new SubscriptionCallbackGeneric<>("test205json");
		SubscriptionClient sub = subscriptionExecutor.jsons("", callback, Arrays.asList(json, json));

		// Let's wait a max of 20 second, until we receive some notifications
		// (20s will never occur... unless using the debugger to undebug some stuff)
		callback.latchForMessageReception.await(20, TimeUnit.SECONDS);

		// Let's disconnect from the subscription
		sub.unsubscribe();

		assertTrue(callback.hasReceveivedAMessage, "We should have received a message");
		assertEquals(2, callback.lastReceivedMessage.size());
		assertEquals(
				"{\"field\":\"value\",\"subObject\":{\"field2\":[1,2,3],\"field3\":[1.1,22.2,3.3]},\"booleans\":[true,false]}",
				callback.lastReceivedMessage.get(0).toString());
	}

	/**
	 * Issue 205: Json scalar not properly managed.
	 * 
	 * @return
	 * @throws GraphQLRequestPreparationException
	 * @throws GraphQLRequestExecutionException
	 * @throws JacksonException
	 * @throws DatabindException
	 * @throws InterruptedException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test205jsonsWithInput() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException,
			DatabindException, JacksonException, InterruptedException {
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test205jsons");

		String test = "a global value for test";
		@SuppressWarnings("deprecation")
		Date date = new Date(2023 - 1900, 12 - 1, 30);// DateUtils.truncate(new Date(),
														// java.util.Calendar.DAY_OF_MONTH);
		Long l = 2345284L;
		Boolean bool = true;
		CEP_Episode_CES e = CEP_Episode_CES.NEWHOPE;
		ObjectNode json = new ObjectMapper().readValue(
				"{\"field\":\"value\", \"subObject\": {\"field2\" : [1,2,3], \"field3\" : [1.1,22.2,3.3]} ,  \"booleans\" : [true , false]}",
				ObjectNode.class);
		List<ObjectNode> jsons = Arrays.asList(json, json);
		CINP_InputWithJson_CINS input = CINP_InputWithJson_CINS.builder()
				.withTest("test_Issue205_JsonArgumentsAndInputTypeWithJsonField")//
				.withDate(date)//
				.withLong(l)//
				.withBoolean(bool)//
				.withEnum(e)//
				.withJson(json)//
				.withJsons(Arrays.asList(json, json))//
				.build();

		SubscriptionCallbackGeneric<List<CTP_TypeWithJson_CTS>> callback = new SubscriptionCallbackGeneric<>(
				"test205json");
		SubscriptionClient sub = subscriptionExecutor.jsonsWithInput(""//
				+ "{"//
				+ "   test"//
				+ "	  withArguments("//
				+ "      test: &test,"//
				+ "      date: &date,"//
				+ "      long: &long,"//
				+ "      boolean: &boolean,"//
				+ "      enum: &enum,"//
				+ "      json: &json,"//
				+ "      jsons: &jsons)"//
				+ "	  long"//
				+ "	  boolean"//
				+ "	  enum"//
				+ "	  json"//
				+ "	  jsons"//
				+ "}", //
				callback, //
				Arrays.asList(input, input), //
				"test", test, //
				"date", date, //
				"long", l, //
				"boolean", bool, //
				"enum", e, //
				"json", json, //
				"jsons", jsons);

		// Let's wait a max of 20 second, until we receive some notifications
		// (20s will never occur... unless using the debugger to undebug some stuff)
		callback.latchForMessageReception.await(20, TimeUnit.SECONDS);

		// Let's disconnect from the subscription
		sub.unsubscribe();

		assertTrue(callback.hasReceveivedAMessage, "We should have received a message");
		assertEquals(2, callback.lastReceivedMessage.size());
		assertEquals(
				"{\"field\":\"value\",\"subObject\":{\"field2\":[1,2,3],\"field3\":[1.1,22.2,3.3]},\"booleans\":[true,false]}",
				callback.lastReceivedMessage.get(1).getJson().toString());
		assertEquals(2, callback.lastReceivedMessage.get(1).getJsons().size());
		assertEquals(
				"{\"field\":\"value\",\"subObject\":{\"field2\":[1,2,3],\"field3\":[1.1,22.2,3.3]},\"booleans\":[true,false]}",
				callback.lastReceivedMessage.get(1).getJsons().get(0).toString());
	}

	public static CINP_SubscriptionTestParam_CINS getSubscriptionTestParam() {
		CINP_SubscriptionTestParam_CINS param = new CINP_SubscriptionTestParam_CINS();
		param.setCloseWebSocketBeforeFirstNotification(false);
		param.setCompleteAfterFirstNotification(false);
		param.setErrorOnNext(false);
		param.setErrorOnSubscription(false);
		return param;
	}
}
