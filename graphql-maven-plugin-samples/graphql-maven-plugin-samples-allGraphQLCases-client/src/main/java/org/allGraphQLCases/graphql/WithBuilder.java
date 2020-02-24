/**
 * 
 */
package org.allGraphQLCases.graphql;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.allGraphQLCases.Queries;
import org.allGraphQLCases.client.AllFieldCases;
import org.allGraphQLCases.client.AllFieldCasesInput;
import org.allGraphQLCases.client.AnotherMutationType;
import org.allGraphQLCases.client.Character;
import org.allGraphQLCases.client.CharacterInput;
import org.allGraphQLCases.client.Episode;
import org.allGraphQLCases.client.FieldParameterInput;
import org.allGraphQLCases.client.Human;
import org.allGraphQLCases.client.HumanInput;
import org.allGraphQLCases.client.MyQueryType;
import org.allGraphQLCases.client._break;
import org.allGraphQLCases.client._extends;

import com.graphql_java_generator.client.request.Builder;
import com.graphql_java_generator.client.request.InputParameter;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.customscalars.GraphQLScalarTypeDate;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * This class implements the away to call GraphQl queries, where all queries are prepared before execution.<BR/>
 * The advantages are:
 * <UL>
 * <LI>Performance: this avoid to build an {@link ObjectResponse} for each response. This {@link ObjectResponse} is
 * useful, to help control at runtime if a field has been queried or not. It allows to throw an exception when your code
 * tries to use a field that was not queried</LI>
 * <LI>Security: as all request have been prepared at startup, this make sure at startup that your queries are
 * valid.</LI>
 * </UL>
 * 
 * @author EtienneSF
 */
public class WithBuilder implements Queries {

	final MyQueryType queryType;
	final AnotherMutationType mutationType;

	GraphQLScalarTypeDate graphQLScalarTypeDate = new GraphQLScalarTypeDate();

	// Queries
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
	public WithBuilder(String graphqlEndpoint) throws GraphQLRequestPreparationException {
		queryType = new MyQueryType(graphqlEndpoint);
		mutationType = new AnotherMutationType(graphqlEndpoint);

		ObjectResponse characterFriends = new Builder(Character.class, "friends").withField("id").withField("name")
				.build();

		withoutParametersResponse = queryType.getWithoutParametersResponseBuilder().withField("appearsIn")
				.withField("name").build();
		withOneOptionalParamResponse = queryType.getWithOneOptionalParamResponseBuilder().withField("id")
				.withField("name").withField("appearsIn").withSubObject(characterFriends).build();
		withOneMandatoryParamResponse = queryType.getWithOneMandatoryParamResponseBuilder().withField("id")
				.withField("name").withField("appearsIn").withSubObject(characterFriends).build();
		// withOneMandatoryParamDefaultValueResponse = queryType.getWithOneMandatoryParamDefaultValueResponseBuilder()
		// .withField("id").withField("name").withField("appearsIn").withSubObject(characterFriends).build();
		// withTwoMandatoryParamDefaultValResponse = queryType.getWithTwoMandatoryParamDefaultValResponseBuilder()
		// .withField("id").withField("name").withField("appearsIn").withSubObject(characterFriends).build();
		withEnumResponse = queryType.getWithEnumResponseBuilder().withField("id").withField("name")
				.withField("appearsIn").withSubObject(characterFriends).build();
		withListResponse = queryType.getWithListResponseBuilder().withField("id").withField("name")
				.withField("appearsIn").withSubObject(characterFriends).build();
		errorResponse = queryType.getErrorResponseBuilder().build();

		// allFieldCasesResponse
		ObjectResponse oneWithIdSubTypeResponse = new Builder(AllFieldCases.class, "oneWithIdSubType").withField("id")
				.withField("name").build();
		ObjectResponse listWithIdSubTypesResponse = new Builder(AllFieldCases.class, "listWithIdSubTypes")
				.withInputParameter(InputParameter.newBindParameter("nbItems", "nbItemsWithId", false))
				.withInputParameter(InputParameter.newBindParameter("date", "date", false, graphQLScalarTypeDate.Date))
				.withInputParameter(
						InputParameter.newBindParameter("dates", "dates", false, graphQLScalarTypeDate.Date))
				.withInputParameter(InputParameter.newBindParameter("uppercaseName", "uppercaseNameList", false))
				.withInputParameter(InputParameter.newBindParameter("textToAppendToTheForname",
						"textToAppendToTheFornameWithId", false))
				.withField("id").withField("name").build();
		ObjectResponse oneWithoutIdSubTypeResponse = new Builder(AllFieldCases.class, "oneWithoutIdSubType")
				.withInputParameter(InputParameter.newBindParameter("input", "input", false)).withField("name").build();
		ObjectResponse listWithoutIdSubTypesResponse = new Builder(AllFieldCases.class, "listWithoutIdSubTypes")
				.withInputParameter(InputParameter.newBindParameter("nbItems", "nbItemsWithoutId", false))
				.withInputParameter(InputParameter.newBindParameter("input", "inputList", false)).withField("name")
				.withInputParameter(InputParameter.newBindParameter("textToAppendToTheForname",
						"textToAppendToTheFornameWithoutId", false))
				.build();
		ObjectResponse simpleFriendsResponse = new Builder(AllFieldCases.class, "friends").withField("id").build();
		//
		allFieldCasesResponse = queryType.getAllFieldCasesResponseBuilder().withField("id").withField("name")
				// Parameter for fields are not managed yet)
				// .withField("forname(uppercase: ?uppercase, textToAppendToTheForname: ?textToAppendToTheForname)")
				.withField("forname")//
				.withField("age").withField("nbComments").withField("comments").withField("booleans")
				.withField("aliases").withField("planets").withSubObject(simpleFriendsResponse)
				.withSubObject(oneWithIdSubTypeResponse).withSubObject(listWithIdSubTypesResponse)
				.withSubObject(oneWithoutIdSubTypeResponse).withSubObject(listWithoutIdSubTypesResponse).build();

		// aBreak {case(test: &test, if: ?if)}
		List<InputParameter> inputParams = new ArrayList<InputParameter>();
		inputParams.add(InputParameter.newBindParameter("test", "test", true));
		inputParams.add(InputParameter.newBindParameter("if", "if", false));
		aBreakResponse = queryType.getABreakResponseBuilder().withField("case", null, inputParams).build();
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
	public Human createHuman(HumanInput human)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationType.createHuman(createHumanResponse, human);
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
	public Character error(String errorLabel)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.error(errorResponse, errorLabel);
	}
}