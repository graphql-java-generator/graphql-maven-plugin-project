/**
 * 
 */
package com.graphql_java_generator.client.graphqlrepository.spring_graphql_one_repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import java.lang.reflect.Proxy;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.graphql_java_generator.client.GraphQLConfiguration;
import com.graphql_java_generator.client.graphqlrepository.EnableGraphQLRepositories;
import com.graphql_java_generator.client.graphqlrepository.GraphQLRepositoryInvocationHandler;
import com.graphql_java_generator.client.graphqlrepository.GraphQLRepositoryTestHelper;
import com.graphql_java_generator.client.graphqlrepository.spring_graphql_one_repo.GraphQLRepositorySpringIntegrationTest.SpringConfig;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.domain.client.allGraphQLCases.AnotherMutationTypeExecutor;
import com.graphql_java_generator.domain.client.allGraphQLCases.Character;
import com.graphql_java_generator.domain.client.allGraphQLCases.CharacterInput;
import com.graphql_java_generator.domain.client.allGraphQLCases.Human;
import com.graphql_java_generator.domain.client.allGraphQLCases.MyQueryTypeExecutor;
import com.graphql_java_generator.domain.client.allGraphQLCases.TheSubscriptionTypeExecutor;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.spring.client.GraphQLAutoConfiguration;

/**
 * This class contain tests that checks that Spring is able to properly load the GraphQL repositories, and automagically
 * create the dynamic proxy for the InvocationHandler.
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { SpringConfig.class })
public class GraphQLRepositorySpringIntegrationTest {

	@Configuration
	@PropertySource("classpath:/application_one_graphql_server.properties")
	@ComponentScan(basePackageClasses = { GraphQLConfiguration.class, MyQueryTypeExecutor.class })
	@Import(GraphQLAutoConfiguration.class)
	@EnableGraphQLRepositories({ "com.graphql_java_generator.client.graphqlrepository.spring_graphql_one_repo" })
	public static class SpringConfig {

	}

	@Autowired
	GraphQLRepositoryTestCase graphQLRepository;

	GraphQLRepositoryInvocationHandler<?> invocationHandler;

	// There seems to be issues with mock, probably because of the way the InvocationHandler calls the Executor, through
	// java reflection
	// So we use @Spy here, instead of @Mock
	// CAUTION: the changes the way to stub method. Use doReturn().when(spy).methodToStub() syntax
	@SpyBean
	MyQueryTypeExecutor spyQueryExecutor;
	@SpyBean
	AnotherMutationTypeExecutor spyMutationExecutor;
	@SpyBean
	TheSubscriptionTypeExecutor spySubscriptionExecutor;

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
