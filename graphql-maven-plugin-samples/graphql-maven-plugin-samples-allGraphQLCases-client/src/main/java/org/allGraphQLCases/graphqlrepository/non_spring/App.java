/**
 * 
 */
package org.allGraphQLCases.graphqlrepository.non_spring;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import org.allGraphQLCases.client.Character;
import org.allGraphQLCases.client.CharacterInput;
import org.allGraphQLCases.client.Episode;
import org.allGraphQLCases.client.Human;
import org.allGraphQLCases.client.HumanInput;
import org.allGraphQLCases.client.util.AnotherMutationTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.client.util.TheSubscriptionTypeExecutorAllGraphQLCases;

import com.graphql_java_generator.client.graphqlrepository.GraphQLRepositoryInvocationHandler;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * This class shows the use of GraphQL Repositories, within a non Spring-app. <BR/>
 * Note: the code below is valid, but it won't work. The allGraphQLCases server is protected by OAuth, and the client
 * can only authenticate against an OAuth server when in a Spring application.
 * 
 * @author etienne-sf
 */
public class App {

	static String endpoint = "http://localhost:8180/my/updated/graphql/path";

	public static void main(String[] args) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		GraphQLRepositoryInvocationHandler<GraphQLRequests> invocationHandler = new GraphQLRepositoryInvocationHandler<GraphQLRequests>(
				GraphQLRequests.class, new MyQueryTypeExecutorAllGraphQLCases(endpoint),
				new AnotherMutationTypeExecutorAllGraphQLCases(endpoint),
				new TheSubscriptionTypeExecutorAllGraphQLCases(endpoint));
		GraphQLRequests graphQLRequests = (GraphQLRequests) Proxy.newProxyInstance(App.class.getClassLoader(),
				new Class[] { GraphQLRequests.class }, invocationHandler);

		CharacterInput characterInput = CharacterInput.builder().withName("the name")
				.withAppearsIn(Arrays.asList(Episode.JEDI, Episode.NEWHOPE)).withType("Human").build();
		HumanInput humanInput = HumanInput.builder().withName("the name")
				.withAppearsIn(Arrays.asList(Episode.JEDI, Episode.NEWHOPE)).build();

		//
		// Below is all you need to execute the GraphQL Request defined in the GraphQL Repository: graphQLRequests
		List<Character> response1 = graphQLRequests.withoutParameters();

		System.out.println(response1.toString());

		//
		// Below is all you need to execute the GraphQL Request defined in the GraphQL Repository: graphQLRequests
		Character response2 = graphQLRequests.withOneOptionalParam(characterInput);

		System.out.println("The query result is: " + response2.toString());

		//
		// Below is all you need to execute the GraphQL Request defined in the GraphQL Repository: graphQLRequests
		Human human = graphQLRequests.createHuman(humanInput).getCreateHuman();

		System.out.println("The mutation result is: " + human.toString());
	}

}
