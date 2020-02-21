package org.allGraphQLCases.graphql;

import java.util.Date;
import java.util.List;

import org.allGraphQLCases.Queries;
import org.allGraphQLCases.client.$break;
import org.allGraphQLCases.client.$extends;
import org.allGraphQLCases.client.AllFieldCases;
import org.allGraphQLCases.client.AllFieldCasesInput;
import org.allGraphQLCases.client.AnotherMutationType;
import org.allGraphQLCases.client.Character;
import org.allGraphQLCases.client.CharacterInput;
import org.allGraphQLCases.client.Episode;
import org.allGraphQLCases.client.FieldParameterInput;
import org.allGraphQLCases.client.Human;
import org.allGraphQLCases.client.HumanInput;
import org.allGraphQLCases.client.MyQueryType;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

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
	public AllFieldCases allFieldCases(AllFieldCasesInput allFieldCasesInput, Boolean uppercase,
			String textToAppendToTheForname, long nbItemsWithId, Date date, List<Date> dates, Boolean uppercaseNameList,
			String textToAppendToTheFornameWithId, FieldParameterInput input, int nbItemsWithoutId,
			FieldParameterInput inputList, String textToAppendToTheFornameWithoutId)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.allFieldCases("{id name forname age nbComments comments booleans aliases planets friends{id} "
				+ " oneWithIdSubType{id name} listWithIdSubTypes(nbItems:3, textToAppendToTheForname:\"textToAppendToTheFornameWithId\"){name id} "
				+ " oneWithoutIdSubType(input:{uppercase: true}){name} "
				+ " listWithoutIdSubTypes(nbItems:6, textToAppendToTheForname:\"textToAppendToTheFornameWithoutId\"){name}}",
				allFieldCasesInput);
	}

	@Override
	public Character error(String errorLabel)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.error("{id name appearsIn friends {id name}}", errorLabel);
	}

	@Override
	public $break aBreak($extends test, String $if)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.aBreak("{case(test: " + test.toString() + ")}");
	}

	@Override
	public Human createHuman(HumanInput human)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationType.createHuman("{id name appearsIn friends {id name}}", human);
	}

}
