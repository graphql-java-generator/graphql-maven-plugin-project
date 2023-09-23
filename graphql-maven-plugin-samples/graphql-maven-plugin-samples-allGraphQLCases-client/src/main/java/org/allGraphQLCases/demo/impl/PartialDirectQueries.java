package org.allGraphQLCases.demo.impl;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

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

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * This class implements the simplest way to call GraphQl partialQueries, with the GraphQL Java Generator
 * 
 * @author etienne-sf
 */
@Component
public class PartialDirectQueries implements PartialQueries {

	@Autowired
	MyQueryTypeExecutorAllGraphQLCases queryType;
	@Autowired
	AnotherMutationTypeExecutorAllGraphQLCases mutationType;

	@Override
	public List<CIP_Character_CIS> withoutParameters()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withoutParameters("{appearsIn name }");
	}

	@Override
	public CIP_Character_CIS withOneOptionalParam(CINP_CharacterInput_CINS character)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withOneOptionalParam("{id name appearsIn friends {id name}}", character);
	}

	@Override
	public CIP_Character_CIS withOneMandatoryParam(CINP_CharacterInput_CINS character)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withOneMandatoryParam("{id name appearsIn friends {id name}}", character);
	}

	// @Override
	// public CIP_Character_CIS withOneMandatoryParamDefaultValue(CINP_CharacterInput_CINS character)
	// throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
	// // TODO Auto-generated method stub
	// return null;
	// }

	// @Override
	// public CTP_Droid_CTS withTwoMandatoryParamDefaultVal(CINP_DroidInput_CINS theHero, Integer index)
	// throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
	// // TODO Auto-generated method stub
	// return null;
	// }

	@Override
	public CIP_Character_CIS withEnum(CEP_Episode_CES episode)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withEnum("{id name appearsIn friends {id name}}", episode);
	}

	@Override
	public List<CIP_Character_CIS> withList(String name, List<CINP_CharacterInput_CINS> friends)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.withList("{id name appearsIn friends {id name}}", name, friends);
	}

	@Override
	public CTP_AllFieldCases_CTS allFieldCases(CINP_AllFieldCasesInput_CINS allFieldCasesInput, Boolean uppercase,
			String textToAppendToTheForname, long nbItemsWithId, Date date, OffsetDateTime dateTime, List<Date> dates,
			Boolean uppercaseNameList, String textToAppendToTheFornameWithId, CINP_FieldParameterInput_CINS input,
			int nbItemsWithoutId, CINP_FieldParameterInput_CINS inputList, String textToAppendToTheFornameWithoutId)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.allFieldCases("{id name date dateTime dates " //
				// Parameter for fields are not managed yet)
				// + " forname(uppercase: ?uppercase, textToAppendToTheForname: ?textToAppendToTheForname) "
				+ " forname"//
				+ " age nbComments " + " comments booleans aliases planets friends {id}" //
				+ " oneWithIdSubType {id name} "//
				+ " listWithIdSubTypes(nbItems: ?nbItemsWithId, date: ?date, dates: &dates, uppercaseName: ?uppercaseNameList, textToAppendToTheForname: ?textToAppendToTheFornameWithId) {name id}"
				+ " oneWithoutIdSubType(input: ?input) {name}"//
				+ " listWithoutIdSubTypes(nbItems: ?nbItemsWithoutId, input: ?inputList, textToAppendToTheForname: ?textToAppendToTheFornameWithoutId) {name}" //
				+ "}", //
				allFieldCasesInput, //
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
	public CIP_Character_CIS error(String errorLabel)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.error("{id name appearsIn friends {id name}}", errorLabel);
	}

	@Override
	public CTP_break_CTS aBreak(CEP_extends_CES test, String $if)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.aBreak("{case(test: " + test.toString() + ")}");
	}

	@Override
	public CTP_Human_CTS createHuman(CINP_HumanInput_CINS human)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationType.createHuman("{id name appearsIn friends {id name}}", human);
	}

}
