/**
 * 
 */
package org.allGraphQLCases.demo.impl;

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

import com.graphql_java_generator.client.graphqlrepository.GraphQLRepository;
import com.graphql_java_generator.client.graphqlrepository.PartialRequest;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * This interface demonstrate the use of GraphqlRepository
 * 
 * @author etienne-sf
 */
@GraphQLRepository
public interface PartialRequestGraphQLRepository {

	////////////////////////////////////////////////////////////////////////////
	// First part: partialQueries (based on the Star Wars use case)
	@PartialRequest(request = "{appearsIn name}")
	List<Character> withoutParameters() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	@PartialRequest(request = "{id name appearsIn friends {id name}}")
	Character withOneOptionalParam(CharacterInput character)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	@PartialRequest(request = "{id name appearsIn friends {id name}}")
	Character withOneMandatoryParam(CharacterInput character)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	@PartialRequest(request = "{id name appearsIn friends {id name}}")
	Character withEnum(Episode episode) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	@PartialRequest(request = "{id name appearsIn friends {id name}}")
	List<Character> withList(String name, List<CharacterInput> friends)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	@PartialRequest(request = "{id name appearsIn friends {id name}}")
	Character error(String errorLabel) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	////////////////////////////////////////////////////////////////////////////
	// Second part: partialQueries (based on the allGraphQLCases use case)

	@PartialRequest(request = "{ ... on WithID { id } name " //
			+ " forname(uppercase: ?uppercase, textToAppendToTheForname: ?textToAppendToTheForname) "
			+ " age nbComments " + " comments booleans aliases planets friends {id}" //
			+ " oneWithIdSubType {id name} "//
			+ " listWithIdSubTypes(nbItems: ?nbItemsWithId, date: ?date, dates: &dates, uppercaseName: ?uppercaseNameList, textToAppendToTheForname: ?textToAppendToTheFornameWithId) {name id}"
			+ " oneWithoutIdSubType(input: ?input) {name}"//
			+ " listWithoutIdSubTypes(nbItems: ?nbItemsWithoutId, input: ?inputList, textToAppendToTheForname: ?textToAppendToTheFornameWithoutId) {name}" //
			+ "}")
	public AllFieldCases allFieldCases(AllFieldCasesInput allFieldCasesInput, Boolean uppercase,
			String textToAppendToTheForname, long nbItemsWithId, Date date, List<Date> dates, Boolean uppercaseNameList,
			String textToAppendToTheFornameWithId, FieldParameterInput input, int nbItemsWithoutId,
			FieldParameterInput inputList, String textToAppendToTheFornameWithoutId)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	////////////////////////////////////////////////////////////////////////////
	// Third part: check of GraphQL types that are java keywords

	public _break aBreak(_extends test, String $if)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	////////////////////////////////////////////////////////////////////////////
	// Fourth part: a mutation

	Human createHuman(HumanInput human) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

}
