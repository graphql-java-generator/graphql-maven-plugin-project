/**
 * 
 */
package org.allGraphQLCases.demo.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

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
import org.allGraphQLCases.client.util.AnotherMutationTypeExecutor;
import org.allGraphQLCases.client.util.MyQueryTypeExecutor;
import org.allGraphQLCases.demo.PartialQueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * This class implements the deprecated way to call GraphQl partialQueries, where all partialQueries are prepared before
 * execution. It's the deprecated way, as it is based on the ObjectResponse and the withQueryResponseDef Builder method.
 * This test is here to insure compatibility of this way of coding, with future evolution of the plugin.<BR/>
 * The advantages of preparing a request are:
 * <UL>
 * <LI>Performance: this avoid to build an {@link ObjectResponse} for each response. This {@link ObjectResponse} is
 * useful, to help control at runtime if a field has been queried or not. It allows to throw an exception when your code
 * tries to use a field that was not queried</LI>
 * <LI>Security: as all request have been prepared at startup, this make sure at startup that your partialQueries are
 * valid.</LI>
 * </UL>
 * 
 * @author etienne-sf
 */
@Component
public class PartialPreparedQueriesDeprecatedWay implements PartialQueries {

	@Autowired
	MyQueryTypeExecutor queryType;
	@Autowired
	AnotherMutationTypeExecutor mutationType;

	// PartialQueries
	ObjectResponse withoutParametersResponse;
	ObjectResponse withOneOptionalParamResponse;
	ObjectResponse withOneMandatoryParamResponse;
	ObjectResponse withOneMandatoryParamDefaultValueResponse;
	ObjectResponse withTwoMandatoryParamDefaultValResponse;
	ObjectResponse withEnumResponse;
	ObjectResponse withListResponse;
	ObjectResponse errorResponse;
	ObjectResponse aBreakResponse;
	ObjectResponse allFieldCasesResponse;

	// Mutations
	ObjectResponse createHumanResponse;

	/**
	 * Preparation of the GraphQL requests (queries, mutations)
	 * 
	 * @throws GraphQLRequestPreparationException
	 */
	@PostConstruct
	public void init() throws GraphQLRequestPreparationException {
		withoutParametersResponse = queryType.getWithoutParametersResponseBuilder()
				.withQueryResponseDef("{appearsIn name}").build();
		withOneOptionalParamResponse = queryType.getWithOneOptionalParamResponseBuilder()
				.withQueryResponseDef("{id name appearsIn friends {id name}}").build();
		withOneMandatoryParamResponse = queryType.getWithOneMandatoryParamResponseBuilder()
				.withQueryResponseDef("{id name appearsIn friends {id name}}").build();
		// withOneMandatoryParamDefaultValueResponse = queryType.getWithOneMandatoryParamDefaultValueResponseBuilder()
		// .withQueryResponseDef("{id name appearsIn friends {id name}}").build();
		// withTwoMandatoryParamDefaultValResponse = queryType.getWithTwoMandatoryParamDefaultValResponseBuilder()
		// .withQueryResponseDef("{id name appearsIn friends {id name}}").build();
		withEnumResponse = queryType.getWithEnumResponseBuilder()
				.withQueryResponseDef("{id name appearsIn friends {id name}}").build();
		withListResponse = queryType.getWithListResponseBuilder()
				.withQueryResponseDef("{id name appearsIn friends {id name}}").build();
		errorResponse = queryType.getErrorResponseBuilder()
				.withQueryResponseDef("{id name appearsIn friends {id name}}").build();
		aBreakResponse = queryType.getABreakResponseBuilder().withQueryResponseDef("{case(test: &test, if: ?if)}")
				.build();
		allFieldCasesResponse = queryType.getAllFieldCasesResponseBuilder().withQueryResponseDef("{id name " //
				// Parameter for fields are not managed yet)
				// + " forname(uppercase: ?uppercase, textToAppendToTheForname: ?textToAppendToTheForname) "
				+ " forname"//
				+ " age nbComments " + " comments booleans aliases planets friends {id}" //
				+ " oneWithIdSubType {id name} "//
				+ " listWithIdSubTypes(nbItems: ?nbItemsWithId, date: ?date, dates: &dates, uppercaseName: ?uppercaseNameList, textToAppendToTheForname: ?textToAppendToTheFornameWithId) {name id}"
				+ " oneWithoutIdSubType(input: ?input) {name}"//
				+ " listWithoutIdSubTypes(nbItems: ?nbItemsWithoutId, input: ?inputList, textToAppendToTheForname: ?textToAppendToTheFornameWithoutId) {name}" //
				+ "}").build();
	}

	@Override
	public List<Character> withoutParameters()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withoutParameters(withoutParametersResponse);
	}

	@Override
	public Character withOneOptionalParam(CharacterInput character)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withOneOptionalParam(withOneOptionalParamResponse, character);
	}

	@Override
	public Character withOneMandatoryParam(CharacterInput character)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withOneMandatoryParam(withOneMandatoryParamResponse, character);
	}

	@Override
	public Character withEnum(Episode episode)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withEnum(withEnumResponse, episode);
	}

	@Override
	public List<Character> withList(String name, List<CharacterInput> friends)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withList(withListResponse, name, friends);
	}

	@Override
	public Character error(String errorLabel)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.error(errorResponse, errorLabel);
	}

	@Override
	public AllFieldCases allFieldCases(AllFieldCasesInput allFieldCasesInput, Boolean uppercase,
			String textToAppendToTheForname, long nbItemsWithId, Date date, List<Date> dates, Boolean uppercaseNameList,
			String textToAppendToTheFornameWithId, FieldParameterInput input, int nbItemsWithoutId,
			FieldParameterInput inputList, String textToAppendToTheFornameWithoutId)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.allFieldCases(allFieldCasesResponse, allFieldCasesInput, //
				"uppercase", uppercase, "textToAppendToTheForname", textToAppendToTheForname, //
				"nbItemsWithId", nbItemsWithId, //
				"date", date, //
				"dates", dates, //
				"uppercaseNameList", uppercaseNameList, //
				"textToAppendToTheFornameWithId", textToAppendToTheFornameWithId, //
				"input", input, //
				"nbItemsWithoutId", nbItemsWithoutId, //
				"inputList", inputList, //
				"textToAppendToTheFornameWithoutId", textToAppendToTheFornameWithoutId);
	}

	@Override
	public _break aBreak(_extends test, String $if)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// aBreak {case(test: &test, if: ?if)}
		return queryType.aBreak(aBreakResponse, "test", test, "if", $if);
	}

	@Override
	public Human createHuman(HumanInput human)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationType.createHuman(createHumanResponse, human);
	}
}