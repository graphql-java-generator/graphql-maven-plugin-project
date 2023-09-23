/**
 * 
 */
package org.allGraphQLCases.demo.impl;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.allGraphQLCases.client.CEP_Episode_CES;
import org.allGraphQLCases.client.CEP_extends_CES;
import org.allGraphQLCases.client.CINP_AllFieldCasesInput_CINS;
import org.allGraphQLCases.client.CINP_CharacterInput_CINS;
import org.allGraphQLCases.client.CINP_FieldParameterInput_CINS;
import org.allGraphQLCases.client.CINP_HumanInput_CINS;
import org.allGraphQLCases.client.CIP_Character_CIS;
import org.allGraphQLCases.client.CTP_AllFieldCases_CTS;
import org.allGraphQLCases.client.CTP_Human_CTS;
import org.allGraphQLCases.client.CTP_break_CTS;
import org.allGraphQLCases.client.util.AnotherMutationTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.client.util.GraphQLRequestAllGraphQLCases;
import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
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
	MyQueryTypeExecutorAllGraphQLCases queryType;

	@Autowired
	AnotherMutationTypeExecutorAllGraphQLCases mutationType;

	// PartialQueries
	GraphQLRequestAllGraphQLCases withoutParametersRequest;
	GraphQLRequestAllGraphQLCases withOneOptionalParamRequest;
	GraphQLRequestAllGraphQLCases withOneMandatoryParamRequest;
	GraphQLRequestAllGraphQLCases withOneMandatoryParamDefaultValueRequest;
	GraphQLRequestAllGraphQLCases withTwoMandatoryParamDefaultValRequest;
	GraphQLRequestAllGraphQLCases withEnumRequest;
	GraphQLRequestAllGraphQLCases withListRequest;
	GraphQLRequestAllGraphQLCases errorRequest;
	GraphQLRequestAllGraphQLCases aBreakRequest;
	GraphQLRequestAllGraphQLCases allFieldCasesRequest;

	// Mutations
	GraphQLRequestAllGraphQLCases createHumanResponse;

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
		allFieldCasesRequest = queryType
				.getAllFieldCasesGraphQLRequest("{ ... on WithID { id } name date dateTime dates " //
						+ " forname(uppercase: ?uppercase, textToAppendToTheForname: ?textToAppendToTheForname) "
						+ " age nbComments " + " comments booleans aliases planets friends {id}" //
						+ " oneWithIdSubType {id name} "//
						+ " listWithIdSubTypes(nbItems: ?nbItemsWithId, date: ?date, dates: &dates, uppercaseName: ?uppercaseNameList, textToAppendToTheForname: ?textToAppendToTheFornameWithId) {name id}"
						+ " oneWithoutIdSubType(input: ?input) {name}"//
						+ " listWithoutIdSubTypes(nbItems: ?nbItemsWithoutId, input: ?inputList, textToAppendToTheForname: ?textToAppendToTheFornameWithoutId) {name}" //
						+ "}");
	}

	@Override
	public List<CIP_Character_CIS> withoutParameters()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withoutParameters(withoutParametersRequest);
	}

	@Override
	public CIP_Character_CIS withOneOptionalParam(CINP_CharacterInput_CINS character)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withOneOptionalParam(withOneOptionalParamRequest, character);
	}

	@Override
	public CIP_Character_CIS withOneMandatoryParam(CINP_CharacterInput_CINS character)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withOneMandatoryParam(withOneMandatoryParamRequest, character);
	}

	@Override
	public CIP_Character_CIS withEnum(CEP_Episode_CES episode)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withEnum(withEnumRequest, episode);
	}

	@Override
	public List<CIP_Character_CIS> withList(String name, List<CINP_CharacterInput_CINS> friends)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withList(withListRequest, name, friends);
	}

	@Override
	public CIP_Character_CIS error(String errorLabel)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.error(errorRequest, errorLabel);
	}

	@Override
	public CTP_AllFieldCases_CTS allFieldCases(CINP_AllFieldCasesInput_CINS allFieldCasesInput, Boolean uppercase,
			String textToAppendToTheForname, long nbItemsWithId, Date date, OffsetDateTime dateTime, List<Date> dates,
			Boolean uppercaseNameList, String textToAppendToTheFornameWithId, CINP_FieldParameterInput_CINS input,
			int nbItemsWithoutId, CINP_FieldParameterInput_CINS inputList, String textToAppendToTheFornameWithoutId)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.allFieldCases(allFieldCasesRequest, allFieldCasesInput, //
				"uppercase", uppercase, "textToAppendToTheForname", textToAppendToTheForname, //
				"nbItemsWithId", nbItemsWithId, //
				"date", date, //
				"dateTime", dateTime, //
				"dates", dates, //
				"uppercaseNameList", uppercaseNameList, //
				"textToAppendToTheFornameWithId", textToAppendToTheFornameWithId, //
				"input", input, //
				"nbItemsWithoutId", nbItemsWithoutId, //
				"inputList", inputList, //
				"textToAppendToTheFornameWithoutId", textToAppendToTheFornameWithoutId);
	}

	@Override
	public CTP_break_CTS aBreak(CEP_extends_CES test, String $if)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// aBreak {case(test: &test, if: ?if)}
		return queryType.aBreak(aBreakRequest, "test", test, "if", $if);
	}

	@Override
	public CTP_Human_CTS createHuman(CINP_HumanInput_CINS human)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationType.createHuman(createHumanResponse, human);
	}
}