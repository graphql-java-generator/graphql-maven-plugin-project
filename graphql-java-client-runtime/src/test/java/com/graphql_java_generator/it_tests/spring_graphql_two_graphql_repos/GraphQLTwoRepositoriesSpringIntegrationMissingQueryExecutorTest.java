/**
 * 
 */
package com.graphql_java_generator.it_tests.spring_graphql_two_graphql_repos;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.graphql.client.GraphQlClient;

import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.graphqlrepository.EnableGraphQLRepositories;
import com.graphql_java_generator.domain.client.allGraphQLCases.MyQueryTypeExecutorMySchema;
import com.graphql_java_generator.domain.client.forum.QueryExecutorMySchema;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * This class contain tests that checks that Spring is able to properly load the GraphQL repositories, and automagically
 * create the dynamic proxy for the InvocationHandler.
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
public class GraphQLTwoRepositoriesSpringIntegrationMissingQueryExecutorTest {

	@Configuration
	@PropertySource("classpath:/application_two_graphql_servers.properties")
	@ComponentScan(basePackageClasses = { GraphqlClientUtils.class, MyQueryTypeExecutorMySchema.class,
			QueryExecutorMySchema.class })
	@EnableGraphQLRepositories({
			"com.graphql_java_generator.it_tests.spring_graphql_two_graphql_repos.ko_missing_queryExecutor" })
	public static class SpringConfigTwoServers {
		@Bean
		@Qualifier("MySchema")
		GraphQlClient graphQlClient() {
			return mock(GraphQlClient.class);
		}
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testMissingQueryExecutor() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException,
			NoSuchMethodException, SecurityException {
		BeanCreationException e = assertThrows(BeanCreationException.class,
				() -> new AnnotationConfigApplicationContext(SpringConfigTwoServers.class));
		assertTrue(e.getMessage().contains("one of your GraphQLRepository annotation didn't provide the QueryExecutor"),
				"The received message is: '" + e.getMessage() + "'");
	}
}
