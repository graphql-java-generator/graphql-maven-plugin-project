/**
 * 
 */
package com.graphql_java_generator.client.graphqlrepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.graphql_java_generator.client.GraphQLMutationExecutor;
import com.graphql_java_generator.client.GraphQLQueryExecutor;
import com.graphql_java_generator.client.GraphQLSubscriptionExecutor;

/**
 * GraphQLRepositoryProxyConfig is used to expose {@link GraphQLRepositoryInvocationHandler} and
 * {@link GraphQLRepositoryProxyBeanFactory} as beans.
 * 
 * @see https://stackoverflow.com/questions/39507736/dynamic-proxy-bean-with-autowiring-capability
 */
@Configuration
public class GraphQLRepositoryProxyConfig {

	@Autowired
	GraphQLQueryExecutor queryExecutor;

	@Autowired(required = false)
	GraphQLMutationExecutor mutationExecutor;

	@Autowired(required = false)
	GraphQLSubscriptionExecutor subscriptionExecutor;

	// @Bean
	// public GraphQLRepositoryInvocationHandler<T> graphQLRepositoryInvocationHandler() {
	// return new GraphQLRepositoryProxyConfig();
	// }

	@Bean(name = "graphQLRepositoryProxyBeanFactory")
	public GraphQLRepositoryProxyBeanFactory graphQLRepositoryProxyBeanFactory() {
		return new GraphQLRepositoryProxyBeanFactory(queryExecutor, mutationExecutor, subscriptionExecutor);
	}
}
