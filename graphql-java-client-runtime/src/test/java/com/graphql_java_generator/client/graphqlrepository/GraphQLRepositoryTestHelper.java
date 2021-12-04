package com.graphql_java_generator.client.graphqlrepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Method;

import com.graphql_java_generator.client.graphqlrepository.GraphQLRepositoryInvocationHandler.RegisteredMethod;

public class GraphQLRepositoryTestHelper {

	public static Object getRegisteredGraphQLRequest(GraphQLRepositoryInvocationHandler<?> invocationHandler,
			Class<?> repoClass, String methodName, Class<?>... argumentTypes)
			throws NoSuchMethodException, SecurityException {
		Method method = repoClass.getMethod(methodName, argumentTypes);
		assertNotNull(method, "Looking for method '" + methodName + "'");

		@SuppressWarnings("rawtypes")
		RegisteredMethod registeredMethod = invocationHandler.registeredMethods.get(method);
		assertNotNull(registeredMethod, "Looking for registered method '" + methodName + "'");

		return registeredMethod.graphQLRequest.getGraphQLRequest();
	}
}
