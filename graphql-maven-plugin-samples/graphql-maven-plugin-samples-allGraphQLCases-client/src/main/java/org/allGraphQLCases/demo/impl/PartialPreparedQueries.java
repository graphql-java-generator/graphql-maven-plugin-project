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
import org.allGraphQLCases.client.util.GraphQLRequest;
import org.allGraphQLCases.client.util.MyQueryTypeExecutor;
import org.allGraphQLCases.demo.PartialQueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * This class implements the way to call GraphQl partialQueries, where all partialQueries are prepared before
 * execution.<BR/>
 * The advantages are:
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
public class PartialPreparedQueries implements PartialQueries {

	@Autowired
	MyQueryTypeExecutor queryType;

	@Autowired
	AnotherMutationTypeExecutor mutationType;

	// PartialQueries
	GraphQLRequest withoutParametersRequest;
	GraphQLRequest withOneOptionalParamRequest;
	GraphQLRequest withOneMandatoryParamRequest;
	GraphQLRequest withOneMandatoryParamDefaultValueRequest;
	GraphQLRequest withTwoMandatoryParamDefaultValRequest;
	GraphQLRequest withEnumRequest;
	GraphQLRequest withListRequest;
	GraphQLRequest errorRequest;
	GraphQLRequest aBreakRequest;
	GraphQLRequest allFieldCasesRequest;

	// Mutations
	GraphQLRequest createHumanResponse;

	/**
	 * This constructor expects the URI of the GraphQL server. This constructor works only for http servers, not for
	 * https ones.<BR/>
	 * For example: https://my.server.com/graphql
	 * 
	 * @param graphqlEndpoint
	 *            the https URI for the GraphQL endpoint
	 * @param sslContext
	 * @param hostnameVerifier
	 * @throws GraphQLRequestPreparationException
	 */
	@PostConstruct
	public void init() throws GraphQLRequestPreparationException {
		withoutParametersRequest = queryType.getWithoutParametersGraphQLRequest("{appearsIn name}");
		withOneOptionalParamRequest = queryType
				.getWithOneOptionalParamGraphQLRequest("{id name appearsIn friends {id name}}");
		withOneMandatoryParamRequest = queryType
				.getWithOneMandatoryParamGraphQLRequest("{id name appearsIn friends {id name}}");
		withEnumRequest = queryType.getWithEnumGraphQLRequest("{id name appearsIn friends {id name}}");
		withListRequest = queryType.getWithListGraphQLRequest("{id name appearsIn friends {id name}}");
		errorRequest = queryType.getErrorGraphQLRequest("{id name appearsIn friends {id name}}");
		aBreakRequest = queryType.getABreakGraphQLRequest("{case(test: &test, if: ?if)}");
		allFieldCasesRequest = queryType.getAllFieldCasesGraphQLRequest("{ ... on WithID { id } name " //
				+ " forname(uppercase: ?uppercase, textToAppendToTheForname: ?textToAppendToTheForname) "
				+ " age nbComments " + " comments booleans aliases planets friends {id}" //
				+ " oneWithIdSubType {id name} "//
				+ " listWithIdSubTypes(nbItems: ?nbItemsWithId, date: ?date, dates: &dates, uppercaseName: ?uppercaseNameList, textToAppendToTheForname: ?textToAppendToTheFornameWithId) {name id}"
				+ " oneWithoutIdSubType(input: ?input) {name}"//
				+ " listWithoutIdSubTypes(nbItems: ?nbItemsWithoutId, input: ?inputList, textToAppendToTheForname: ?textToAppendToTheFornameWithoutId) {name}" //
				+ "}");
	}

	@Override
	public List<Character> withoutParameters()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withoutParameters(withoutParametersRequest);
	}

	@Override
	public Character withOneOptionalParam(CharacterInput character)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withOneOptionalParam(withOneOptionalParamRequest, character);
	}

	@Override
	public Character withOneMandatoryParam(CharacterInput character)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withOneMandatoryParam(withOneMandatoryParamRequest, character);
	}

	@Override
	public Character withEnum(Episode episode)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withEnum(withEnumRequest, episode);
	}

	@Override
	public List<Character> withList(String name, List<CharacterInput> friends)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withList(withListRequest, name, friends);
	}

	@Override
	public Character error(String errorLabel)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.error(errorRequest, errorLabel);
	}

	@Override
	public AllFieldCases allFieldCases(AllFieldCasesInput allFieldCasesInput, Boolean uppercase,
			String textToAppendToTheForname, long nbItemsWithId, Date date, List<Date> dates, Boolean uppercaseNameList,
			String textToAppendToTheFornameWithId, FieldParameterInput input, int nbItemsWithoutId,
			FieldParameterInput inputList, String textToAppendToTheFornameWithoutId)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.allFieldCases(allFieldCasesRequest, allFieldCasesInput, //
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
		return queryType.aBreak(aBreakRequest, "test", test, "if", $if);
	}

	@Override
	public Human createHuman(HumanInput human)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationType.createHuman(createHumanResponse, human);
	}
}