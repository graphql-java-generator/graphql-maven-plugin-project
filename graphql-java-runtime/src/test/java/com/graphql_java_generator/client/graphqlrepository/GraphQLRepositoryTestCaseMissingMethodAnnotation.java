package com.graphql_java_generator.client.graphqlrepository;

import com.graphql_java_generator.domain.client.allGraphQLCases.Character;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This interface contains the test cases for the {@link GraphQLRepositoryInvocationHandler} tests
 * 
 * @author etienne-sf
 */
@GraphQLRepository
public interface GraphQLRepositoryTestCaseMissingMethodAnnotation {

	/** A method with no annotation should raise an error */
	public Character noAnnotation() throws GraphQLRequestExecutionException;

}
