/**
 * 
 */
package com.graphql_java_generator.client.graphqlrepository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * The {@link GraphQLRepositoryInvocationHandler}s are created for each interface marked by the
 * {@link GraphQLRepository} annotation by the {@link GraphQLRepositoryProxyBeansRegistrar} Spring configuration class.
 * It register a bean factory for each such interface, and provided the right {@link InvocationHandler} for each.
 * 
 * @see https://stackoverflow.com/questions/39507736/dynamic-proxy-bean-with-autowiring-capability
 */
@Component
public class GraphQLRepositoryProxyBeanFactory {

	@Autowired
	ApplicationContext ctx;

	/**
	 * 
	 * @param <R>
	 *            The class of the bean to construct. This class is expected to be annotated by
	 *            {@link GraphQLRepository} or {@link GraphQLReactiveRepository}
	 * @param classLoader
	 * @param clazz
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	@SuppressWarnings("unchecked")
	public <R> R createGraphQLRepositoryInvocationHandler(ClassLoader classLoader, Class<R> clazz)
			throws GraphQLRequestPreparationException {
		GraphQLRepositoryInvocationHandler<?> invocationHandler = new GraphQLRepositoryInvocationHandler<R>(clazz,
				this.ctx);
		return (R) Proxy.newProxyInstance(classLoader, new Class[] { clazz }, invocationHandler);
	}
}
