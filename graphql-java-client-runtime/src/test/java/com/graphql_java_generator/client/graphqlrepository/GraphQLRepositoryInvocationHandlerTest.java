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
	@Mock
	GraphQlClient webSocketGraphQlClientMySchema;
	@Spy
	protected MyQueryTypeExecutorMySchema spyQueryExecutor;
	@Spy
	protected AnotherMutationTypeExecutorMySchema spyMutationExecutor;
	@Spy
	protected TheSubscriptionTypeExecutorMySchema spySubscriptionExecutor;

	@BeforeEach
	void beforeEach() throws GraphQLRequestPreparationException {

		SpringContextBean.setApplicationContext(this.applicationContext);

		when(this.applicationContext.getBean("httpGraphQlClientMySchema", GraphQlClient.class)) //$NON-NLS-1$
				.thenReturn(this.httpGraphQlClientMySchema);
		when(this.applicationContext.getBean("webSocketGraphQlClientMySchema", GraphQlClient.class)) //$NON-NLS-1$
				.thenReturn(this.webSocketGraphQlClientMySchema);

		Map<String, GraphQLQueryExecutor> queries = new HashMap<>();
		queries.put("a bean name", this.spyQueryExecutor); //$NON-NLS-1$
		when(this.applicationContext.getBeansOfType(GraphQLQueryExecutor.class)).thenReturn(queries);

		Map<String, GraphQLMutationExecutor> mutations = new HashMap<>();
		mutations.put("a bean name", this.spyMutationExecutor); //$NON-NLS-1$
		when(this.applicationContext.getBeansOfType(GraphQLMutationExecutor.class)).thenReturn(mutations);

		Map<String, GraphQLSubscriptionExecutor> subscriptions = new HashMap<>();
		subscriptions.put("a bean name", this.spySubscriptionExecutor); //$NON-NLS-1$
		when(this.applicationContext.getBeansOfType(GraphQLSubscriptionExecutor.class)).thenReturn(subscriptions);

		// We need that the package of our spy is the package of the GraphQLQueryExecutor interface
		// when(spyQueryExecutor.getClass()).thenReturn(GraphQLQueryExecutor.class);

		// The invocationHandler is created, based on the given context
		this.invocationHandler = new GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCase>(
				GraphQLRepositoryTestCase.class, this.applicationContext);
		this.graphQLRepository = this.invocationHandler.getProxyInstance();
	}

	@Test
	void testConstructor() {
		// Preparation (done in beforeEach)

		// Verification
		assertEquals(this.spyQueryExecutor, this.invocationHandler.queryExecutor);
		assertEquals(this.spyMutationExecutor, this.invocationHandler.mutationExecutor);
		assertEquals(this.spySubscriptionExecutor, this.invocationHandler.subscriptionExecutor);
	}

	/** The {@link GraphQLRepository} annotation is mandatory on the interface */
	@Test
	void testError_noInterfaceAnnotation() {
		// Go, go, go
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCaseMissingInterfaceAnnotation>(
						GraphQLRepositoryTestCaseMissingInterfaceAnnotation.class, this.applicationContext));

		// Verification
		assertTrue(e.getMessage().contains("one of these annotations: '" + GraphQLRepository.class.getName()), //$NON-NLS-1$
				e.getMessage());
	}

	/** A method with no annotation should raise an error */
	@Test
	void testError_missingMethodAnnotation() {
		// Go, go, go
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCaseMissingMethodAnnotation>(
						GraphQLRepositoryTestCaseMissingMethodAnnotation.class, this.applicationContext));

		// Verification
		assertTrue(e.getMessage().contains("@PartialRequest or @FullRequest"), e.getMessage()); //$NON-NLS-1$
	}

	/** A method that doesn't return the same type as the executor matching method */
	@Test
	void testError_badReturnType() {
		// Go, go, go
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCaseBadReturnType>(
						GraphQLRepositoryTestCaseBadReturnType.class, this.applicationContext));

		// Verification
		assertTrue(
				e.getMessage()
						.contains("should return com.graphql_java_generator.domain.client.allGraphQLCases.Character"), //$NON-NLS-1$
				e.getMessage());
		assertTrue(e.getMessage().contains("but returns java.lang.Integer"), e.getMessage()); //$NON-NLS-1$
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
				"found no Spring Bean of type 'GraphQLQueryExecutor' in the same package as the provided QueryExecutor"), //$NON-NLS-1$
				e.getMessage());
		assertTrue(
				e.getMessage().contains(
						"com.graphql_java_generator.client.graphqlrepository.GraphQLRepositoryTestCaseBadExecutor"), //$NON-NLS-1$
				e.getMessage());
		assertTrue(
				e.getMessage().contains(
						"com.graphql_java_generator.domain.client.allGraphQLCases.MyQueryTypeExecutorMySchema"), //$NON-NLS-1$
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
						GraphQLRepositoryTestCaseMissingException.class, this.applicationContext));

		// Verification
		assertTrue(e.getMessage().contains("'com.graphql_java_generator.exception.GraphQLRequestExecutionException'"), //$NON-NLS-1$
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
						GraphQLRepositoryTestCaseParameterWithMap.class, this.applicationContext));

		// Verification
		assertTrue(e.getMessage().contains("Map and vararg (Object[]) are not allowed."), e.getMessage()); //$NON-NLS-1$
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
						GraphQLRepositoryTestCaseParameterWithVararg.class, this.applicationContext));

		// Verification
		assertTrue(e.getMessage().contains("Map and vararg (Object[]) are not allowed."), e.getMessage()); //$NON-NLS-1$
	}

	@Test
	void testInvoke_floatInsteadDoubleParameterType()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
		// Go, go, go
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCaseParameterWithFloatParam>(
						GraphQLRepositoryTestCaseParameterWithFloatParam.class, this.applicationContext));

		// Verification
		assertTrue(e.getMessage().contains(
				"Float and float parameter types are not allowed. Please note that the GraphQL Float type maps to the Double java type."), //$NON-NLS-1$
				e.getMessage());
	}

	/** no requestName: the method name is the field name of the query type in the GraphQL schema */
	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_partialRequest_noRequestName_withObjectArray() throws GraphQLRequestExecutionException,
			GraphQLRequestPreparationException, NoSuchMethodException, SecurityException {
		// Preparation
		CharacterInput input = new CharacterInput();
		Human h = Human.builder().withId("1").withHomePlanet("home planet").withName(" a name ").build(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		doReturn(h).when(this.spyQueryExecutor).withOneOptionalParamWithBindValues(any(ObjectResponse.class),
				any(CharacterInput.class), any(Map.class));

		// Go, go, go
		Character verif = this.graphQLRepository.withOneOptionalParam(input);

		// Verification
		assertEquals(h, verif);
		assertEquals("{appearsIn name}", GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(this.invocationHandler, //$NON-NLS-1$
				GraphQLRepositoryTestCase.class, "withOneOptionalParam", CharacterInput.class)); //$NON-NLS-1$
	}

	/** with requestName: the method name is the field name of the query type in the GraphQL schema */
	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_partialRequest_withRequestName_withObjectArray() throws GraphQLRequestExecutionException,
			GraphQLRequestPreparationException, NoSuchMethodException, SecurityException {
		// Preparation
		CharacterInput input = new CharacterInput();
		Human h = Human.builder().withId("1").withHomePlanet("home planet").withName(" a name ").build(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		doReturn(h).when(this.spyQueryExecutor).withOneOptionalParamWithBindValues(any(ObjectResponse.class),
				any(CharacterInput.class), any(Map.class));

		// Go, go, go
		Character verif = this.graphQLRepository.thisIsNotARequestName1(input);

		// Verification
		assertEquals(h, verif);
		assertEquals("{appearsIn name id}", //$NON-NLS-1$
				GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(this.invocationHandler,
						GraphQLRepositoryTestCase.class, "thisIsNotARequestName1", CharacterInput.class)); //$NON-NLS-1$
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
		doReturn(allFieldCases).when(this.spyQueryExecutor).allFieldCasesWithBindValues(any(ObjectResponse.class),
				any(AllFieldCasesInput.class), any(Map.class));

		// Go, go, go
		AllFieldCases verif = this.graphQLRepository.thisIsNotARequestName3(input, 666, fieldParameterInput);

		ArgumentCaptor<AllFieldCasesInput> inputCaptor = ArgumentCaptor.forClass(AllFieldCasesInput.class);
		ArgumentCaptor<Map<String, Object>> bindParamsCaptor = ArgumentCaptor.forClass(Map.class);
		verify(this.spyQueryExecutor).allFieldCasesWithBindValues(any(ObjectResponse.class), inputCaptor.capture(),
				bindParamsCaptor.capture());
		//
		assertEquals(input, inputCaptor.getValue());
		//
		List<Map<String, Object>> params = bindParamsCaptor.getAllValues();
		assertEquals(1, params.size(), "one invocation"); //$NON-NLS-1$
		assertEquals(2, params.get(0).keySet().size(), "two bind parameters"); //$NON-NLS-1$
		//
		assertTrue(params.get(0).keySet().contains("nbItemsParam")); //$NON-NLS-1$
		assertTrue(params.get(0).keySet().contains("fieldParameterInput")); //$NON-NLS-1$
		//
		assertEquals((long) 666, params.get(0).get("nbItemsParam")); //$NON-NLS-1$
		assertEquals(fieldParameterInput, params.get(0).get("fieldParameterInput")); //$NON-NLS-1$

		// Verification
		assertEquals(allFieldCases, verif);
		assertEquals("{listWithoutIdSubTypes(nbItems: &nbItemsParam, input:?fieldParameterInput)}", //$NON-NLS-1$
				GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(this.invocationHandler,
						GraphQLRepositoryTestCase.class, "thisIsNotARequestName3", AllFieldCasesInput.class, long.class, //$NON-NLS-1$
						FieldParameterInput.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_partialRequest_withRequestName_withoutObjectArray()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
		// Preparation
		CharacterInput input = new CharacterInput();
		Human h = Human.builder().withId("1").withHomePlanet("home planet").withName(" a name ").build(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		doReturn(h).when(this.spyQueryExecutor).withOneOptionalParamWithBindValues(any(ObjectResponse.class),
				any(CharacterInput.class), any(Map.class));

		// Go, go, go
		Character verif = this.graphQLRepository.thisIsNotARequestName2(input);

		// Verification
		assertEquals(h, verif);
		assertEquals("{id name appearsIn}", //$NON-NLS-1$
				GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(this.invocationHandler,
						GraphQLRepositoryTestCase.class, "thisIsNotARequestName2", CharacterInput.class)); //$NON-NLS-1$
	}

	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_partialRequest_withIntParamAndReturnType()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
		// Preparation
		doReturn(666).when(this.spyQueryExecutor)
				.withOneMandatoryParamDefaultValueWithBindValues(any(ObjectResponse.class), eq(123), any(Map.class));

		// Go, go, go
		int verif = this.graphQLRepository.withIntParamAndReturnType(123);

		// Verification
		assertEquals(666, verif);
		assertEquals("", GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(this.invocationHandler, //$NON-NLS-1$
				GraphQLRepositoryTestCase.class, "withIntParamAndReturnType", int.class)); //$NON-NLS-1$
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
		doReturn(Double.valueOf(1.2)).when(this.spyQueryExecutor).issue82FloatWithBindValues(any(ObjectResponse.class),
				eq(1.1), any(Map.class));

		// Go, go, go
		Double verif = this.graphQLRepository.withDoubleParamAndReturnType(1.1);

		// Verification
		assertEquals(1.2, verif);
		assertEquals("", GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(this.invocationHandler, //$NON-NLS-1$
				GraphQLRepositoryTestCase.class, "withDoubleParamAndReturnType", double.class)); //$NON-NLS-1$
	}

	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_partialRequest_withListParam()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {

		// Preparation
		AllFieldCases expected = new AllFieldCases();
		doReturn(expected).when(this.spyQueryExecutor).withListOfListWithBindValues(any(ObjectResponse.class),
				any(List.class), any(Map.class));

		// Go, go, go
		AllFieldCases verif = this.graphQLRepository.withListParam(Arrays.asList(Arrays.asList((float) 1.1)));

		// Verification
		assertEquals(expected, verif);
		assertEquals("", GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(this.invocationHandler, //$NON-NLS-1$
				GraphQLRepositoryTestCase.class, "withListParam", List.class)); //$NON-NLS-1$
	}

	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_partialRequest_mutation_withoutRequestName()
			throws NoSuchMethodException, SecurityException, GraphQLRequestExecutionException {
		// Preparation
		HumanInput input = new HumanInput();
		Human h = Human.builder().withId("1").withHomePlanet("home planet").withName(" a name ").build(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		doReturn(h).when(this.spyMutationExecutor).createHumanWithBindValues(any(ObjectResponse.class),
				any(HumanInput.class), any(Map.class));

		// Go, go, go
		Character verif = this.graphQLRepository.createHuman(input);

		// Verification
		assertEquals(h, verif);
		assertEquals("{id}", GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(this.invocationHandler, //$NON-NLS-1$
				GraphQLRepositoryTestCase.class, "createHuman", HumanInput.class)); //$NON-NLS-1$
	}

	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_partialRequest_mutation_withRequestName()
			throws NoSuchMethodException, SecurityException, GraphQLRequestExecutionException {
		// Preparation
		HumanInput input = new HumanInput();
		Human h = Human.builder().withId("1").withHomePlanet("home planet").withName(" a name ").build(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		doReturn(h).when(this.spyMutationExecutor).createHumanWithBindValues(any(ObjectResponse.class),
				any(HumanInput.class), any(Map.class));

		// Go, go, go
		Character verif = this.graphQLRepository.thisIsAMutation(input);

		// Verification
		assertEquals(h, verif);
		assertEquals("{id name}", GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(this.invocationHandler, //$NON-NLS-1$
				GraphQLRepositoryTestCase.class, "thisIsAMutation", HumanInput.class)); //$NON-NLS-1$
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
		doReturn(subscriptionClient).when(this.spySubscriptionExecutor).subscribeNewHumanForEpisodeWithBindValues(
				any(ObjectResponse.class), any(SubscriptionCallback.class), any(Episode.class), any(Map.class));

		// Go, go, go
		SubscriptionClient verif = this.graphQLRepository.subscribeNewHumanForEpisode(callback, episode);

		// Verification
		assertEquals(subscriptionClient, verif);
		assertEquals("{ id   appearsIn }", //$NON-NLS-1$
				GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(this.invocationHandler,
						GraphQLRepositoryTestCase.class, "subscribeNewHumanForEpisode", SubscriptionCallback.class, //$NON-NLS-1$
						Episode.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_fullRequest_query()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
		// Preparation
		MyQueryTypeResponse queryType = new MyQueryTypeResponse();
		doReturn(queryType).when(this.spyQueryExecutor).execWithBindValues(any(ObjectResponse.class), any(Map.class));

		// Go, go, go
		MyQueryType verif = this.graphQLRepository.fullRequest1(true, "aBindParameterValue"); //$NON-NLS-1$

		// Verification
		assertEquals(queryType, verif);

		ArgumentCaptor<Map<String, Object>> bindParamsCaptor = ArgumentCaptor.forClass(Map.class);
		verify(this.spyQueryExecutor).execWithBindValues(any(ObjectResponse.class), bindParamsCaptor.capture());
		List<Map<String, Object>> params = bindParamsCaptor.getAllValues();
		assertEquals(1, params.size(), "one invocation"); //$NON-NLS-1$
		assertEquals(2, params.get(0).keySet().size());
		//
		assertTrue(params.get(0).keySet().contains("uppercase")); //$NON-NLS-1$
		assertTrue(params.get(0).keySet().contains("valueParam")); //$NON-NLS-1$
		// Mockito changes the List<Object[]> objects to a List<String> at runtime:
		assertEquals(true, params.get(0).get("uppercase")); //$NON-NLS-1$
		assertEquals("aBindParameterValue", params.get(0).get("valueParam")); //$NON-NLS-1$ //$NON-NLS-2$

		assertEquals("{directiveOnQuery  (uppercase: ?uppercase) @testDirective(value:&valueParam)}", //$NON-NLS-1$
				GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(this.invocationHandler,
						GraphQLRepositoryTestCase.class, "fullRequest1", boolean.class, String.class)); //$NON-NLS-1$
	}

	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_fullRequest_query_withRequestType()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
		// Preparation
		MyQueryTypeResponse queryType = new MyQueryTypeResponse();
		doReturn(queryType).when(this.spyQueryExecutor).execWithBindValues(any(ObjectResponse.class), any(Map.class));

		// Go, go, go
		MyQueryType verif = this.graphQLRepository.fullRequest2("aBindParameterValue", true); //$NON-NLS-1$

		// Verification
		assertEquals(queryType, verif);

		ArgumentCaptor<Map<String, Object>> bindParamsCaptor = ArgumentCaptor.forClass(Map.class);
		verify(this.spyQueryExecutor).execWithBindValues(any(ObjectResponse.class), bindParamsCaptor.capture());
		List<Map<String, Object>> params = bindParamsCaptor.getAllValues();
		assertEquals(1, params.size(), "one invocation"); //$NON-NLS-1$
		assertEquals(2, params.get(0).keySet().size());
		//
		assertTrue(params.get(0).keySet().contains("uppercase")); //$NON-NLS-1$
		assertTrue(params.get(0).keySet().contains("valueParam")); //$NON-NLS-1$
		// Mockito changes the List<Object[]> objects to a List<String> at runtime:
		assertEquals(true, params.get(0).get("uppercase")); //$NON-NLS-1$
		assertEquals("aBindParameterValue", params.get(0).get("valueParam")); //$NON-NLS-1$ //$NON-NLS-2$

		assertEquals("{directiveOnQuery  (uppercase: ?uppercase) @testDirective(value:&valueParam)}", //$NON-NLS-1$
				GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(this.invocationHandler,
						GraphQLRepositoryTestCase.class, "fullRequest2", String.class, boolean.class)); //$NON-NLS-1$
	}

	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_fullRequest_mutation()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
		// Preparation
		AnotherMutationTypeResponse mutation = new AnotherMutationTypeResponse();
		HumanInput input = HumanInput.builder().withName("the name") //$NON-NLS-1$
				.withAppearsIn(Arrays.asList(Episode.JEDI, Episode.NEWHOPE)).build();
		doReturn(mutation).when(this.spyMutationExecutor).execWithBindValues(any(ObjectResponse.class), any(Map.class));

		// Go, go, go
		AnotherMutationType verif = this.graphQLRepository.fullRequestMutation(input);

		// Verification
		assertEquals(mutation, verif);

		ArgumentCaptor<Map<String, Object>> bindParamsCaptor = ArgumentCaptor.forClass(Map.class);
		verify(this.spyMutationExecutor).execWithBindValues(any(ObjectResponse.class), bindParamsCaptor.capture());
		List<Map<String, Object>> params = bindParamsCaptor.getAllValues();
		assertEquals(1, params.size(), "one invocation"); //$NON-NLS-1$
		assertEquals(1, params.get(0).keySet().size(), "one parameter"); //$NON-NLS-1$
		//
		assertTrue(params.get(0).keySet().contains("input")); //$NON-NLS-1$
		// Mockito changes the List<Object[]> objects to a List<String> at runtime:
		assertEquals(input, params.get(0).get("input")); //$NON-NLS-1$

		assertEquals("mutation($input: HumanInput!) {createHuman(human: $input) {id name }}", //$NON-NLS-1$
				GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(this.invocationHandler,
						GraphQLRepositoryTestCase.class, "fullRequestMutation", HumanInput.class)); //$NON-NLS-1$
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
		doReturn(client).when(this.spySubscriptionExecutor).execWithBindValues(any(ObjectResponse.class),
				any(SubscriptionCallback.class), any(Map.class));

		// Go, go, go
		this.graphQLRepository.fullSubscription(callback, date);

		// Verification
		assertNotNull(client);

		ArgumentCaptor<Map<String, Object>> bindParamsCaptor = ArgumentCaptor.forClass(Map.class);
		verify(this.spySubscriptionExecutor).execWithBindValues(any(ObjectResponse.class),
				any(SubscriptionCallback.class), bindParamsCaptor.capture());
		List<Map<String, Object>> params = bindParamsCaptor.getAllValues();
		assertEquals(1, params.size(), "one invocation"); //$NON-NLS-1$
		assertEquals(1, params.get(0).keySet().size(), "one parameter"); //$NON-NLS-1$
		//
		assertTrue(params.get(0).keySet().contains("date")); //$NON-NLS-1$
		// Mockito changes the List<Object[]> objects to a List<String> at runtime:
		assertEquals(date, params.get(0).get("date")); //$NON-NLS-1$

		assertEquals("subscription {issue53(date: &date) {}}", //$NON-NLS-1$
				GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(this.invocationHandler,
						GraphQLRepositoryTestCase.class, "fullSubscription", SubscriptionCallback.class, Date.class)); //$NON-NLS-1$
	}

}
