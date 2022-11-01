/**
 * 
 */
package org.allGraphQLCases.graphqlrepository.non_spring;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import org.allGraphQLCases.client.CIP_Character_CIS;
import org.allGraphQLCases.client.CINP_CharacterInput_CINS;
import org.allGraphQLCases.client.CEP_Episode_CES;
import org.allGraphQLCases.client.CTP_Human_CTS;
import org.allGraphQLCases.client.CINP_HumanInput_CINS;
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

		CINP_CharacterInput_CINS characterInput = CINP_CharacterInput_CINS.builder().withName("the name")
				.withAppearsIn(Arrays.asList(CEP_Episode_CES.JEDI, CEP_Episode_CES.NEWHOPE)).withType("Human").build();
		CINP_HumanInput_CINS humanInput = CINP_HumanInput_CINS.builder().withName("the name")
				.withAppearsIn(Arrays.asList(CEP_Episode_CES.JEDI, CEP_Episode_CES.NEWHOPE)).build();

		//
		// Below is all you need to execute the GraphQL Request defined in the GraphQL Repository: graphQLRequests
		List<CIP_Character_CIS> response1 = graphQLRequests.withoutParameters();

		System.out.println(response1.toString());

		//
		// Below is all you need to execute the GraphQL Request defined in the GraphQL Repository: graphQLRequests
		CIP_Character_CIS response2 = graphQLRequests.withOneOptionalParam(characterInput);

		System.out.println("The query result is: " + response2.toString());

		//
		// Below is all you need to execute the GraphQL Request defined in the GraphQL Repository: graphQLRequests
		CTP_Human_CTS human = graphQLRequests.createHuman(humanInput).getCreateHuman();

		System.out.println("The mutation result is: " + human.toString());
	}

}
