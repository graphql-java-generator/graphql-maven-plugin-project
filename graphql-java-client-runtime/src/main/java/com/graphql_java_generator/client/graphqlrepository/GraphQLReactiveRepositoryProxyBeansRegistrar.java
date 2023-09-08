package com.graphql_java_generator.client.graphqlrepository;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;

/**
 * Implementation of {@link ImportBeanDefinitionRegistrar}, that scans the given package for {@link GraphQLRepository}
 * annotated interfaces, and register dynamic proxies as beans.
 * 
 * @see https://stackoverflow.com/questions/39507736/dynamic-proxy-bean-with-autowiring-capability
 */
@Configuration
public class GraphQLReactiveRepositoryProxyBeansRegistrar
		extends AbstractGraphQLRepositoryProxyBeansRegistrar<GraphQLReactiveRepository> {

	public GraphQLReactiveRepositoryProxyBeansRegistrar() {
		super(GraphQLReactiveRepository.class);
	}
}
