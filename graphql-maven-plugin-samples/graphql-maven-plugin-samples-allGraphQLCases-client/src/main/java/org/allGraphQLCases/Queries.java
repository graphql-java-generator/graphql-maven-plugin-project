package org.allGraphQLCases;

import java.util.List;

import org.allGraphQLCases.client.AllFieldCases;
import org.allGraphQLCases.client.AllFieldCasesInput;
import org.allGraphQLCases.client.Character;
import org.allGraphQLCases.client.CharacterInput;
import org.allGraphQLCases.client.Episode;
import org.allGraphQLCases.client.FieldParameterInput;
import org.allGraphQLCases.client.Human;
import org.allGraphQLCases.client.HumanInput;
import org.allGraphQLCases.graphql.DirectQueries;
import org.allGraphQLCases.graphql.PreparedQueries;
import org.allGraphQLCases.graphql.WithBuilder;

import com.graphql_java_generator.client.response.GraphQLRequestExecutionException;
import com.graphql_java_generator.client.response.GraphQLRequestPreparationException;

/**
 * These are samples of queries that can be used with GraphQL against the Star Wars GraphQL schema. There are
 * implemented in three ways in these classes: {@link DirectQueries}, {@link WithBuilder}, {@link PreparedQueries}.<BR/>
 * You can see use of these queries in the JUnit tests.
 * 
 * @author EtienneSF
 *
 */
public interface Queries {

	////////////////////////////////////////////////////////////////////////////
	// First part: queries (based on the Star Wars use case)
	List<Character> withoutParameters() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	Character withOneOptionalParam(CharacterInput character)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	Character withOneMandatoryParam(CharacterInput character)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	// Character withOneMandatoryParamDefaultValue(CharacterInput character)
	// throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	// Droid withTwoMandatoryParamDefaultVal(DroidInput theHero, Integer index)
	// throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	Character withEnum(Episode episode) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	List<Character> withList(String name, List<CharacterInput> friends)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	Character error(String errorLabel) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	////////////////////////////////////////////////////////////////////////////
	// Second part: queries (based on the allGraphQLCases use case)

	public AllFieldCases allFieldCases(AllFieldCasesInput allFieldCasesInput, Boolean uppercase,
			String textToAppendToTheForname, int nbItemsWithId, Boolean uppercaseNameList,
			String textToAppendToTheFornameWithId, FieldParameterInput input, int nbItemsWithoutId,
			FieldParameterInput inputList, String textToAppendToTheFornameWithoutId)
			throws GraphQLRequestExecutionException;

	////////////////////////////////////////////////////////////////////////////
	// Third part: mutations

	Human createHuman(HumanInput human) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

}