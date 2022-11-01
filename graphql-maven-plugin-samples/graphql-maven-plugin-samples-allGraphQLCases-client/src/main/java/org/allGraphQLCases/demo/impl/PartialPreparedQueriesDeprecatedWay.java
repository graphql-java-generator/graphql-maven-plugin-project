/**
 * 
 */
package org.allGraphQLCases.demo.impl;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.allGraphQLCases.client.CTP_AllFieldCases_CTS;
import org.allGraphQLCases.client.CINP_AllFieldCasesInput_CINS;
import org.allGraphQLCases.client.CIP_Character_CIS;
import org.allGraphQLCases.client.CINP_CharacterInput_CINS;
import org.allGraphQLCases.client.CEP_Episode_CES;
import org.allGraphQLCases.client.CINP_FieldParameterInput_CINS;
import org.allGraphQLCases.client.CTP_Human_CTS;
import org.allGraphQLCases.client.CINP_HumanInput_CINS;
import org.allGraphQLCases.client.CTP_break_CTS;
import org.allGraphQLCases.client.CEP_extends_CES;
import org.allGraphQLCases.client.util.AnotherMutationTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
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
	MyQueryTypeExecutorAllGraphQLCases queryType;
	@Autowired
	AnotherMutationTypeExecutorAllGraphQLCases mutationType;

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
		allFieldCasesResponse = queryType.getAllFieldCasesResponseBuilder()
				.withQueryResponseDef("{id name date dateTime dates " //
						// Parameter for fields are not managed yet)
						// + " forname(uppercase: ?uppercase, textToAppendToTheForname: ?textToAppendToTheForname) "
						+ " forname"//
						+ " age nbComments " + " comments booleans aliases planets friends {id}" //
						+ " oneWithIdSubType {id name} "//
						+ " listWithIdSubTypes(nbItems: ?nbItemsWithId, date: ?date, dates: &dates, uppercaseName: ?uppercaseNameList, textToAppendToTheForname: ?textToAppendToTheFornameWithId) {name id}"
						+ " oneWithoutIdSubType(input: ?input) {name}"//
						+ " listWithoutIdSubTypes(nbItems: ?nbItemsWithoutId, input: ?inputList, textToAppendToTheForname: ?textToAppendToTheFornameWithoutId) {name}" //
						+ "}")
				.build();
	}

	@Override
	public List<CIP_Character_CIS> withoutParameters()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withoutParameters(withoutParametersResponse);
	}

	@Override
	public CIP_Character_CIS withOneOptionalParam(CINP_CharacterInput_CINS character)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withOneOptionalParam(withOneOptionalParamResponse, character);
	}

	@Override
	public CIP_Character_CIS withOneMandatoryParam(CINP_CharacterInput_CINS character)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withOneMandatoryParam(withOneMandatoryParamResponse, character);
	}

	@Override
	public CIP_Character_CIS withEnum(CEP_Episode_CES episode)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withEnum(withEnumResponse, episode);
	}

	@Override
	public List<CIP_Character_CIS> withList(String name, List<CINP_CharacterInput_CINS> friends)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withList(withListResponse, name, friends);
	}

	@Override
	public CIP_Character_CIS error(String errorLabel)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.error(errorResponse, errorLabel);
	}

	@Override
	public CTP_AllFieldCases_CTS allFieldCases(CINP_AllFieldCasesInput_CINS allFieldCasesInput, Boolean uppercase,
			String textToAppendToTheForname, long nbItemsWithId, Date date, OffsetDateTime dateTime, List<Date> dates,
			Boolean uppercaseNameList, String textToAppendToTheFornameWithId, CINP_FieldParameterInput_CINS input,
			int nbItemsWithoutId, CINP_FieldParameterInput_CINS inputList, String textToAppendToTheFornameWithoutId)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.allFieldCases(allFieldCasesResponse, allFieldCasesInput, //
				"uppercase", uppercase, "textToAppendToTheForname", textToAppendToTheForname, //
				"nbItemsWithId", nbItemsWithId, //
				"date", date, //
				"dates", dates, //
				"dateTime", dateTime, //
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
		return queryType.aBreak(aBreakResponse, "test", test, "if", $if);
	}

	@Override
	public CTP_Human_CTS createHuman(CINP_HumanInput_CINS human)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationType.createHuman(createHumanResponse, human);
	}
}