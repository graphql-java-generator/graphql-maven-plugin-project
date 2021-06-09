package com.graphql_java_generator.client.request;

import com.graphql_java_generator.annotation.GraphQLRepository;
import com.graphql_java_generator.annotation.PartialRequest;
import com.graphql_java_generator.client.domain.allGraphQLCases.CharacterInput;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This interface contains the test cases for the {@link GraphQLRepositoryInvocationHandlerTest} test class
 * 
 * @author etienne-sf
 */
@GraphQLRepository
public interface GraphQLRepositoryTestCaseBadReturnType {

	/** The return type of this method is not the good one */
	@PartialRequest(request = "{appearsIn name}")
	public Integer withOneOptionalParam(CharacterInput character, Object... o) throws GraphQLRequestExecutionException;

}
