package org.allGraphQLCases.demo;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

import org.allGraphQLCases.client.AllFieldCases;
import org.allGraphQLCases.client.AllFieldCasesInput;
import org.allGraphQLCases.client.Character;
import org.allGraphQLCases.client.CharacterInput;
import org.allGraphQLCases.client.Episode;
import org.allGraphQLCases.client.FieldParameterInput;
import org.allGraphQLCases.client.Human;
import org.allGraphQLCases.client.HumanInput;
import org.allGraphQLCases.client._break;
import org.allGraphQLCases.client._extends;
import org.allGraphQLCases.demo.impl.PartialDirectQueries;
import org.allGraphQLCases.demo.impl.PartialPreparedQueries;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * These are samples of partialQueries that can be used with GraphQL against the allGraphQLCases GraphQL schema. There
 * are implemented in three ways in these classes: {@link PartialDirectQueries}, {@link PartialWithBuilder},
 * {@link PartialPreparedQueries}.<BR/>
 * You can see use of these partialQueries in the JUnit tests.<BR/>
 * These samples tests the execution of partial partialQueries, that is: calling for one of the query, mutation or
 * subscription that is defined in a Query, a Mutation or a Subscription object.<BR/>
 * For instance:
 * 
 * <PRE>
 * Character character = queryType.withEnum("{id name appearsIn homePlanet friends{name}}", "180");
 * </PRE>
 * 
 * @author etienne-sf
 *
 */
public interface PartialQueries {

	////////////////////////////////////////////////////////////////////////////
	// First part: partialQueries (based on the Star Wars use case)
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
	// Second part: partialQueries (based on the allGraphQLCases use case)

	public AllFieldCases allFieldCases(AllFieldCasesInput allFieldCasesInput, Boolean uppercase,
			String textToAppendToTheForname, long nbItemsWithId, Date date, OffsetDateTime dateTime, List<Date> dates,
			Boolean uppercaseNameList, String textToAppendToTheFornameWithId, FieldParameterInput input,
			int nbItemsWithoutId, FieldParameterInput inputList, String textToAppendToTheFornameWithoutId)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	////////////////////////////////////////////////////////////////////////////
	// Third part: check of GraphQL types that are java keywords

	public _break aBreak(_extends test, String $if)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	////////////////////////////////////////////////////////////////////////////
	// Fourth part: mutations

	Human createHuman(HumanInput human) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

}