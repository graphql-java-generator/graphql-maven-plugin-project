package org.allGraphQLCases.demo;

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
 * CIP_Character_CIS character = queryType.withEnum("{id name appearsIn homePlanet friends{name}}", "180");
 * </PRE>
 * 
 * @author etienne-sf
 *
 */
public interface PartialQueries {

	////////////////////////////////////////////////////////////////////////////
	// First part: partialQueries (based on the Star Wars use case)
	List<CIP_Character_CIS> withoutParameters() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	CIP_Character_CIS withOneOptionalParam(CINP_CharacterInput_CINS character)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	CIP_Character_CIS withOneMandatoryParam(CINP_CharacterInput_CINS character)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	// CIP_Character_CIS withOneMandatoryParamDefaultValue(CINP_CharacterInput_CINS character)
	// throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	// CTP_Droid_CTS withTwoMandatoryParamDefaultVal(CINP_DroidInput_CINS theHero, Integer index)
	// throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	CIP_Character_CIS withEnum(CEP_Episode_CES episode) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	List<CIP_Character_CIS> withList(String name, List<CINP_CharacterInput_CINS> friends)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	CIP_Character_CIS error(String errorLabel) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	////////////////////////////////////////////////////////////////////////////
	// Second part: partialQueries (based on the allGraphQLCases use case)

	public CTP_AllFieldCases_CTS allFieldCases(CINP_AllFieldCasesInput_CINS allFieldCasesInput, Boolean uppercase,
			String textToAppendToTheForname, long nbItemsWithId, Date date, OffsetDateTime dateTime, List<Date> dates,
			Boolean uppercaseNameList, String textToAppendToTheFornameWithId, CINP_FieldParameterInput_CINS input,
			int nbItemsWithoutId, CINP_FieldParameterInput_CINS inputList, String textToAppendToTheFornameWithoutId)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	////////////////////////////////////////////////////////////////////////////
	// Third part: check of GraphQL types that are java keywords

	public CTP_break_CTS aBreak(CEP_extends_CES test, String $if)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	////////////////////////////////////////////////////////////////////////////
	// Fourth part: mutations

	CTP_Human_CTS createHuman(CINP_HumanInput_CINS human) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

}