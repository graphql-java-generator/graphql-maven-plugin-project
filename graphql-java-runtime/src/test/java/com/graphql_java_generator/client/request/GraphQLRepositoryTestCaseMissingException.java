package com.graphql_java_generator.client.request;

import java.util.List;

import com.graphql_java_generator.annotation.GraphQLRepository;
import com.graphql_java_generator.annotation.PartialRequest;
import com.graphql_java_generator.client.domain.allGraphQLCases.Character;

/**
 * This interface contains the test cases for the {@link GraphQLRepositoryInvocationHandlerTest} test class
 * 
 * @author etienne-sf
 */
@GraphQLRepository
public interface GraphQLRepositoryTestCaseMissingException {

	/** A method that throws no GraphQLRequestExecutionException */
	@PartialRequest(request = "{appearsIn name}")
	public List<Character> noGraphQLRequestExecutionException();

}
