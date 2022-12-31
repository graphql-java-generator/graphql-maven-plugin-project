/**
 * 
 */
package com.graphql_java_generator.client.graphqlrepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.graphql.client.GraphQlClient;

import com.graphql_java_generator.client.GraphQLMutationExecutor;
import com.graphql_java_generator.client.GraphQLQueryExecutor;
import com.graphql_java_generator.client.GraphQLSubscriptionExecutor;
import com.graphql_java_generator.client.SpringContextBean;
import com.graphql_java_generator.client.SubscriptionCallback;
import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.client.SubscriptionClientReactiveImpl;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.domain.client.allGraphQLCases.AllFieldCases;
import com.graphql_java_generator.domain.client.allGraphQLCases.AllFieldCasesInput;
import com.graphql_java_generator.domain.client.allGraphQLCases.AnotherMutationType;
import com.graphql_java_generator.domain.client.allGraphQLCases.AnotherMutationTypeExecutorMySchema;
import com.graphql_java_generator.domain.client.allGraphQLCases.AnotherMutationTypeResponse;
import com.graphql_java_generator.domain.client.allGraphQLCases.Character;
import com.graphql_java_generator.domain.client.allGraphQLCases.CharacterInput;
import com.graphql_java_generator.domain.client.allGraphQLCases.Episode;
import com.graphql_java_generator.domain.client.allGraphQLCases.FieldParameterInput;
import com.graphql_java_generator.domain.client.allGraphQLCases.Human;
import com.graphql_java_generator.domain.client.allGraphQLCases.HumanInput;
import com.graphql_java_generator.domain.client.allGraphQLCases.MyQueryType;
import com.graphql_java_generator.domain.client.allGraphQLCases.MyQueryTypeExecutorMySchema;
import com.graphql_java_generator.domain.client.allGraphQLCases.MyQueryTypeResponse;
import com.graphql_java_generator.domain.client.allGraphQLCases.TheSubscriptionTypeExecutorMySchema;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * This class is the superclass for the two GraphQLRepositoryInvocationHandlerTest test classes. It contains each test
 * that doesnt depend on the constructor, so that each of this test is executed against instances of
 * {@link GraphQLRepositoryInvocationHandler} created by each of its two constructors.
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(MockitoExtension.class)
public class GraphQLRepositoryInvocationHandlerTest {

	protected GraphQLRepositoryTestCase graphQLRepository;
	protected GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCase> invocationHandler;

	// There seems to be issues with mock, probably because of the way the InvocationHandler calls the Executor, through
	// java reflection
	// So we use @Spy here, instead of @Mock
	// CAUTION: the changes the way to stub method. Use doReturn().when(spy).methodToStub() syntax
	@Mock
	ApplicationContext applicationContext;
	@Mock
	GraphQlClient httpGraphQlClientMySchema;
	@Spy
	protected MyQueryTypeExecutorMySchema spyQueryExecutor;
	@Spy
	protected AnotherMutationTypeExecutorMySchema spyMutationExecutor;
	@Spy
	protected TheSubscriptionTypeExecutorMySchema spySubscriptionExecutor;

	@BeforeEach
	void beforeEach() throws GraphQLRequestPreparationException {

		SpringContextBean.setApplicationContext(applicationContext);

		when(applicationContext.getBean("httpGraphQlClientMySchema", GraphQlClient.class))
				.thenReturn(httpGraphQlClientMySchema);

		Map<String, GraphQLQueryExecutor> queries = new HashMap<>();
		queries.put("a bean name", spyQueryExecutor);
		when(applicationContext.getBeansOfType(GraphQLQueryExecutor.class)).thenReturn(queries);

		Map<String, GraphQLMutationExecutor> mutations = new HashMap<>();
		mutations.put("a bean name", spyMutationExecutor);
		when(applicationContext.getBeansOfType(GraphQLMutationExecutor.class)).thenReturn(mutations);

		Map<String, GraphQLSubscriptionExecutor> subscriptions = new HashMap<>();
		subscriptions.put("a bean name", spySubscriptionExecutor);
		when(applicationContext.getBeansOfType(GraphQLSubscriptionExecutor.class)).thenReturn(subscriptions);

		// We need that the package of our spy is the package of the GraphQLQueryExecutor interface
		// when(spyQueryExecutor.getClass()).thenReturn(GraphQLQueryExecutor.class);

		// The invocationHandler is created, based on the given context
		invocationHandler = new GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCase>(
				GraphQLRepositoryTestCase.class, applicationContext);
		graphQLRepository = invocationHandler.getProxyInstance();
	}

	@Test
	void testConstructor() {
		// Preparation (done in beforeEach)

		// Verification
		assertEquals(spyQueryExecutor, invocationHandler.queryExecutor);
		assertEquals(spyMutationExecutor, invocationHandler.mutationExecutor);
		assertEquals(spySubscriptionExecutor, invocationHandler.subscriptionExecutor);
	}

	/** The {@link GraphQLRepository} annotation is mandatory on the interface */
	@Test
	void testError_noInterfaceAnnotation() {
		// Go, go, go
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCaseMissingInterfaceAnnotation>(
						GraphQLRepositoryTestCaseMissingInterfaceAnnotation.class, applicationContext));

		// Verification
		assertTrue(e.getMessage().contains(GraphQLRepository.class.getName() + "' annotation"), e.getMessage());
	}

	/** A method with no annotation should raise an error */
	@Test
	void testError_missingMethodAnnotation() {
		// Go, go, go
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCaseMissingMethodAnnotation>(
						GraphQLRepositoryTestCaseMissingMethodAnnotation.class, applicationContext));

		// Verification
		assertTrue(e.getMessage().contains("@PartialRequest or @FullRequest"), e.getMessage());
	}

	/** A method that doesn't return the same type as the executor matching method */
	@Test
	void testError_badReturnType() {
		// Go, go, go
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCaseBadReturnType>(
						GraphQLRepositoryTestCaseBadReturnType.class, applicationContext));

		// Verification
		assertTrue(
				e.getMessage()
						.contains("should return com.graphql_java_generator.domain.client.allGraphQLCases.Character"),
				e.getMessage());
		assertTrue(e.getMessage().contains("but returns java.lang.Integer"), e.getMessage());
	}

	/** Tests the error message when a bad query executor is given in the GraphQLRepository annotation */
	@Test
	void testError_badQueryExecutor() {
		// Preparation
		Map<String, Object> values = new HashMap<>(); // an emtpy map is enough for this test, like if no bean where
														// registered
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		when(applicationContext.getBeansOfType(any())).thenReturn(values);

		// Go, go, go
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> new GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCaseBadExecutor>(
						GraphQLRepositoryTestCaseBadExecutor.class, applicationContext));

		// Verification
		assertTrue(e.getMessage().contains(
				"found no Spring Bean of type 'GraphQLQueryExecutor' in the same package as the provided QueryExecutor"),
				e.getMessage());
		assertTrue(
				e.getMessage().contains(
						"com.graphql_java_generator.client.graphqlrepository.GraphQLRepositoryTestCaseBadExecutor"),
				e.getMessage());
		assertTrue(
				e.getMessage().contains(
						"com.graphql_java_generator.domain.client.allGraphQLCases.MyQueryTypeExecutorMySchema"),
				e.getMessage());
	}

	/**
	 * A method with no annotation should raise an error
	 * 
	 * @throws GraphQLRequestExecutionException
	 */
	@Test
	void testError_noGraphQLRequestExecutionException() throws GraphQLRequestExecutionException {
		// Go, go, go
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCaseMissingException>(
						GraphQLRepositoryTestCaseMissingException.class, applicationContext));

		// Verification
		assertTrue(e.getMessage().contains("'com.graphql_java_generator.exception.GraphQLRequestExecutionException'"),
				e.getMessage());
	}

	/**
	 * A method that contains a Map parameter is not allowed
	 * 
	 * @throws GraphQLRequestExecutionException
	 */
	@Test
	void testError_withMapParameter() throws GraphQLRequestExecutionException {
		// Go, go, go
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCaseParameterWithMap>(
						GraphQLRepositoryTestCaseParameterWithMap.class, applicationContext));

		// Verification
		assertTrue(e.getMessage().contains("Map and vararg (Object[]) are not allowed."), e.getMessage());
	}

	/**
	 * A method that contains an Object... parameter is not allowed
	 * 
	 * @throws GraphQLRequestExecutionException
	 */
	@Test
	void testError_withVarArgParameter() throws GraphQLRequestExecutionException {
		// Go, go, go
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCaseParameterWithVararg>(
						GraphQLRepositoryTestCaseParameterWithVararg.class, applicationContext));

		// Verification
		assertTrue(e.getMessage().contains("Map and vararg (Object[]) are not allowed."), e.getMessage());
	}

	@Test
	void testInvoke_floatInsteadDoubleParameterType()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
		// Go, go, go
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCaseParameterWithFloatParam>(
						GraphQLRepositoryTestCaseParameterWithFloatParam.class, applicationContext));

		// Verification
		assertTrue(e.getMessage().contains(
				"Float and float parameter types are not allowed. Please note that the GraphQL Float type maps to the Double java type."),
				e.getMessage());
	}

	/** no requestName: the method name is the field name of the query type in the GraphQL schema */
	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_partialRequest_noRequestName_withObjectArray() throws GraphQLRequestExecutionException,
			GraphQLRequestPreparationException, NoSuchMethodException, SecurityException {
		// Preparation
		CharacterInput input = new CharacterInput();
		Human h = Human.builder().withId("1").withHomePlanet("home planet").withName(" a name ").build();
		doReturn(h).when(spyQueryExecutor).withOneOptionalParamWithBindValues(any(ObjectResponse.class),
				any(CharacterInput.class), any(Map.class));

		// Go, go, go
		Character verif = graphQLRepository.withOneOptionalParam(input);

		// Verification
		assertEquals(h, verif);
		assertEquals("{appearsIn name}", GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(invocationHandler,
				GraphQLRepositoryTestCase.class, "withOneOptionalParam", CharacterInput.class));
	}

	/** with requestName: the method name is the field name of the query type in the GraphQL schema */
	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_partialRequest_withRequestName_withObjectArray() throws GraphQLRequestExecutionException,
			GraphQLRequestPreparationException, NoSuchMethodException, SecurityException {
		// Preparation
		CharacterInput input = new CharacterInput();
		Human h = Human.builder().withId("1").withHomePlanet("home planet").withName(" a name ").build();
		doReturn(h).when(spyQueryExecutor).withOneOptionalParamWithBindValues(any(ObjectResponse.class),
				any(CharacterInput.class), any(Map.class));

		// Go, go, go
		Character verif = graphQLRepository.thisIsNotARequestName1(input);

		// Verification
		assertEquals(h, verif);
		assertEquals("{appearsIn name id}", GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(invocationHandler,
				GraphQLRepositoryTestCase.class, "thisIsNotARequestName1", CharacterInput.class));
	}

	/** with requestName: the method name is the field name of the query type in the GraphQL schema */
	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_partialRequest_withRequestName_withBindParameterValues() throws GraphQLRequestExecutionException,
			GraphQLRequestPreparationException, NoSuchMethodException, SecurityException {
		// Preparation
		AllFieldCasesInput input = new AllFieldCasesInput();
		FieldParameterInput fieldParameterInput = FieldParameterInput.builder().withUppercase(true).build();
		AllFieldCases allFieldCases = AllFieldCases.builder().withAFloat(123.3).build();
		doReturn(allFieldCases).when(spyQueryExecutor).allFieldCasesWithBindValues(any(ObjectResponse.class),
				any(AllFieldCasesInput.class), any(Map.class));

		// Go, go, go
		AllFieldCases verif = graphQLRepository.thisIsNotARequestName3(input, 666, fieldParameterInput);

		ArgumentCaptor<AllFieldCasesInput> inputCaptor = ArgumentCaptor.forClass(AllFieldCasesInput.class);
		ArgumentCaptor<Map<String, Object>> bindParamsCaptor = ArgumentCaptor.forClass(Map.class);
		verify(spyQueryExecutor).allFieldCasesWithBindValues(any(ObjectResponse.class), inputCaptor.capture(),
				bindParamsCaptor.capture());
		//
		assertEquals(input, inputCaptor.getValue());
		//
		List<Map<String, Object>> params = bindParamsCaptor.getAllValues();
		assertEquals(1, params.size(), "one invocation");
		assertEquals(2, params.get(0).keySet().size(), "two bind parameters");
		//
		assertTrue(params.get(0).keySet().contains("nbItemsParam"));
		assertTrue(params.get(0).keySet().contains("fieldParameterInput"));
		//
		assertEquals((long) 666, params.get(0).get("nbItemsParam"));
		assertEquals(fieldParameterInput, params.get(0).get("fieldParameterInput"));

		// Verification
		assertEquals(allFieldCases, verif);
		assertEquals("{listWithoutIdSubTypes(nbItems: &nbItemsParam, input:?fieldParameterInput)}",
				GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(invocationHandler,
						GraphQLRepositoryTestCase.class, "thisIsNotARequestName3", AllFieldCasesInput.class, long.class,
						FieldParameterInput.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_partialRequest_withRequestName_withoutObjectArray()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
		// Preparation
		CharacterInput input = new CharacterInput();
		Human h = Human.builder().withId("1").withHomePlanet("home planet").withName(" a name ").build();
		doReturn(h).when(spyQueryExecutor).withOneOptionalParamWithBindValues(any(ObjectResponse.class),
				any(CharacterInput.class), any(Map.class));

		// Go, go, go
		Character verif = graphQLRepository.thisIsNotARequestName2(input);

		// Verification
		assertEquals(h, verif);
		assertEquals("{id name appearsIn}", GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(invocationHandler,
				GraphQLRepositoryTestCase.class, "thisIsNotARequestName2", CharacterInput.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_partialRequest_withIntParamAndReturnType()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
		// Preparation
		doReturn(666).when(spyQueryExecutor).withOneMandatoryParamDefaultValueWithBindValues(any(ObjectResponse.class),
				eq(123), any(Map.class));

		// Go, go, go
		int verif = graphQLRepository.withIntParamAndReturnType(123);

		// Verification
		assertEquals(666, verif);
		assertEquals("", GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(invocationHandler,
				GraphQLRepositoryTestCase.class, "withIntParamAndReturnType", int.class));
	}

	@Test
	void testInvoke_partialRequest_withBooleanParam()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {

	}

	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_partialRequest_withFloatParamAndReturnType()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
		// Preparation
		doReturn(Double.valueOf(1.2)).when(spyQueryExecutor).issue82FloatWithBindValues(any(ObjectResponse.class),
				eq(1.1), any(Map.class));

		// Go, go, go
		Double verif = graphQLRepository.withDoubleParamAndReturnType(1.1);

		// Verification
		assertEquals(1.2, verif);
		assertEquals("", GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(invocationHandler,
				GraphQLRepositoryTestCase.class, "withDoubleParamAndReturnType", double.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_partialRequest_withListParam()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {

		// Preparation
		AllFieldCases expected = new AllFieldCases();
		doReturn(expected).when(spyQueryExecutor).withListOfListWithBindValues(any(ObjectResponse.class),
				any(List.class), any(Map.class));

		// Go, go, go
		AllFieldCases verif = graphQLRepository.withListParam(Arrays.asList(Arrays.asList((float) 1.1)));

		// Verification
		assertEquals(expected, verif);
		assertEquals("", GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(invocationHandler,
				GraphQLRepositoryTestCase.class, "withListParam", List.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_partialRequest_mutation_withoutRequestName()
			throws NoSuchMethodException, SecurityException, GraphQLRequestExecutionException {
		// Preparation
		HumanInput input = new HumanInput();
		Human h = Human.builder().withId("1").withHomePlanet("home planet").withName(" a name ").build();
		doReturn(h).when(spyMutationExecutor).createHumanWithBindValues(any(ObjectResponse.class),
				any(HumanInput.class), any(Map.class));

		// Go, go, go
		Character verif = graphQLRepository.createHuman(input);

		// Verification
		assertEquals(h, verif);
		assertEquals("{id}", GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(invocationHandler,
				GraphQLRepositoryTestCase.class, "createHuman", HumanInput.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_partialRequest_mutation_withRequestName()
			throws NoSuchMethodException, SecurityException, GraphQLRequestExecutionException {
		// Preparation
		HumanInput input = new HumanInput();
		Human h = Human.builder().withId("1").withHomePlanet("home planet").withName(" a name ").build();
		doReturn(h).when(spyMutationExecutor).createHumanWithBindValues(any(ObjectResponse.class),
				any(HumanInput.class), any(Map.class));

		// Go, go, go
		Character verif = graphQLRepository.thisIsAMutation(input);

		// Verification
		assertEquals(h, verif);
		assertEquals("{id name}", GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(invocationHandler,
				GraphQLRepositoryTestCase.class, "thisIsAMutation", HumanInput.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_partialRequest_subscription()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
		// Preparation
		SubscriptionCallback<Human> callback = new SubscriptionCallback<Human>() {
			@Override
			public void onConnect() {
			}

			@Override
			public void onMessage(Human t) {
			}

			@Override
			public void onClose(int statusCode, String reason) {
			}

			@Override
			public void onError(Throwable cause) {
			}
		};
		Episode episode = Episode.JEDI;
		SubscriptionClient subscriptionClient = new SubscriptionClientReactiveImpl(null);
		doReturn(subscriptionClient).when(spySubscriptionExecutor).subscribeNewHumanForEpisodeWithBindValues(
				any(ObjectResponse.class), any(SubscriptionCallback.class), any(Episode.class), any(Map.class));

		// Go, go, go
		SubscriptionClient verif = graphQLRepository.subscribeNewHumanForEpisode(callback, episode);

		// Verification
		assertEquals(subscriptionClient, verif);
		assertEquals("{ id   appearsIn }",
				GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(invocationHandler,
						GraphQLRepositoryTestCase.class, "subscribeNewHumanForEpisode", SubscriptionCallback.class,
						Episode.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_fullRequest_query()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
		// Preparation
		MyQueryTypeResponse queryType = new MyQueryTypeResponse();
		doReturn(queryType).when(spyQueryExecutor).execWithBindValues(any(ObjectResponse.class), any(Map.class));

		// Go, go, go
		MyQueryType verif = graphQLRepository.fullRequest1(true, "aBindParameterValue");

		// Verification
		assertEquals(queryType, verif);

		ArgumentCaptor<Map<String, Object>> bindParamsCaptor = ArgumentCaptor.forClass(Map.class);
		verify(spyQueryExecutor).execWithBindValues(any(ObjectResponse.class), bindParamsCaptor.capture());
		List<Map<String, Object>> params = bindParamsCaptor.getAllValues();
		assertEquals(1, params.size(), "one invocation");
		assertEquals(2, params.get(0).keySet().size());
		//
		assertTrue(params.get(0).keySet().contains("uppercase"));
		assertTrue(params.get(0).keySet().contains("valueParam"));
		// Mockito changes the List<Object[]> objects to a List<String> at runtime:
		assertEquals(true, params.get(0).get("uppercase"));
		assertEquals("aBindParameterValue", params.get(0).get("valueParam"));

		assertEquals("{directiveOnQuery  (uppercase: ?uppercase) @testDirective(value:&valueParam)}",
				GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(invocationHandler,
						GraphQLRepositoryTestCase.class, "fullRequest1", boolean.class, String.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_fullRequest_query_withRequestType()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
		// Preparation
		MyQueryTypeResponse queryType = new MyQueryTypeResponse();
		doReturn(queryType).when(spyQueryExecutor).execWithBindValues(any(ObjectResponse.class), any(Map.class));

		// Go, go, go
		MyQueryType verif = graphQLRepository.fullRequest2("aBindParameterValue", true);

		// Verification
		assertEquals(queryType, verif);

		ArgumentCaptor<Map<String, Object>> bindParamsCaptor = ArgumentCaptor.forClass(Map.class);
		verify(spyQueryExecutor).execWithBindValues(any(ObjectResponse.class), bindParamsCaptor.capture());
		List<Map<String, Object>> params = bindParamsCaptor.getAllValues();
		assertEquals(1, params.size(), "one invocation");
		assertEquals(2, params.get(0).keySet().size());
		//
		assertTrue(params.get(0).keySet().contains("uppercase"));
		assertTrue(params.get(0).keySet().contains("valueParam"));
		// Mockito changes the List<Object[]> objects to a List<String> at runtime:
		assertEquals(true, params.get(0).get("uppercase"));
		assertEquals("aBindParameterValue", params.get(0).get("valueParam"));

		assertEquals("{directiveOnQuery  (uppercase: ?uppercase) @testDirective(value:&valueParam)}",
				GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(invocationHandler,
						GraphQLRepositoryTestCase.class, "fullRequest2", String.class, boolean.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_fullRequest_mutation()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
		// Preparation
		AnotherMutationTypeResponse mutation = new AnotherMutationTypeResponse();
		HumanInput input = HumanInput.builder().withName("the name")
				.withAppearsIn(Arrays.asList(Episode.JEDI, Episode.NEWHOPE)).build();
		doReturn(mutation).when(spyMutationExecutor).execWithBindValues(any(ObjectResponse.class), any(Map.class));

		// Go, go, go
		AnotherMutationType verif = graphQLRepository.fullRequestMutation(input);

		// Verification
		assertEquals(mutation, verif);

		ArgumentCaptor<Map<String, Object>> bindParamsCaptor = ArgumentCaptor.forClass(Map.class);
		verify(spyMutationExecutor).execWithBindValues(any(ObjectResponse.class), bindParamsCaptor.capture());
		List<Map<String, Object>> params = bindParamsCaptor.getAllValues();
		assertEquals(1, params.size(), "one invocation");
		assertEquals(1, params.get(0).keySet().size(), "one parameter");
		//
		assertTrue(params.get(0).keySet().contains("input"));
		// Mockito changes the List<Object[]> objects to a List<String> at runtime:
		assertEquals(input, params.get(0).get("input"));

		assertEquals("mutation($input: HumanInput!) {createHuman(human: $input) {id name }}",
				GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(invocationHandler,
						GraphQLRepositoryTestCase.class, "fullRequestMutation", HumanInput.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	@Disabled
	void testInvoke_fullRequest_subscription()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
		// Preparation
		Date date = new Calendar.Builder().setDate(2021, 6 - 1, 18).build().getTime();
		SubscriptionCallback<Date> callback = null;// This would be invalid in real use. Null is just for this test
		SubscriptionClient client = new SubscriptionClientReactiveImpl(null);
		doReturn(client).when(spySubscriptionExecutor).execWithBindValues(any(ObjectResponse.class),
				any(SubscriptionCallback.class), any(Map.class));

		// Go, go, go
		graphQLRepository.fullSubscription(callback, date);

		// Verification
		assertNotNull(client);

		ArgumentCaptor<Map<String, Object>> bindParamsCaptor = ArgumentCaptor.forClass(Map.class);
		verify(spySubscriptionExecutor).execWithBindValues(any(ObjectResponse.class), any(SubscriptionCallback.class),
				bindParamsCaptor.capture());
		List<Map<String, Object>> params = bindParamsCaptor.getAllValues();
		assertEquals(1, params.size(), "one invocation");
		assertEquals(1, params.get(0).keySet().size(), "one parameter");
		//
		assertTrue(params.get(0).keySet().contains("date"));
		// Mockito changes the List<Object[]> objects to a List<String> at runtime:
		assertEquals(date, params.get(0).get("date"));

		assertEquals("subscription {issue53(date: &date) {}}",
				GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(invocationHandler,
						GraphQLRepositoryTestCase.class, "fullSubscription", SubscriptionCallback.class, Date.class));
	}

}
