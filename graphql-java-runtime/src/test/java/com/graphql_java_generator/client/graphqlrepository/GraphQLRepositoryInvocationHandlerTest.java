package com.graphql_java_generator.client.graphqlrepository;

// import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.graphql_java_generator.client.SubscriptionCallback;
import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.client.SubscriptionClientReactiveImpl;
import com.graphql_java_generator.client.domain.allGraphQLCases.AnotherMutationTypeExecutor;
import com.graphql_java_generator.client.domain.allGraphQLCases.Character;
import com.graphql_java_generator.client.domain.allGraphQLCases.CharacterInput;
import com.graphql_java_generator.client.domain.allGraphQLCases.Episode;
import com.graphql_java_generator.client.domain.allGraphQLCases.Human;
import com.graphql_java_generator.client.domain.allGraphQLCases.HumanInput;
import com.graphql_java_generator.client.domain.allGraphQLCases.MyQueryType;
import com.graphql_java_generator.client.domain.allGraphQLCases.MyQueryTypeExecutor;
import com.graphql_java_generator.client.domain.allGraphQLCases.MyQueryTypeResponse;
import com.graphql_java_generator.client.domain.allGraphQLCases.TheSubscriptionTypeExecutor;
import com.graphql_java_generator.client.graphqlrepository.GraphQLRepositoryInvocationHandler.RegisteredMethod;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@SuppressWarnings("deprecation")
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(MockitoExtension.class)
class GraphQLRepositoryInvocationHandlerTest {

	GraphQLRepositoryTestCase graphQLRepository;
	GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCase> invocationHandler;

	// There seems to be issues with mock, probably because of the way the InvocationHandler calls the Executor, through
	// java reflection
	// So we use @Spy here, instead of @Mock
	// CAUTION: the changes the way to stub method. Use doReturn().when(spy).methodToStub() syntax
	@Spy
	MyQueryTypeExecutor spyQueryExecutor;
	@Spy
	AnotherMutationTypeExecutor spyMutationExecutor;
	@Spy
	TheSubscriptionTypeExecutor spySubscriptionExecutor;

	@BeforeEach
	void beforeEach() throws GraphQLRequestPreparationException {
		invocationHandler = new GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCase>(
				GraphQLRepositoryTestCase.class, spyQueryExecutor, spyMutationExecutor, spySubscriptionExecutor);
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
						GraphQLRepositoryTestCaseMissingInterfaceAnnotation.class, spyQueryExecutor,
						spyMutationExecutor, spySubscriptionExecutor));

		// Verification
		assertTrue(e.getMessage().contains("'com.graphql_java_generator.annotation.GraphQLRepository' annotation"),
				e.getMessage());
	}

	/** A method with no annotation should raise an error */
	@Test
	void testError_missingMethodAnnotation() {
		// Go, go, go
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCaseMissingMethodAnnotation>(
						GraphQLRepositoryTestCaseMissingMethodAnnotation.class, spyQueryExecutor, spyMutationExecutor,
						spySubscriptionExecutor));

		// Verification
		assertTrue(e.getMessage().contains("@PartialRequest or @FullRequest"), e.getMessage());
	}

	/** A method that doesn't return the same type as the executor matching method */
	@Test
	void testError_missingBadReturnType() {
		// Go, go, go
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCaseBadReturnType>(
						GraphQLRepositoryTestCaseBadReturnType.class, spyQueryExecutor, spyMutationExecutor,
						spySubscriptionExecutor));

		// Verification
		assertTrue(
				e.getMessage()
						.contains("should return com.graphql_java_generator.client.domain.allGraphQLCases.Character"),
				e.getMessage());
		assertTrue(e.getMessage().contains("but returns java.lang.Integer"), e.getMessage());
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
						GraphQLRepositoryTestCaseMissingException.class, spyQueryExecutor, spyMutationExecutor,
						spySubscriptionExecutor));

		// Verification
		assertTrue(e.getMessage().contains("'com.graphql_java_generator.exception.GraphQLRequestExecutionException'"),
				e.getMessage());
	}

	/** no requestName: the method name is the field name of the query type in the GraphQL schema */
	@Test
	void testInvoke_noRequestName_withObjectArray() throws GraphQLRequestExecutionException,
			GraphQLRequestPreparationException, NoSuchMethodException, SecurityException {
		// Preparation
		CharacterInput input = new CharacterInput();
		Human h = Human.builder().withId("1").withHomePlanet("home planet").withName(" a name ").build();
		doReturn(h).when(spyQueryExecutor).withOneOptionalParam(any(ObjectResponse.class), any(CharacterInput.class));

		// Go, go, go
		Character verif = graphQLRepository.withOneOptionalParam(input);

		// Verification
		assertEquals(h, verif);
		assertEquals("{appearsIn name}",
				getRegisteredGraphQLRequest("withOneOptionalParam", CharacterInput.class, Object[].class));
	}

	/** with requestName: the method name is the field name of the query type in the GraphQL schema */
	@Test
	void testInvoke_withRequestName_withObjectArray() throws GraphQLRequestExecutionException,
			GraphQLRequestPreparationException, NoSuchMethodException, SecurityException {
		// Preparation
		CharacterInput input = new CharacterInput();
		Human h = Human.builder().withId("1").withHomePlanet("home planet").withName(" a name ").build();
		doReturn(h).when(spyQueryExecutor).withOneOptionalParam(any(ObjectResponse.class), any(CharacterInput.class));

		// Go, go, go
		Character verif = graphQLRepository.thisIsNotARequestName1(input);

		// Verification
		assertEquals(h, verif);
		assertEquals("{appearsIn name id}",
				getRegisteredGraphQLRequest("thisIsNotARequestName1", CharacterInput.class, Object[].class));
	}

	/** with requestName: the method name is the field name of the query type in the GraphQL schema */
	@Test
	void testInvoke_withRequestName_withObjectArray_withBindParameterValues() throws GraphQLRequestExecutionException,
			GraphQLRequestPreparationException, NoSuchMethodException, SecurityException {
		fail("not yet implemented");
	}

	@Test
	void testInvoke_withRequestName_withoutObjectArray()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
		// Preparation
		CharacterInput input = new CharacterInput();
		Human h = Human.builder().withId("1").withHomePlanet("home planet").withName(" a name ").build();
		doReturn(h).when(spyQueryExecutor).withOneOptionalParam(any(ObjectResponse.class), any(CharacterInput.class));

		// Go, go, go
		Character verif = graphQLRepository.thisIsNotARequestName2(input);

		// Verification
		assertEquals(h, verif);
		assertEquals("{id name appearsIn}",
				getRegisteredGraphQLRequest("thisIsNotARequestName2", CharacterInput.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_withRequestName_withMap_withRequestType()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
		// Preparation
		CharacterInput input = new CharacterInput();
		Human h = Human.builder().withId("1").withHomePlanet("home planet").withName(" a name ").build();
		doReturn(h).when(spyQueryExecutor).withOneOptionalParamWithBindValues(any(ObjectResponse.class),
				any(CharacterInput.class), any(Map.class));

		// Go, go, go
		Character verif = graphQLRepository.thisIsNotARequestName3(input, new HashMap<String, Object>());

		// Verification
		assertEquals(h, verif);
		assertEquals("{appearsIn id name}",
				getRegisteredGraphQLRequest("thisIsNotARequestName3", CharacterInput.class, Map.class));
	}

	@Test
	void testInvoke_mutation_withoutRequestName()
			throws NoSuchMethodException, SecurityException, GraphQLRequestExecutionException {
		// Preparation
		HumanInput input = new HumanInput();
		Human h = Human.builder().withId("1").withHomePlanet("home planet").withName(" a name ").build();
		doReturn(h).when(spyMutationExecutor).createHuman(any(ObjectResponse.class), any(HumanInput.class));

		// Go, go, go
		Character verif = graphQLRepository.createHuman(input);

		// Verification
		assertEquals(h, verif);
		assertEquals("{id}", getRegisteredGraphQLRequest("createHuman", HumanInput.class));
	}

	@Test
	void testInvoke_mutation_withRequestName()
			throws NoSuchMethodException, SecurityException, GraphQLRequestExecutionException {
		// Preparation
		HumanInput input = new HumanInput();
		Human h = Human.builder().withId("1").withHomePlanet("home planet").withName(" a name ").build();
		doReturn(h).when(spyMutationExecutor).createHuman(any(ObjectResponse.class), any(HumanInput.class));

		// Go, go, go
		Character verif = graphQLRepository.thisIsAMutation(input);

		// Verification
		assertEquals(h, verif);
		assertEquals("{id name}", getRegisteredGraphQLRequest("thisIsAMutation", HumanInput.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testInvoke_subscription() throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
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
		SubscriptionClient subscriptionClient = new SubscriptionClientReactiveImpl(null, null);
		doReturn(subscriptionClient).when(spySubscriptionExecutor).subscribeNewHumanForEpisode(
				any(ObjectResponse.class), any(SubscriptionCallback.class), any(Episode.class));

		// Go, go, go
		SubscriptionClient verif = graphQLRepository.subscribeNewHumanForEpisode(callback, episode);

		// Verification
		assertEquals(subscriptionClient, verif);
		assertEquals("{ id   appearsIn }",
				getRegisteredGraphQLRequest("subscribeNewHumanForEpisode", SubscriptionCallback.class, Episode.class));
	}

	@Test
	void testInvoke_fullRequest_query_withObjectArrayParameter()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
		// Preparation
		MyQueryTypeResponse queryType = new MyQueryTypeResponse();
		doReturn(queryType).when(spyQueryExecutor).exec(any(ObjectResponse.class), ArgumentMatchers.<Object[]>any());

		// Go, go, go
		MyQueryType verif = graphQLRepository.fullQuery1("aBindParameterName", "aBindParameterValue");

		// Verification
		assertEquals(queryType, verif);

		ArgumentCaptor<Object[]> bindParamsCaptor = ArgumentCaptor.forClass(Object[].class);
		verify(spyQueryExecutor).exec(any(ObjectResponse.class), bindParamsCaptor.capture());
		List<Object[]> objects = bindParamsCaptor.getAllValues();
		assertEquals(2, objects.size());
		// Mockito changes the List<Object[]> objects to a List<String> at runtime:
		assertEquals("aBindParameterName", objects.get(0));
		assertEquals("aBindParameterValue", objects.get(1));

		assertEquals("{directiveOnQuery  (uppercase: true) @testDirective(value:&value)}",
				getRegisteredGraphQLRequest("fullQuery1", Object[].class));
	}

	@Test
	void testInvoke_fullRequest_query_withParameter_withoutObjectArray()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
		// Preparation
		MyQueryTypeResponse queryType = new MyQueryTypeResponse();
		doReturn(queryType).when(spyQueryExecutor).exec(any(ObjectResponse.class), any(Object[].class));

		// Go, go, go
		MyQueryType verif = graphQLRepository.fullQuery2("a param value");

		// Verification
		assertEquals(queryType, verif);

		ArgumentCaptor<Object[]> bindParamsCaptor = ArgumentCaptor.forClass(Object[].class);
		verify(spyQueryExecutor).exec(any(ObjectResponse.class), bindParamsCaptor.capture());
		List<Object[]> objects = bindParamsCaptor.getAllValues();
		assertEquals(2, objects.size());
		// Mockito changes the List<Object[]> objects to a List<String> at runtime:
		assertEquals("value", objects.get(0));
		assertEquals("a param value", objects.get(1));

		assertEquals("{directiveOnQuery (uppercase: true) @testDirective(value:&value)}",
				getRegisteredGraphQLRequest("fullQuery2", String.class));
		fail("not yet implemented");
	}

	@Test
	void testInvoke_fullRequest_query_withParameter_withObjectArray()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
		// Preparation
		MyQueryTypeResponse queryType = new MyQueryTypeResponse();
		doReturn(queryType).when(spyQueryExecutor).exec(any(ObjectResponse.class), any(Object[].class));

		// Go, go, go
		MyQueryType verif = graphQLRepository.fullQuery3("a param value");

		// Verification
		assertEquals(queryType, verif);

		ArgumentCaptor<Object[]> bindParamsCaptor = ArgumentCaptor.forClass(Object[].class);
		verify(spyQueryExecutor).exec(any(ObjectResponse.class), bindParamsCaptor.capture());
		List<Object[]> objects = bindParamsCaptor.getAllValues();
		assertEquals(2, objects.size());
		// Mockito changes the List<Object[]> objects to a List<String> at runtime:
		assertEquals("value", objects.get(0));
		assertEquals("a param value", objects.get(1));

		assertEquals("{directiveOnQuery (uppercase: true) @testDirective(value:&value)}",
				getRegisteredGraphQLRequest("fullQuery3", String.class));

	}

	@Test
	void testInvoke_fullRequest_query_withParameterAsMap() {
		fail("not yet implemented");
	}

	@Test
	void testInvoke_fullRequest_mutation() {
		// Preparation

		// Go, go, go

		// Verification

		fail("Not yet implemented");
	}

	@Test
	void testInvoke_fullRequest_subscription() {
		// Preparation

		// Go, go, go

		// Verification

		fail("Not yet implemented");
	}

	@Test
	void testInvoke_fullRequest_withBindParameterValues() {
		// Preparation

		// Go, go, go

		// Verification

		fail("Not yet implemented");
	}

	private Object getRegisteredGraphQLRequest(String methodName, Class<?>... argumentTypes)
			throws NoSuchMethodException, SecurityException {
		Method method = GraphQLRepositoryTestCase.class.getMethod(methodName, argumentTypes);
		assertNotNull(method, "Looking for method '" + methodName + "'");

		@SuppressWarnings("rawtypes")
		RegisteredMethod registeredMethod = invocationHandler.registeredMethods.get(method);
		assertNotNull(registeredMethod, "Looking for registered method '" + methodName + "'");

		return registeredMethod.graphQLRequest.getGraphQLRequest();
	}
}
