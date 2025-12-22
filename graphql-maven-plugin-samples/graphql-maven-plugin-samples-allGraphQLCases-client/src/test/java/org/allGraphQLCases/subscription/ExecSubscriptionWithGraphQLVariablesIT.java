package org.allGraphQLCases.subscription;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.allGraphQLCases.SpringTestConfig;
import org.allGraphQLCases.client.CINP_AllFieldCasesInput_CINS;
import org.allGraphQLCases.client.CINP_AllFieldCasesWithoutIdSubtypeInput_CINS;
import org.allGraphQLCases.client.CTP_AllFieldCases_CTS;
import org.allGraphQLCases.client.util.GraphQLRequestAllGraphQLCases;
import org.allGraphQLCases.client.util.TheSubscriptionTypeExecutorAllGraphQLCases;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

//Adding "webEnvironment = SpringBootTest.WebEnvironment.NONE" avoid this error:
//"No qualifying bean of type 'ReactiveClientRegistrationRepository' available"
//More details here: https://stackoverflow.com/questions/62558552/error-when-using-enablewebfluxsecurity-in-springboot
@SpringBootTest(classes = SpringTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ExecSubscriptionWithGraphQLVariablesIT {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(ExecSubscriptionWithGraphQLVariablesIT.class);

	@Autowired
	TheSubscriptionTypeExecutorAllGraphQLCases subscriptionExecutor;

	String id = "123e4567-e89b-12d3-a456-426614174000";
	String name = "a String";
	Long age = (long) 1234567890;
	int i = 666;
	Long nbItems = (long) 3;
	Date date = new Calendar.Builder().setDate(2021, 02 - 1, 01).build().getTime();
	Date[] dates = { //
			new Calendar.Builder().setDate(2021, 03 - 1, 01).build().getTime(),
			new Calendar.Builder().setDate(2021, 03 - 1, 02).build().getTime(),
			new Calendar.Builder().setDate(2021, 03 - 1, 03).build().getTime() };
	String[] aliases = { "alias" };
	String[] planets = { "planet1", "planet2" };

	Double[] list0 = {};
	Double[] list1 = { 1.1 };
	Double[] list2 = { 22.22, 333.333 };
	List<List<Double>> matrix = new ArrayList<>();

	CINP_AllFieldCasesWithoutIdSubtypeInput_CINS oneWithoutIdSubtype = new CINP_AllFieldCasesWithoutIdSubtypeInput_CINS();
	List<CINP_AllFieldCasesWithoutIdSubtypeInput_CINS> listWithoutIdSubtype = new ArrayList<>();

	@SuppressWarnings("unchecked")
	@BeforeEach
	void setup() {

		// matrix
		matrix.add((List<Double>) (Object) Arrays.asList(list0));
		matrix.add((List<Double>) (Object) Arrays.asList(list1));
		matrix.add((List<Double>) (Object) Arrays.asList(list2));

		// oneWithoutIdSubtype
		oneWithoutIdSubtype.setName("the name");

		// listWithoutIdSubtype
		listWithoutIdSubtype.add(CINP_AllFieldCasesWithoutIdSubtypeInput_CINS.builder().withName("name0").build());
		listWithoutIdSubtype.add(CINP_AllFieldCasesWithoutIdSubtypeInput_CINS.builder().withName("name1").build());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_GraphQLVariables_allGraphQLCasesInput()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		// Preparation
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_GraphQLVariables_allGraphQLCasesInput");
		SubscriptionCallbackGeneric<CTP_AllFieldCases_CTS> callback = new SubscriptionCallbackGeneric<>(
				"test_GraphQLVariables_allGraphQLCasesInput");

		CINP_AllFieldCasesInput_CINS input = new CINP_AllFieldCasesInput_CINS();
		input.setId(id);
		input.setAge(age);
		input.setDate(date);
		input.setDates((List<Date>) (Object) Arrays.asList(dates));
		input.setMatrix(matrix);
		input.setName(name);
		input.setPlanets((List<String>) (Object) Arrays.asList(planets));
		input.setAliases((List<String>) (Object) Arrays.asList(aliases));
		input.setWithoutIdSubtype(listWithoutIdSubtype);

		GraphQLRequestAllGraphQLCases subscriptionRequest = subscriptionExecutor.getGraphQLRequest(
				"subscription sub($nbItems: Long!, $input: AllFieldCasesInput!){allGraphQLCasesInput(input: $input){id name age date dates matrix oneWithoutIdSubType listWithoutIdSubTypes(nbItems: $nbItems)}}");

		// Go, go, go
		@SuppressWarnings("unused")
		SubscriptionClient sub = subscriptionRequest.execSubscription(callback, CTP_AllFieldCases_CTS.class, //
				"input", input, //
				"nbItems", nbItems);

		// Verification

		// Let's wait a max of 20 seconds, until we receive a first notification
		// (20s will never occur... unless using the debugger to undebug some stuff)
		callback.latchForMessageReception.await(20, TimeUnit.SECONDS);

		assertNull(callback.lastExceptionReceived, (callback.lastExceptionReceived == null) ? //
				"No error"//
				: "lastExceptionReceived should be null, but is " + callback.lastExceptionReceived.getClass().getName()
						+ ": " + callback.lastExceptionReceived.getMessage()//
		);
		assertNotNull(callback.lastReceivedMessage);

		// Let's wait for the subscription to close (the server will send back only one item)
		callback.latchForCompleteReception.await(20, TimeUnit.SECONDS);
		assertNull(callback.lastExceptionReceived);
		assertNull(callback.closureReason, "Null since 2.0");

		// Check of the received values
		assertNotNull(callback.lastReceivedMessage);
		assertEquals(name, callback.lastReceivedMessage.getName());
		assertEquals(age, callback.lastReceivedMessage.getAge());
		assertEquals(date, callback.lastReceivedMessage.getDate());

		// matrix
		assertEquals(3, callback.lastReceivedMessage.getMatrix().size());
		assertArrayEquals(list0, callback.lastReceivedMessage.getMatrix().get(0).toArray());
		assertArrayEquals(list1, callback.lastReceivedMessage.getMatrix().get(1).toArray());
		assertArrayEquals(list2, callback.lastReceivedMessage.getMatrix().get(2).toArray());

		// WithIdSubTypes have not been given: they should be null
		assertNull(callback.lastReceivedMessage.getOneWithIdSubType());
		assertNull(callback.lastReceivedMessage.getListWithIdSubTypes());

		// listWithoutIdSubTypes
		assertEquals(2, callback.lastReceivedMessage.getListWithoutIdSubTypes().size());
		assertEquals("name0", callback.lastReceivedMessage.getListWithoutIdSubTypes().get(0).getName());
		assertEquals("name1", callback.lastReceivedMessage.getListWithoutIdSubTypes().get(1).getName());
	}

	@Test
	public void test_GraphQLVariables_allGraphQLCasesParam_asBindParameters()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		// Preparation
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_GraphQLVariables_allGraphQLCasesParam_asBindParameters");
		SubscriptionCallbackGeneric<CTP_AllFieldCases_CTS> callback = new SubscriptionCallbackGeneric<>(
				"test_GraphQLVariables_allGraphQLCasesInput");

		// Go, go, go
		// allGraphQLCasesParam(id: String!, name: String!, age: Long!, integer: Int!, date: Date!, dates: [Date]!,
		// matrix: [[Float]]!, oneWithoutIdSubtype: AllFieldCasesWithoutIdSubtypeInput!, listWithoutIdSubtype:
		// [AllFieldCasesWithoutIdSubtypeInput!]!): AllFieldCases!
		@SuppressWarnings({ "unused", "unchecked" })
		SubscriptionClient sub = subscriptionExecutor.allGraphQLCasesParam(
				"{id name age date dates matrix oneWithoutIdSubType listWithoutIdSubTypes(nbItems: 15)}", callback, id,
				name, age, i, date, (List<Date>) (Object) Arrays.asList(dates), matrix, oneWithoutIdSubtype,
				listWithoutIdSubtype);

		// Verification

		// Let's wait a max of 20 seconds, until we receive a first notification
		// (20s will never occur... unless using the debugger to undebug some stuff)
		callback.latchForMessageReception.await(20, TimeUnit.SECONDS);

		assertNull(callback.lastExceptionReceived, (callback.lastExceptionReceived == null) ? //
				"No error"//
				: "lastExceptionReceived should be null, but is " + callback.lastExceptionReceived.getClass().getName()
						+ ": " + callback.lastExceptionReceived.getMessage()//
		);
		assertNotNull(callback.lastReceivedMessage);

		// Let's wait for the subscription to close (the server will send back only one item)
		callback.latchForCompleteReception.await(20, TimeUnit.SECONDS);
		assertNull(callback.lastExceptionReceived);
		assertNull(callback.closureReason, "Null since 2.0");

		// Check of the received values
		assertNotNull(callback.lastReceivedMessage);
		assertEquals(name, callback.lastReceivedMessage.getName());
		assertEquals(age, callback.lastReceivedMessage.getAge());
		assertEquals(date, callback.lastReceivedMessage.getDate());

		// matrix
		assertEquals(3, callback.lastReceivedMessage.getMatrix().size());
		assertArrayEquals(list0, callback.lastReceivedMessage.getMatrix().get(0).toArray());
		assertArrayEquals(list1, callback.lastReceivedMessage.getMatrix().get(1).toArray());
		assertArrayEquals(list2, callback.lastReceivedMessage.getMatrix().get(2).toArray());

		// WithIdSubTypes have not been given: they should be null
		assertNull(callback.lastReceivedMessage.getOneWithIdSubType());
		assertNull(callback.lastReceivedMessage.getListWithIdSubTypes());

		// oneWithoutIdSubTypes
		assertNotNull(callback.lastReceivedMessage.getOneWithoutIdSubType());
		assertEquals("the name", callback.lastReceivedMessage.getOneWithoutIdSubType().getName());

		// listWithoutIdSubTypes
		assertEquals(2, callback.lastReceivedMessage.getListWithoutIdSubTypes().size());
		assertEquals("name0", callback.lastReceivedMessage.getListWithoutIdSubTypes().get(0).getName());
		assertEquals("name1", callback.lastReceivedMessage.getListWithoutIdSubTypes().get(1).getName());
	}

	@Test
	public void test_GraphQLVariables_allGraphQLCasesParam_asGraphQLVariables()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, InterruptedException {
		// Preparation
		logger.info("------------------------------------------------------------------------------------------------");
		logger.info("Starting test_GraphQLVariables_allGraphQLCasesParam_asGraphQLVariables");
		SubscriptionCallbackGeneric<CTP_AllFieldCases_CTS> callback = new SubscriptionCallbackGeneric<>(
				"test_GraphQLVariables_allGraphQLCasesInput");

		GraphQLRequestAllGraphQLCases subscriptionRequest = subscriptionExecutor.getGraphQLRequest(
				"subscription sub($nbItems: Long!, $id: String!, $name: String!, $age: Long!, $i: Int!, $date: MyCustomScalarForADate!, $dates: [MyCustomScalarForADate!]!, $matrix: [[Float]!]!, $oneWithoutIdSubtype: AllFieldCasesWithoutIdSubtypeInput!, $listWithoutIdSubtype: [AllFieldCasesWithoutIdSubtypeInput!]!)"
						+ "{allGraphQLCasesParam(id: $id, name: $name, age: $age, integer: $i, date: $date, dates: $dates, matrix: $matrix, oneWithoutIdSubtype: $oneWithoutIdSubtype, listWithoutIdSubtype: $listWithoutIdSubtype)"
						+ "{id name age date dates matrix oneWithoutIdSubType listWithoutIdSubTypes(nbItems: $nbItems)}}");

		// Go, go, go
		@SuppressWarnings("unused")
		SubscriptionClient sub = subscriptionRequest.execSubscription(callback, CTP_AllFieldCases_CTS.class, //
				"nbItems", nbItems, //
				"id", id, //
				"name", name, //
				"age", age, //
				"i", i, //
				"date", date, //
				"dates", dates, //
				"matrix", matrix, //
				"oneWithoutIdSubtype", oneWithoutIdSubtype, //
				"listWithoutIdSubtype", listWithoutIdSubtype);

		// Verification

		// Let's wait a max of 20 seconds, until we receive a first notification
		// (20s will never occur... unless using the debugger to undebug some stuff)
		callback.latchForMessageReception.await(20, TimeUnit.SECONDS);

		assertNull(callback.lastExceptionReceived, (callback.lastExceptionReceived == null) ? //
				"No error"//
				: "lastExceptionReceived should be null, but is " + callback.lastExceptionReceived.getClass().getName()
						+ ": " + callback.lastExceptionReceived.getMessage()//
		);
		assertNotNull(callback.lastReceivedMessage);

		// Let's wait for the subscription to close (the server will send back only one item)
		callback.latchForCompleteReception.await(20, TimeUnit.SECONDS);
		assertNull(callback.lastExceptionReceived);
		assertNull(callback.closureReason, "Null since 2.0");

		// Check of the received values
		assertNotNull(callback.lastReceivedMessage);
		assertEquals(name, callback.lastReceivedMessage.getName());
		assertEquals(age, callback.lastReceivedMessage.getAge());
		assertEquals(date, callback.lastReceivedMessage.getDate());

		// matrix
		assertEquals(3, callback.lastReceivedMessage.getMatrix().size());
		assertArrayEquals(list0, callback.lastReceivedMessage.getMatrix().get(0).toArray());
		assertArrayEquals(list1, callback.lastReceivedMessage.getMatrix().get(1).toArray());
		assertArrayEquals(list2, callback.lastReceivedMessage.getMatrix().get(2).toArray());

		// WithIdSubTypes have not been given: they should be null
		assertNull(callback.lastReceivedMessage.getOneWithIdSubType());
		assertNull(callback.lastReceivedMessage.getListWithIdSubTypes());

		// oneWithoutIdSubTypes
		assertNotNull(callback.lastReceivedMessage.getOneWithoutIdSubType());
		assertEquals("the name", callback.lastReceivedMessage.getOneWithoutIdSubType().getName());

		// listWithoutIdSubTypes
		assertEquals(2, callback.lastReceivedMessage.getListWithoutIdSubTypes().size());
		assertEquals("name0", callback.lastReceivedMessage.getListWithoutIdSubTypes().get(0).getName());
		assertEquals("name1", callback.lastReceivedMessage.getListWithoutIdSubTypes().get(1).getName());
	}
}
