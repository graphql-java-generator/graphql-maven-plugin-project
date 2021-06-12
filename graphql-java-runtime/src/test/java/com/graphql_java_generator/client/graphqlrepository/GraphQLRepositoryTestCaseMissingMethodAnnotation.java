package com.graphql_java_generator.client.graphqlrepository;

import com.graphql_java_generator.client.domain.allGraphQLCases.Character;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This interface contains the test cases for the {@link GraphQLRepositoryInvocationHandlerTest} test class
 * 
 * @author etienne-sf
 */
@GraphQLRepository
public interface GraphQLRepositoryTestCaseMissingMethodAnnotation {

	/** A method with no annotation should raise an error */
	public Character noAnnotation() throws GraphQLRequestExecutionException;

}
