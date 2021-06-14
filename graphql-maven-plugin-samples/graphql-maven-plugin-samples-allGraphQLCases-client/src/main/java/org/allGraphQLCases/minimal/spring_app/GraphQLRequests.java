/**
 * 
 */
package org.allGraphQLCases.minimal.spring_app;

import java.util.List;

import org.allGraphQLCases.client.AnotherMutationType;
import org.allGraphQLCases.client.Character;
import org.allGraphQLCases.client.CharacterInput;
import org.allGraphQLCases.client.HumanInput;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.graphqlrepository.BindParameter;
import com.graphql_java_generator.client.graphqlrepository.FullRequest;
import com.graphql_java_generator.client.graphqlrepository.GraphQLRepository;
import com.graphql_java_generator.client.graphqlrepository.PartialRequest;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This is a demo of the use of a {@link GraphQLRepository}. It contains the definition of GraphQL requests. Doing this
 * hides all the wiring to prepare and execute the GraphQL requests (query/mutation/subscription). Just declare a
 * {@link GraphQLRequests} autowired bean in your Spring component, and you can execute GraphQL requests. Take a look at
 * the {@link MinimalSpringApp} main class for that.
 * 
 * @author etienne-sf
 */
@GraphQLRepository
public interface GraphQLRequests {

	@PartialRequest(request = "{appearsIn name }")
	public List<Character> withoutParameters() throws GraphQLRequestExecutionException;

	@PartialRequest(request = "{id appearsIn name}")
	public Character withOneOptionalParam(CharacterInput character) throws GraphQLRequestExecutionException;

	@FullRequest(request = "mutation {createHuman (human: &input) {id name} }", requestType = RequestType.mutation)
	public AnotherMutationType createHuman(@BindParameter(name = "input") HumanInput input)
			throws GraphQLRequestExecutionException;
}
