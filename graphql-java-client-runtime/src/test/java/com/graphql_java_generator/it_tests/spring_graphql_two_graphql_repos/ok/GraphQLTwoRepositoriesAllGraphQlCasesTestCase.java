package com.graphql_java_generator.it_tests.spring_graphql_two_graphql_repos.ok;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.SubscriptionCallback;
import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.client.graphqlrepository.GraphQLRepository;
import com.graphql_java_generator.client.graphqlrepository.PartialRequest;
import com.graphql_java_generator.domain.client.allGraphQLCases.Character;
import com.graphql_java_generator.domain.client.allGraphQLCases.CharacterInput;
import com.graphql_java_generator.domain.client.allGraphQLCases.Episode;
import com.graphql_java_generator.domain.client.allGraphQLCases.Human;
import com.graphql_java_generator.domain.client.allGraphQLCases.HumanInput;
import com.graphql_java_generator.domain.client.allGraphQLCases.MyQueryType;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.it_tests.spring_graphql_two_graphql_repos.GraphQLTwoRepositoriesSpringIntegrationTest;

/**
 * This interface contains the test cases for the {@link GraphQLTwoRepositoriesSpringIntegrationTest} test class
 * 
 * @author etienne-sf
 */
@GraphQLRepository(queryExecutor = MyQueryType.class)
public interface GraphQLTwoRepositoriesAllGraphQlCasesTestCase {

	/** a Query */
	@PartialRequest(request = "{appearsIn name}")
	public Character withOneOptionalParam(CharacterInput character) throws GraphQLRequestExecutionException;

	/** A Mutation */
	@PartialRequest(requestName = "createHuman", request = "{id name}", requestType = RequestType.mutation)
	public Human thisIsAMutation(HumanInput character) throws GraphQLRequestExecutionException;

	/** A Subscription */
	@PartialRequest(request = "{ id   appearsIn }", requestType = RequestType.subscription)
	public SubscriptionClient subscribeNewHumanForEpisode(SubscriptionCallback<Human> subscriptionCallback,
			Episode episode) throws GraphQLRequestExecutionException;
}
