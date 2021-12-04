package com.graphql_java_generator.client.graphqlrepository;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This interface contains the test cases for the {@link GraphQLRepositoryInvocationHandler} tests
 * 
 * @author etienne-sf
 */
@GraphQLRepository
public interface GraphQLRepositoryTestCaseParameterWithFloatParam {

	/**
	 * A query with a float parameter (GraphQL Float actually maps to Java Double, but Java Float and float should be
	 * accepted here)
	 */
	@PartialRequest(requestName = "issue82Float", request = "")
	Double withFloatParamAndReturnType(float f) throws GraphQLRequestExecutionException;

}
