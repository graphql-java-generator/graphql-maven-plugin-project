package com.graphql_java_generator.it_tests.spring_graphql_two_graphql_repos.ok;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.graphqlrepository.GraphQLRepository;
import com.graphql_java_generator.client.graphqlrepository.PartialRequest;
import com.graphql_java_generator.domain.client.forum.QueryExecutorMySchema;
import com.graphql_java_generator.domain.client.forum.Topic;
import com.graphql_java_generator.domain.client.forum.TopicInput;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.it_tests.spring_graphql_two_graphql_repos.GraphQLTwoRepositoriesSpringIntegrationTest;

/**
 * This interface contains the test cases for the {@link GraphQLTwoRepositoriesSpringIntegrationTest} test class
 * 
 * @author etienne-sf
 */
@GraphQLRepository(queryExecutor = QueryExecutorMySchema.class)
public interface GraphQLTwoRepositoriesForumTestCase {

	/** A Mutation */
	@PartialRequest(request = " { id title } ", requestType = RequestType.mutation)
	public Topic createTopic(TopicInput input) throws GraphQLRequestExecutionException;

}
