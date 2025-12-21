/**
 * 
 */
package com.graphql_java_generator.it_tests.spring_graphql_one_graphql_repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Proxy;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.graphqlrepository.EnableGraphQLRepositories;
import com.graphql_java_generator.client.graphqlrepository.GraphQLRepositoryInvocationHandler;
import com.graphql_java_generator.client.graphqlrepository.GraphQLRepositoryTestHelper;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.domain.client.allGraphQLCases.AnotherMutationTypeExecutorAllGraphQLCases;
import com.graphql_java_generator.domain.client.allGraphQLCases.Character;
import com.graphql_java_generator.domain.client.allGraphQLCases.CharacterInput;
import com.graphql_java_generator.domain.client.allGraphQLCases.Human;
import com.graphql_java_generator.domain.client.allGraphQLCases.MyQueryTypeExecutorAllGraphQLCases;
import com.graphql_java_generator.domain.client.allGraphQLCases.TheSubscriptionTypeExecutorAllGraphQLCases;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.it_tests.spring_graphql_one_graphql_repo.GraphQLRepositorySpringIntegrationTest.SpringConfig;

/**
 * This class contain tests that checks that Spring is able to properly load the GraphQL repositories, and automagically
 * create the dynamic proxy for the InvocationHandler.
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { SpringConfig.class }, webEnvironment = WebEnvironment.NONE)
public class GraphQLRepositorySpringIntegrationTest {

	@Configuration
	@PropertySource("classpath:/application_one_graphql_server.properties")
	@ComponentScan(basePackageClasses = { GraphqlClientUtils.class, MyQueryTypeExecutorAllGraphQLCases.class })
	@EnableGraphQLRepositories({ "com.graphql_java_generator.it_tests.spring_graphql_one_graphql_repo" })
	public static class SpringConfig {
		@Bean
		@Qualifier("AllGraphQLCases")
		GraphQlClient graphQlClientMySchema() {
			return mock(GraphQlClient.class);
		}
	}

	@Autowired
	GraphQLRepositoryTestCase graphQLRepository;

	GraphQLRepositoryInvocationHandler<?> invocationHandler;

	// There seems to be issues with mock, probably because of the way the InvocationHandler calls the Executor, through
	// java reflection
	// So we use @Spy here, instead of @Mock
	// CAUTION: the changes the way to stub method. Use doReturn().when(spy).methodToStub() syntax
	@SuppressWarnings("removal")
	@SpyBean
	MyQueryTypeExecutorAllGraphQLCases spyQueryExecutor;
	@SuppressWarnings("removal")
	@SpyBean
	AnotherMutationTypeExecutorAllGraphQLCases spyMutationExecutor;
	@SuppressWarnings("removal")
	@SpyBean
	TheSubscriptionTypeExecutorAllGraphQLCases spySubscriptionExecutor;

	@BeforeEach
	void setup() {
		invocationHandler = (GraphQLRepositoryInvocationHandler<?>) Proxy.getInvocationHandler(graphQLRepository);
	}

	@SuppressWarnings("unchecked")
	@Test
	@Execution(ExecutionMode.CONCURRENT)
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
		assertEquals("{appearsIn name}", //
				GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(invocationHandler,
						GraphQLRepositoryTestCase.class, "withOneOptionalParam", CharacterInput.class));
	}

}
