/**
 * 
 */
package com.graphql_java_generator.client.graphqlrepository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import com.graphql_java_generator.client.GraphQLMutationExecutor;
import com.graphql_java_generator.client.GraphQLQueryExecutor;
import com.graphql_java_generator.client.GraphQLSubscriptionExecutor;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * The {@link GraphQLRepositoryInvocationHandler}s are created for each interface marked by the
 * {@link GraphQLRepository} annotation by the {@link GraphQLRepositoryProxyBeansRegistrar} Spring configuration class.
 * It register a bean factory for each such interface, and provided the right {@link InvocationHandler} for each.
 * 
 * @see https://stackoverflow.com/questions/39507736/dynamic-proxy-bean-with-autowiring-capability
 */
public class GraphQLRepositoryProxyBeanFactory {

	final GraphQLQueryExecutor queryExecutor;
	final GraphQLMutationExecutor mutationExecutor;
	final GraphQLSubscriptionExecutor subscriptionExecutor;

	public GraphQLRepositoryProxyBeanFactory(GraphQLQueryExecutor queryExecutor,
			GraphQLMutationExecutor mutationExecutor, GraphQLSubscriptionExecutor subscriptionExecutor) {
		this.queryExecutor = queryExecutor;
		this.mutationExecutor = mutationExecutor;
		this.subscriptionExecutor = subscriptionExecutor;
	}

	@SuppressWarnings("unchecked")
	public <R> R createGraphQLRepositoryInvocationHandler(ClassLoader classLoader, Class<R> clazz)
			throws GraphQLRequestPreparationException {
		GraphQLRepositoryInvocationHandler<?> invocationHandler = new GraphQLRepositoryInvocationHandler<R>(clazz,
				queryExecutor, mutationExecutor, subscriptionExecutor);
		return (R) Proxy.newProxyInstance(classLoader, new Class[] { clazz }, invocationHandler);
	}
}
