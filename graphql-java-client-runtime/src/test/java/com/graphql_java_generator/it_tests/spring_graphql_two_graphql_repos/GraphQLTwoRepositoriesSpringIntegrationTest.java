/**
 * 
 */
package com.graphql_java_generator.it_tests.spring_graphql_two_graphql_repos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Proxy;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.graphqlrepository.EnableGraphQLRepositories;
import com.graphql_java_generator.client.graphqlrepository.GraphQLRepositoryInvocationHandler;
import com.graphql_java_generator.client.graphqlrepository.GraphQLRepositoryTestHelper;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.domain.client.allGraphQLCases.AnotherMutationTypeExecutorMySchema;
import com.graphql_java_generator.domain.client.allGraphQLCases.Character;
import com.graphql_java_generator.domain.client.allGraphQLCases.CharacterInput;
import com.graphql_java_generator.domain.client.allGraphQLCases.Human;
import com.graphql_java_generator.domain.client.allGraphQLCases.MyQueryTypeExecutorMySchema;
import com.graphql_java_generator.domain.client.allGraphQLCases.TheSubscriptionTypeExecutorMySchema;
import com.graphql_java_generator.domain.client.forum.Member;
import com.graphql_java_generator.domain.client.forum.MutationExecutorMySchema;
import com.graphql_java_generator.domain.client.forum.QueryExecutorMySchema;
import com.graphql_java_generator.domain.client.forum.Topic;
import com.graphql_java_generator.domain.client.forum.TopicInput;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.it_tests.spring_graphql_two_graphql_repos.GraphQLTwoRepositoriesSpringIntegrationTest.SpringConfigTwoServers;
import com.graphql_java_generator.it_tests.spring_graphql_two_graphql_repos.ok.GraphQLTwoRepositoriesAllGraphQlCasesTestCase;
import com.graphql_java_generator.it_tests.spring_graphql_two_graphql_repos.ok.GraphQLTwoRepositoriesForumTestCase;

/**
 * This class contain tests that checks that Spring is able to properly load the GraphQL repositories, and automagically
 * create the dynamic proxy for the InvocationHandler.
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = { SpringConfigTwoServers.class }, webEnvironment = WebEnvironment.NONE)
public class GraphQLTwoRepositoriesSpringIntegrationTest {

	@Configuration
	@PropertySource("classpath:/application_two_graphql_servers.properties")
	@ComponentScan(basePackageClasses = { GraphqlClientUtils.class, MyQueryTypeExecutorMySchema.class,
			QueryExecutorMySchema.class })
	@EnableGraphQLRepositories({ "com.graphql_java_generator.it_tests.spring_graphql_two_graphql_repos.ok" })
	public static class SpringConfigTwoServers {
		@Bean
		@Qualifier("MySchema")
		GraphQlClient graphQlClientMySchema() {
			return mock(GraphQlClient.class);
		}
	}

	@Autowired
	GraphQLTwoRepositoriesAllGraphQlCasesTestCase graphQLAllGraphQLCasesRepo;

	@Autowired
	GraphQLTwoRepositoriesForumTestCase forumRepo;

	GraphQLRepositoryInvocationHandler<GraphQLTwoRepositoriesAllGraphQlCasesTestCase> invocationHandlerAllGraphQLCases;
	GraphQLRepositoryInvocationHandler<GraphQLTwoRepositoriesForumTestCase> invocationHandlerForum;

	// There seems to be issues with mock, probably because of the way the InvocationHandler calls the Executor, through
	// java reflection
	// So we use @Spy here, instead of @Mock
	// CAUTION: the changes the way to stub method. Use doReturn().when(spy).methodToStub() syntax
	@SuppressWarnings("removal")
	@SpyBean
	MyQueryTypeExecutorMySchema spyQueryExecutor; // allGraphQLCases
	@SuppressWarnings("removal")
	@SpyBean
	AnotherMutationTypeExecutorMySchema spyMutationExecutor;// allGraphQLCases
	@SuppressWarnings("removal")
	@SpyBean
	TheSubscriptionTypeExecutorMySchema spySubscriptionExecutor;// allGraphQLCases
	@SuppressWarnings("removal")
	@SpyBean
	MutationExecutorMySchema spyForumMutationExecutor;// Forum

	@SuppressWarnings("unchecked")
	@BeforeEach
	void setup() {
		invocationHandlerAllGraphQLCases = (GraphQLRepositoryInvocationHandler<GraphQLTwoRepositoriesAllGraphQlCasesTestCase>) Proxy
				.getInvocationHandler(graphQLAllGraphQLCasesRepo);
		invocationHandlerForum = (GraphQLRepositoryInvocationHandler<GraphQLTwoRepositoriesForumTestCase>) Proxy
				.getInvocationHandler(forumRepo);
	}

	@SuppressWarnings("unchecked")
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testInvoke_allGraphQLCases_withOneOptionalParam() throws GraphQLRequestExecutionException,
			GraphQLRequestPreparationException, NoSuchMethodException, SecurityException {
		// Preparation
		CharacterInput input = new CharacterInput();
		Human h = Human.builder().withId("1").withHomePlanet("home planet").withName(" a name ").build();
		doReturn(h).when(spyQueryExecutor).withOneOptionalParamWithBindValues(any(ObjectResponse.class),
				any(CharacterInput.class), any(Map.class));

		// Go, go, go
		Character verif = graphQLAllGraphQLCasesRepo.withOneOptionalParam(input);

		// Verification
		assertEquals(h, verif);
		assertEquals("{appearsIn name}", //
				GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(invocationHandlerAllGraphQLCases,
						GraphQLTwoRepositoriesAllGraphQlCasesTestCase.class, "withOneOptionalParam",
						CharacterInput.class));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	@SuppressWarnings("unchecked")
	void testInvoke_forum_mutation() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException,
			NoSuchMethodException, SecurityException {
		// Preparation
		TopicInput input = new TopicInput();
		Topic topic = Topic.builder().withAuthor(Member.builder().withId("45").build())
				.withContent("this is the content").withTitle("title").build();
		doReturn(topic).when(spyForumMutationExecutor).createTopicWithBindValues(any(ObjectResponse.class),
				any(TopicInput.class), any(Map.class));

		// Go, go, go
		Topic verif = forumRepo.createTopic(input);

		// Verification
		assertEquals(topic, verif);
		assertEquals(" { id title } ", //
				GraphQLRepositoryTestHelper.getRegisteredGraphQLRequest(invocationHandlerForum,
						GraphQLTwoRepositoriesForumTestCase.class, "createTopic", TopicInput.class));
	}
}
