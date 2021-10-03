package com.graphql_java_generator.client.graphqlrepository;

import java.util.Map;

import com.graphql_java_generator.domain.client.allGraphQLCases.CharacterInput;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This interface contains the test cases for the {@link GraphQLRepositoryInvocationHandler} tests
 * 
 * @author etienne-sf
 */
@GraphQLRepository
public interface GraphQLRepositoryTestCaseParameterWithMap {

	/** The return type of this method is not the good one */
	@PartialRequest(request = "{appearsIn name}")
	public Integer withOneOptionalParam(CharacterInput character, Map<String, Object> bindParameters)
			throws GraphQLRequestExecutionException;

}
