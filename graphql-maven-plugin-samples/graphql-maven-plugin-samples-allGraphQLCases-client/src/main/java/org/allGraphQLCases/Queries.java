package org.allGraphQLCases;

import java.util.List;

import org.allGraphQLCases.client.Character;
import org.allGraphQLCases.client.CharacterInput;
import org.allGraphQLCases.client.Episode;
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

	// First part: queries
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

	// Second part: mutations

	Human createHuman(HumanInput human) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

}