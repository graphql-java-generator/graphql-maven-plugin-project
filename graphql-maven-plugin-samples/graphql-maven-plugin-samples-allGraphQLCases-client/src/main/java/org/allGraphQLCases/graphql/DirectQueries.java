package org.allGraphQLCases.graphql;

import java.util.List;

import org.allGraphQLCases.Queries;
import org.allGraphQLCases.client.AnotherMutationType;
import org.allGraphQLCases.client.Character;
import org.allGraphQLCases.client.CharacterInput;
import org.allGraphQLCases.client.Episode;
import org.allGraphQLCases.client.Human;
import org.allGraphQLCases.client.HumanInput;
import org.allGraphQLCases.client.MyQueryType;

import com.graphql_java_generator.client.response.GraphQLRequestExecutionException;
import com.graphql_java_generator.client.response.GraphQLRequestPreparationException;

/**
 * This class implements the simplest way to call GraphQl queries, with the GraphQL Java Generator
 * 
 * @author EtienneSF
 */
public class DirectQueries implements Queries {

	final MyQueryType queryType;
	final AnotherMutationType mutationType;

	/**
	 * This constructor expects the URI of the GraphQL server. This constructor works only for http servers, not for
	 * https ones.<BR/>
	 * For example: https://my.server.com/graphql
	 * 
	 * @param graphqlEndpoint
	 *            the https URI for the GraphQL endpoint
	 */
	public DirectQueries(String graphqlEndpoint) {
		queryType = new MyQueryType(graphqlEndpoint);
		mutationType = new AnotherMutationType(graphqlEndpoint);
	}

	@Override
	public List<Character> withoutParameters()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withoutParameters("{appearsIn name }");
	}

	@Override
	public Character withOneOptionalParam(CharacterInput character)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withOneOptionalParam("{id name appearsIn friends {id name}}", character);
	}

	@Override
	public Character withOneMandatoryParam(CharacterInput character)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withOneMandatoryParam("{id name appearsIn friends {id name}}", character);
	}

	// @Override
	// public Character withOneMandatoryParamDefaultValue(CharacterInput character)
	// throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
	// // TODO Auto-generated method stub
	// return null;
	// }

	// @Override
	// public Droid withTwoMandatoryParamDefaultVal(DroidInput theHero, Integer index)
	// throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
	// // TODO Auto-generated method stub
	// return null;
	// }

	@Override
	public Character withEnum(Episode episode)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withEnum("{id name appearsIn friends {id name}}", episode);
	}

	@Override
	public List<Character> withList(String name, List<CharacterInput> friends)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withList("{id name appearsIn friends {id name}}", name, friends);
	}

	@Override
	public Human createHuman(HumanInput human)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationType.createHuman("{id name appearsIn friends {id name}}", human);
	}

	@Override
	public Character error(String errorLabel)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.error("{id name appearsIn friends {id name}}", errorLabel);
	}

}
