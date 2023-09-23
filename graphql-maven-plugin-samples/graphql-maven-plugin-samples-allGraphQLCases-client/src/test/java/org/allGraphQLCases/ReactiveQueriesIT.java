package org.allGraphQLCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.allGraphQLCases.client.util.AnotherMutationTypeReactiveExecutorAllGraphQLCases;
import org.allGraphQLCases.client.CINP_FieldParameterInput_CINS;
import org.allGraphQLCases.client.CINP_SubscriptionTestParam_CINS;
import org.allGraphQLCases.client.CTP_AllFieldCases_CTS;
import org.allGraphQLCases.client.CTP_MyQueryType_CTS;
import org.allGraphQLCases.client.util.GraphQLReactiveRequestAllGraphQLCases;
import org.allGraphQLCases.client.util.MyQueryTypeReactiveExecutorAllGraphQLCases;
import org.allGraphQLCases.client.util.TheSubscriptionTypeReactiveExecutorAllGraphQLCases;
import org.allGraphQLCases.client2.TheSubscriptionTypeReactiveExecutorAllGraphQLCases2;
import org.allGraphQLCases.subscription.ExecSubscriptionIT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.GraphQlTransportException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

import reactor.core.Disposable;
import reactor.core.publisher.Signal;

//Adding "webEnvironment = SpringBootTest.WebEnvironment.NONE" avoid this error:
//"No qualifying bean of type 'ReactiveClientRegistrationRepository' available"
//More details here: https://stackoverflow.com/questions/62558552/error-when-using-enablewebfluxsecurity-in-springboot
@SpringBootTest(classes = SpringTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Execution(ExecutionMode.CONCURRENT)
public class ReactiveQueriesIT {

	private static Logger logger = LoggerFactory.getLogger(ReactiveQueriesIT.class);

	@Autowired
	MyQueryTypeReactiveExecutorAllGraphQLCases reactiveQueryExecutor;

	@Autowired
	AnotherMutationTypeReactiveExecutorAllGraphQLCases reactiveMutationExecutor;

	@Autowired
	TheSubscriptionTypeReactiveExecutorAllGraphQLCases reactiveSubscriptionExecutor;

	/** The {@link TheSubscriptionTypeReactiveExecutorAllGraphQLCases2} executor allows to check connection errors */
	@Autowired
	TheSubscriptionTypeReactiveExecutorAllGraphQLCases2 reactiveExecutorAllGraphQLCases2;

	@Resource(name = "httpGraphQlClientAllGraphQLCases")
	GraphQlClient httpGraphQlClient;

	// Prepared full queries
	GraphQLReactiveRequestAllGraphQLCases reactiveMutationWithDirectiveRequest;
	GraphQLReactiveRequestAllGraphQLCases reactiveMutationWithoutDirectiveRequest;
	GraphQLReactiveRequestAllGraphQLCases reactiveWithDirectiveTwoParametersRequest;
	GraphQLReactiveRequestAllGraphQLCases reactiveMultipleQueriesRequest;

	// Test stuff for the subscriptions (valid only for partial queries)
	public static class ReceivedFromSubscription<T> {
		public CountDownLatch latchForMessageReception = new CountDownLatch(1);
		public boolean hasReceveivedAMessage = false;
		public T lastReceivedMessage = null;
		public Throwable lastReceivedError = null;

		public void doOnEach(Signal<Optional<T>> o) {
			switch (o.getType()) {
			case ON_NEXT:
				this.lastReceivedMessage = o.get().orElse(null);
				this.hasReceveivedAMessage = true;
				this.latchForMessageReception.countDown();
				break;
			case ON_ERROR:
				this.lastReceivedError = o.getThrowable();
				this.latchForMessageReception.countDown();
				break;
			default:
				// No action}
			}
		}
	}

	final ReceivedFromSubscription<String> receivedFromSubsriptionString = new ReceivedFromSubscription<String>();
	final ReceivedFromSubscription<Date> receivedFromSubsriptionDate = new ReceivedFromSubscription<Date>();

	public static class ExtensionValue {
		public String name;
		public String forname;
	}

	@BeforeEach
	void setup() throws GraphQLRequestPreparationException {
		String req;
		// The response preparation should be somewhere in the application initialization code.
		req = "mutation{createHuman (human: &humanInput) @testDirective(value:&value, anotherValue:?anotherValue)   "//
				+ "{id name appearsIn friends {id name}}}";//
		this.reactiveMutationWithDirectiveRequest = this.reactiveMutationExecutor.getGraphQLRequest(req);

		req = "mutation{createHuman (human: &humanInput) {id name appearsIn friends {id name}}}";
		this.reactiveMutationWithoutDirectiveRequest = this.reactiveMutationExecutor.getGraphQLRequest(req);

		req = "query{directiveOnQuery (uppercase: false) @testDirective(value:&value, anotherValue:?anotherValue)}";
		this.reactiveWithDirectiveTwoParametersRequest = this.reactiveMutationExecutor.getGraphQLRequest(req);

		req = "{"//
				+ " directiveOnQuery (uppercase: false) @testDirective(value:&value, anotherValue:?anotherValue)"//
				+ " withOneOptionalParam {id name appearsIn friends {id name}}"//
				+ " withoutParameters {appearsIn @skip(if: &skipAppearsIn) name @skip(if: &skipName) }"//
				+ "}";
		this.reactiveMultipleQueriesRequest = this.reactiveQueryExecutor.getGraphQLRequest(req);
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_fullQuery_noDirective_extensionsResponseField()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, JsonProcessingException {
		String request = "{directiveOnQuery}";

		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_fullQuery_noDirective_extensionsResponseField");

		// Direct queries should be used only for very simple cases
		CTP_MyQueryType_CTS resp = this.reactiveQueryExecutor.exec(request).block();

		// Verifications
		assertNotNull(resp);
		List<String> ret = resp.getDirectiveOnQuery();
		assertNotNull(ret);
		assertEquals(0, ret.size());
		//
		// The extensions field contains a CTP_Human_CTS instance, for the key "aValueToTestTheExtensionsField".
		// Check the org.allGraphQLCases.server.extensions.CustomBeans (creation of the customGraphQL Spring bean)
		assertNotNull(resp.getExtensions());
		assertNotNull(resp.getExtensionsAsMap());
		assertNotNull(resp.getExtensionsAsMap().get("aValueToTestTheExtensionsField"));
		ExtensionValue value = resp.getExtensionsField("aValueToTestTheExtensionsField", ExtensionValue.class);
		assertEquals("The name", value.name);
		assertEquals("The forname", value.forname);
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_fullQuery_withDirectiveOneParameter()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		String request = "{directiveOnQuery  (uppercase: true) @testDirective(value:&value)}";

		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_fullQuery_withDirectiveOneParameter");

		// Go, go, go

		// Direct queries should be used only for very simple cases, but you can do what you want... :)
		CTP_MyQueryType_CTS resp = this.reactiveQueryExecutor//
				.exec(request, "value", "the value", "skip", Boolean.FALSE) //$NON-NLS-3$
				.block();

		// Verifications
		assertNotNull(resp);
		List<String> ret = resp.getDirectiveOnQuery();
		assertNotNull(ret);
		assertEquals(1, ret.size());
		//
		assertEquals("THE VALUE", ret.get(0));
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_preparedQuery_withDirectiveTwoParameters()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_preparedQuery_withDirectiveTwoParameters");

		// Go, go, go
		CTP_MyQueryType_CTS resp = this.reactiveWithDirectiveTwoParametersRequest.execQuery( //
				"value", "the value", "anotherValue", "the other value", "skip", Boolean.TRUE).block();
		// Verifications
		assertNotNull(resp);
		List<String> ret = resp.getDirectiveOnQuery();
		assertNotNull(ret);
		assertEquals(2, ret.size());
		//
		assertEquals("the value", ret.get(0));
		assertEquals("the other value", ret.get(1));
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_preparedQuery_GraphQLVariable_directQuery()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_preparedQuery_GraphQLVariable_directQuery");

		// Preparation
		List<List<Double>> matrix = Arrays.asList(//
				null, //
				Arrays.asList(), //
				Arrays.asList(1.0), //
				Arrays.asList(4.0, 5.0, 6.0)//
		);

		// Go, go, go
		CTP_MyQueryType_CTS resp = this.reactiveQueryExecutor
				.exec("query queryWithAMatrix($matrixParam: [[Float]]!) {withListOfList(matrix:$matrixParam){matrix}}", //
						"matrixParam", matrix)
				.block();

		// Verifications
		List<List<Double>> ret = resp.getWithListOfList().getMatrix();
		assertNotNull(ret);
		assertEquals(4, ret.size());
		int i = 0;
		//
		assertNull(ret.get(i++));
		//
		List<Double> item = ret.get(i++);
		assertNotNull(item);
		assertEquals(0, item.size());
		//
		item = ret.get(i++);
		assertNotNull(item);
		assertEquals(1, item.size());
		assertEquals(1, item.get(0));
		//
		item = ret.get(i++);
		assertNotNull(item);
		assertEquals(3, item.size());
		assertEquals(4, item.get(0));
		assertEquals(5, item.get(1));
		assertEquals(6, item.get(2));
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_fullQuery_GraphQLVariable_directQuery_map()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_fullQuery_GraphQLVariable_directQuery_map");

		// Preparation
		List<List<Double>> matrix = Arrays.asList(//
				null, //
				Arrays.asList(), //
				Arrays.asList(1.0), //
				Arrays.asList(4.0, 5.0, 6.0)//
		);
		Map<String, Object> map = new HashMap<>();
		map.put("matrixParam", matrix);

		// Go, go, go
		CTP_MyQueryType_CTS resp = this.reactiveQueryExecutor.execWithBindValues(
				"query queryWithAMatrix($matrixParam: [[Float]]!) {withListOfList(matrix:$matrixParam){matrix}}", map)
				.block();

		// Verifications
		List<List<Double>> ret = resp.getWithListOfList().getMatrix();
		assertNotNull(ret);
		assertEquals(4, ret.size());
		int i = 0;
		//
		assertNull(ret.get(i++));
		//
		List<Double> item = ret.get(i++);
		assertNotNull(item);
		assertEquals(0, item.size());
		//
		item = ret.get(i++);
		assertNotNull(item);
		assertEquals(1, item.size());
		assertEquals(1, item.get(0));
		//
		item = ret.get(i++);
		assertNotNull(item);
		assertEquals(3, item.size());
		assertEquals(4, item.get(0));
		assertEquals(5, item.get(1));
		assertEquals(6, item.get(2));
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_fullQuery_withDirectiveTwoParameters()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_fullQuery_withDirectiveTwoParameters");

		Map<String, Object> params = new HashMap<>();
		params.put("uppercase", true);
		params.put("anotherValue", "another value with an antislash: \\");
		params.put("Value", "a first \"value\"");

		// Preparation
		GraphQLReactiveRequestAllGraphQLCases reactiveDirectiveOnQuery = this.reactiveMutationExecutor
				.getGraphQLRequest("query namedQuery($uppercase :\n" //
						+ "Boolean, \n\r"//
						+ " $Value :   String ! , $anotherValue:String) {directiveOnQuery (uppercase: $uppercase) @testDirective(value:$Value, anotherValue:$anotherValue)}");

		// Go, go, go
		CTP_MyQueryType_CTS resp = reactiveDirectiveOnQuery.execQuery(params).block();

		// Verifications
		assertNotNull(resp);
		List<String> ret = resp.getDirectiveOnQuery();
		assertNotNull(ret);
		assertEquals(2, ret.size());
		//
		assertEquals("A FIRST \"VALUE\"", ret.get(0));
		assertEquals("ANOTHER VALUE WITH AN ANTISLASH: \\", ret.get(1));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_preparedPartialQuery_Issue65_ListID_array()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_preparedPartialQuery_Issue65_ListID_array");

		// Preparation
		List<CINP_FieldParameterInput_CINS> inputs = new ArrayList<>();
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(true).build());
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(false).build());
		//
		GraphQLReactiveRequestAllGraphQLCases reactivePartialQuery = this.reactiveQueryExecutor
				.getAllFieldCasesGraphQLRequest("{issue65(inputs: &inputs)}");

		// Go, go, go
		CTP_AllFieldCases_CTS ret = this.reactiveQueryExecutor
				.allFieldCases(reactivePartialQuery, null, "inputs", inputs).block().get();

		// Verification
		assertEquals(inputs.size(), ret.getIssue65().size());
		assertEquals(ret.getIssue65().get(0).getName().toUpperCase(), ret.getIssue65().get(0).getName(),
				"The first name should be in uppercase");
		assertNotEquals(ret.getIssue65().get(1).getName().toUpperCase(), ret.getIssue65().get(1).getName(),
				"The second name should NOT be in uppercase");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_preparedPartialQuery_Issue65_ListID_map()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_preparedPartialQuery_Issue65_ListID_map");

		// Preparation
		List<CINP_FieldParameterInput_CINS> inputs = new ArrayList<>();
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(true).build());
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(false).build());
		//
		Map<String, Object> map = new HashMap<>();
		map.put("inputs", inputs);
		//
		GraphQLReactiveRequestAllGraphQLCases reactivePartialQuery = this.reactiveQueryExecutor
				.getAllFieldCasesGraphQLRequest("{issue65(inputs: &inputs)}");

		// Go, go, go
		CTP_AllFieldCases_CTS ret = this.reactiveQueryExecutor
				.allFieldCasesWithBindValues(reactivePartialQuery, null, map).block().get();

		// Verification
		assertEquals(inputs.size(), ret.getIssue65().size());
		assertEquals(ret.getIssue65().get(0).getName().toUpperCase(), ret.getIssue65().get(0).getName(),
				"The first name should be in uppercase");
		assertNotEquals(ret.getIssue65().get(1).getName().toUpperCase(), ret.getIssue65().get(1).getName(),
				"The second name should NOT be in uppercase");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_directPartialQuery_Issue65_ListID_array()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_directPartialQuery_Issue65_ListID_array");

		// Preparation
		List<CINP_FieldParameterInput_CINS> inputs = new ArrayList<>();
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(true).build());
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(false).build());
		//

		// Go, go, go
		CTP_AllFieldCases_CTS ret = this.reactiveQueryExecutor
				.allFieldCases("{issue65(inputs: &inputs)}", null, "inputs", inputs).block().get();

		// Verification
		assertEquals(inputs.size(), ret.getIssue65().size());
		assertEquals(ret.getIssue65().get(0).getName().toUpperCase(), ret.getIssue65().get(0).getName(),
				"The first name should be in uppercase");
		assertNotEquals(ret.getIssue65().get(1).getName().toUpperCase(), ret.getIssue65().get(1).getName(),
				"The second name should NOT be in uppercase");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_directPartialQuery_Issue65_ListID_map()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_directPartialQuery_Issue65_ListID_map");

		// Preparation
		List<CINP_FieldParameterInput_CINS> inputs = new ArrayList<>();
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(true).build());
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(false).build());
		//
		Map<String, Object> map = new HashMap<>();
		map.put("inputs", inputs);

		// Go, go, go
		CTP_AllFieldCases_CTS ret = this.reactiveQueryExecutor
				.allFieldCasesWithBindValues("{issue65(inputs: &inputs)}", null, map).block().get();

		// Verification
		assertEquals(inputs.size(), ret.getIssue65().size());
		assertEquals(ret.getIssue65().get(0).getName().toUpperCase(), ret.getIssue65().get(0).getName(),
				"The first name should be in uppercase");
		assertNotEquals(ret.getIssue65().get(1).getName().toUpperCase(), ret.getIssue65().get(1).getName(),
				"The second name should NOT be in uppercase");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	public void test_subscribeToANullableString()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {

		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_subscribeToANullableString");

		// Go, go, go
		Disposable d = this.reactiveSubscriptionExecutor//
				.subscriptionWithNullResponse("")//
				.doOnEach(o -> this.receivedFromSubsriptionString.doOnEach(o))//
				.subscribe();

		// Verification

		// Let's wait a max of 20 seconds, until we receive a notification
		// (20s will never occur... unless using the debugger to debug some stuff)
		this.receivedFromSubsriptionString.latchForMessageReception.await(20, TimeUnit.SECONDS);
		// Let's release the used resources
		d.dispose();

		assertNull(this.receivedFromSubsriptionString.lastReceivedError,
				"We should have received no exception (if any, the received exception is: "
						+ ((this.receivedFromSubsriptionString.lastReceivedError == null) ? null
								: this.receivedFromSubsriptionString.lastReceivedError.getClass().getName())
						+ ": " + ((this.receivedFromSubsriptionString.lastReceivedError == null) ? null
								: this.receivedFromSubsriptionString.lastReceivedError.getMessage()));
		assertTrue(this.receivedFromSubsriptionString.hasReceveivedAMessage, "We should have received a message");
		assertNull(this.receivedFromSubsriptionString.lastReceivedMessage,
				"The message should be null (it is actually: " + this.receivedFromSubsriptionString.lastReceivedMessage
						+ ")");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_connectionError()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		Date date = new Calendar.Builder().setDate(2018, 02, 01).build().getTime();

		// Go, go, go
		Disposable d = this.reactiveExecutorAllGraphQLCases2//
				.issue53("", date)//
				.doOnEach(o -> this.receivedFromSubsriptionDate.doOnEach(o))//
				.subscribe();

		// Verification

		// Let's wait a max of 20 seconds, until we receive a notification
		// (20s will never occur... unless using the debugger to debug some stuff)
		this.receivedFromSubsriptionDate.latchForMessageReception.await(20, TimeUnit.SECONDS);
		// Let's release the used resources
		d.dispose();

		assertInstanceOf(GraphQlTransportException.class, this.receivedFromSubsriptionDate.lastReceivedError);
		assertTrue(this.receivedFromSubsriptionDate.lastReceivedError.getMessage().contains("Connection refused"),
				"The received error message is: " + this.receivedFromSubsriptionDate.lastReceivedError.getMessage());
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
	public void test_subscribeToAString_subscriptionError()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_subscribeToAString_subscriptionError");

		CINP_SubscriptionTestParam_CINS param = ExecSubscriptionIT.getSubscriptionTestParam();
		param.setErrorOnSubscription(true);

		// Go, go, go
		Disposable d = this.reactiveSubscriptionExecutor//
				.subscriptionTest("", param)//
				.doOnEach(o -> this.receivedFromSubsriptionString.doOnEach(o))//
				.subscribe();

		// Verification

		// Let's wait a max of 20 seconds, until we receive a notification
		// (20s will never occur... unless using the debugger to debug some stuff)
		this.receivedFromSubsriptionString.latchForMessageReception.await(20, TimeUnit.SECONDS);
		// Let's release the used resources
		d.dispose();

		// Let's test this exception
		assertNotNull(this.receivedFromSubsriptionString.lastReceivedError, "we should have received an exception");
		assertTrue(
				this.receivedFromSubsriptionString.lastReceivedError.getMessage()
						.contains("Oups, the subscriber asked for an error during the subscription"),
				"The received error message is: " + this.receivedFromSubsriptionString.lastReceivedError.getMessage());
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
	// @Execution(ExecutionMode.CONCURRENT)
	public void test_subscriptionTest_nextError()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_subscriptionTest_nextError");

		CINP_SubscriptionTestParam_CINS param = ExecSubscriptionIT.getSubscriptionTestParam();
		param.setErrorOnNext(true);

		// Go, go, go
		Disposable d = this.reactiveSubscriptionExecutor//
				.subscriptionTest("", param)//
				.doOnEach(o -> this.receivedFromSubsriptionString.doOnEach(o))//
				.subscribe();

		// Verification

		// Let's wait a max of 20 seconds, until we receive a notification
		// (20s will never occur... unless using the debugger to debug some stuff)
		this.receivedFromSubsriptionString.latchForMessageReception.await(20, TimeUnit.SECONDS);
		// Let's release the used resources
		d.dispose();

		// Let's test this exception
		assertNotNull(this.receivedFromSubsriptionString.lastReceivedError, "we must have received an exception");
		assertTrue(
				this.receivedFromSubsriptionString.lastReceivedError.getMessage()
						.contains("Oups, the subscriber asked for an error for each next message"),
				"The received error message is: " + this.receivedFromSubsriptionString.lastReceivedError.getMessage());
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

		CINP_SubscriptionTestParam_CINS param = ExecSubscriptionIT.getSubscriptionTestParam();
		param.setCloseWebSocketBeforeFirstNotification(true);

		// Go, go, go
		Disposable d = this.reactiveSubscriptionExecutor//
				.subscriptionTest("", param)//
				.doOnEach(o -> this.receivedFromSubsriptionString.doOnEach(o))//
				.subscribe();

		// Verification

		// Let's wait a max of 20 seconds, until we receive a notification
		// (20s will never occur... unless using the debugger to debug some stuff)
		this.receivedFromSubsriptionString.latchForMessageReception.await(20, TimeUnit.SECONDS);
		// Let's release the used resources
		d.dispose();

		// Let's test this exception
		assertNotNull(this.receivedFromSubsriptionString.lastReceivedError, "we must have received an exception");
		assertTrue(this.receivedFromSubsriptionString.lastReceivedError.getMessage().contains(
				"message=Oups, the subscriber asked that the web socket get disconnected before the first notification"));
		assertTrue(
				this.receivedFromSubsriptionString.lastReceivedError.getMessage()
						.contains("classification=ExecutionAborted"),
				"The error message is: " + this.receivedFromSubsriptionString.lastReceivedError.getMessage());
	}
}
