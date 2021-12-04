package com.graphql_java_generator.client.graphqlrepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

class GraphQLRepositoryInvocationHandlerFirstTest extends AbstractGraphQLRepositoryInvocationHandlerTest {

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
	void testError_badReturnType() {
		// Go, go, go
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCaseBadReturnType>(
						GraphQLRepositoryTestCaseBadReturnType.class, spyQueryExecutor, spyMutationExecutor,
						spySubscriptionExecutor));

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
		ApplicationContext ctx = mock(ApplicationContext.class);
		when(ctx.getBeansOfType(anyObject())).thenReturn(values);

		// Go, go, go
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> new GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCaseBadExecutor>(
						GraphQLRepositoryTestCaseBadExecutor.class, ctx));

		// Verification
		assertTrue(e.getMessage().contains(
				"found no Spring Bean of type 'GraphQLQueryExecutor' in the same package as the provided QueryExecutor"),
				e.getMessage());
		assertTrue(
				e.getMessage().contains(
						"com.graphql_java_generator.client.graphqlrepository.GraphQLRepositoryTestCaseBadExecutor"),
				e.getMessage());
		assertTrue(e.getMessage().contains("com.graphql_java_generator.domain.client.starwars.QueryTypeExecutor"),
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
						GraphQLRepositoryTestCaseMissingException.class, spyQueryExecutor, spyMutationExecutor,
						spySubscriptionExecutor));

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
						GraphQLRepositoryTestCaseParameterWithMap.class, spyQueryExecutor, spyMutationExecutor,
						spySubscriptionExecutor));

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
						GraphQLRepositoryTestCaseParameterWithVararg.class, spyQueryExecutor, spyMutationExecutor,
						spySubscriptionExecutor));

		// Verification
		assertTrue(e.getMessage().contains("Map and vararg (Object[]) are not allowed."), e.getMessage());
	}

	@Test
	void testInvoke_floatInsteadDoubleParameterType()
			throws GraphQLRequestExecutionException, NoSuchMethodException, SecurityException {
		// Go, go, go
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRepositoryInvocationHandler<GraphQLRepositoryTestCaseParameterWithFloatParam>(
						GraphQLRepositoryTestCaseParameterWithFloatParam.class, spyQueryExecutor, spyMutationExecutor,
						spySubscriptionExecutor));

		// Verification
		assertTrue(e.getMessage().contains(
				"Float and float parameter types are not allowed. Please note that the GraphQL Float type maps to the Double java type."),
				e.getMessage());
	}
}
